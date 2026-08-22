#!/usr/bin/env python3

import argparse
import json
import mimetypes
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid
from pathlib import Path


API_BASE = "https://api.modrinth.com/v2"
USER_AGENT = "x-oOvOo-x/Carpet-DDS-Addition GitHub-Actions"


def request_json(url, token=None, method="GET", data=None, headers=None):
    request_headers = {
        "User-Agent": USER_AGENT,
    }
    if token:
        request_headers["Authorization"] = token
    if headers:
        request_headers.update(headers)

    req = urllib.request.Request(
        url,
        data=data,
        headers=request_headers,
        method=method,
    )

    try:
        with urllib.request.urlopen(req, timeout=120) as response:
            body = response.read()
            return response.status, json.loads(body.decode("utf-8")) if body else None
    except urllib.error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        try:
            parsed = json.loads(body)
        except json.JSONDecodeError:
            parsed = {"description": body}
        return exc.code, parsed


def resolve_project_id(id_or_slug, token):
    value = urllib.parse.quote(id_or_slug, safe="")
    status, data = request_json(f"{API_BASE}/project/{value}", token)

    if status != 200 or not isinstance(data, dict) or not data.get("id"):
        description = data.get("description", data) if isinstance(data, dict) else data
        raise RuntimeError(
            f"Could not resolve Modrinth project {id_or_slug!r}: "
            f"HTTP {status}: {description}"
        )

    return data["id"]


def multipart_body(metadata, jar_path):
    boundary = "----CarpetDDSAddition" + uuid.uuid4().hex
    newline = b"\r\n"
    chunks = [
        f"--{boundary}".encode(),
        b'Content-Disposition: form-data; name="data"',
        b"Content-Type: application/json",
        b"",
        json.dumps(metadata, separators=(",", ":")).encode("utf-8"),
        f"--{boundary}".encode(),
        b'Content-Disposition: form-data; name="file"; filename="' + jar_path.name.encode("utf-8") + b'"',
        f"Content-Type: {mimetypes.guess_type(jar_path.name)[0] or 'application/java-archive'}".encode(),
        b"",
        jar_path.read_bytes(),
        f"--{boundary}--".encode(),
        b"",
    ]
    return newline.join(chunks), f"multipart/form-data; boundary={boundary}"


def find_jar(release_dir, mod_version, build_target):
    pattern = f"*-v{mod_version}-mc{build_target}.jar"
    matches = [
        path for path in release_dir.glob(pattern)
        if "-sources" not in path.name and "-dev" not in path.name
    ]

    if len(matches) != 1:
        names = ", ".join(path.name for path in matches) or "none"
        raise RuntimeError(
            f"Expected exactly one release jar for {build_target} using "
            f"pattern {pattern!r}, found: {names}"
        )

    return matches[0]


def version_exists(project_slug, version_number, token):
    project_part = urllib.parse.quote(project_slug, safe="")
    version_part = urllib.parse.quote(version_number, safe="")
    url = f"{API_BASE}/project/{project_part}/version/{version_part}"

    status, data = request_json(url, token)
    if status == 200:
        return True
    if status == 404:
        return False

    description = data.get("description", data) if isinstance(data, dict) else data
    raise RuntimeError(
        f"Could not check existing Modrinth version {version_number}: "
        f"HTTP {status}: {description}"
    )


def publish(
    project_slug,
    project_id,
    carpet_project_id,
    token,
    mod_version,
    build_target,
    game_versions,
    jar_path,
    changelog,
):
    version_number = f"{mod_version}+mc{build_target}"

    if version_exists(project_slug, version_number, token):
        print(f"[skip] {version_number} already exists on Modrinth")
        return

    metadata = {
        "name": f"{mod_version} - Minecraft {build_target}",
        "version_number": version_number,
        "changelog": changelog,
        "dependencies": [
            {
                "project_id": carpet_project_id,
                "dependency_type": "required",
            }
        ],
        "game_versions": game_versions,
        "version_type": "release",
        "loaders": ["fabric"],
        "featured": True,
        "status": "listed",
        "project_id": project_id,
        "file_parts": ["file"],
        "primary_file": "file",
    }

    body, content_type = multipart_body(metadata, jar_path)
    status, data = request_json(
        f"{API_BASE}/version",
        token,
        method="POST",
        data=body,
        headers={"Content-Type": content_type},
    )

    if status != 200:
        description = data.get("description", data) if isinstance(data, dict) else data
        raise RuntimeError(
            f"Failed to publish {version_number}: HTTP {status}: {description}"
        )

    version_id = data.get("id", "unknown") if isinstance(data, dict) else "unknown"
    print(f"[published] {version_number} -> {version_id}")


def main():
    parser = argparse.ArgumentParser(
        description="Publish Carpet DDS Addition release jars to Modrinth."
    )
    parser.add_argument("--project", required=True, help="Modrinth project slug or ID")
    parser.add_argument("--version", required=True, help="DDS mod version, e.g. 2.1.0")
    parser.add_argument("--release-dir", required=True, type=Path)
    parser.add_argument("--config", required=True, type=Path)
    parser.add_argument("--changelog-file", type=Path)
    args = parser.parse_args()

    token = os.environ.get("MODRINTH_TOKEN")
    if not token:
        raise RuntimeError("MODRINTH_TOKEN is not set")

    if not args.release_dir.is_dir():
        raise RuntimeError(f"Release directory does not exist: {args.release_dir}")

    project_id = resolve_project_id(args.project, token)
    carpet_project_id = resolve_project_id("carpet", token)

    print(f"[project] {args.project} -> {project_id}")
    print(f"[dependency] carpet -> {carpet_project_id}")

    config = json.loads(args.config.read_text(encoding="utf-8"))

    changelog = ""
    if args.changelog_file and args.changelog_file.exists():
        changelog = args.changelog_file.read_text(encoding="utf-8").strip()
    if not changelog:
        changelog = f"Carpet DDS Addition {args.version}"

    for build_target, game_versions in config.items():
        jar_path = find_jar(args.release_dir, args.version, build_target)
        print(
            f"[prepare] {build_target}: {jar_path.name} "
            f"-> {', '.join(game_versions)}"
        )
        publish(
            args.project,
            project_id,
            carpet_project_id,
            token,
            args.version,
            build_target,
            game_versions,
            jar_path,
            changelog,
        )


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)
