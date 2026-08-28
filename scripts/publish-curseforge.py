#!/usr/bin/env python3

import argparse
import json
import mimetypes
import os
import sys
import urllib.error
import urllib.request
import uuid
from pathlib import Path

API_BASE = "https://minecraft.curseforge.com"
USER_AGENT = "x-oOvOo-x/Carpet-DDS-Addition GitHub-Actions"
CARPET_PROJECT_ID = "349239"


def request_json(url, token, method="GET", data=None, headers=None):
    req_headers = {"User-Agent": USER_AGENT, "X-Api-Token": token}
    if headers: req_headers.update(headers)
    req = urllib.request.Request(url, data=data, headers=req_headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=120) as response:
            raw = response.read().decode("utf-8", errors="replace")
            return response.status, json.loads(raw) if raw else None, raw
    except urllib.error.HTTPError as exc:
        raw = exc.read().decode("utf-8", errors="replace")
        try: data = json.loads(raw)
        except json.JSONDecodeError: data = None
        return exc.code, data, raw


def multipart_body(metadata, jar_path):
    boundary = "----CarpetDDSAddition" + uuid.uuid4().hex
    nl = b"\r\n"
    chunks = [
        f"--{boundary}".encode(), b'Content-Disposition: form-data; name="metadata"', b"Content-Type: application/json", b"",
        json.dumps(metadata, separators=(",", ":")).encode("utf-8"),
        f"--{boundary}".encode(), b'Content-Disposition: form-data; name="file"; filename="' + jar_path.name.encode("utf-8") + b'"',
        f"Content-Type: {mimetypes.guess_type(jar_path.name)[0] or 'application/java-archive'}".encode(), b"", jar_path.read_bytes(),
        f"--{boundary}--".encode(), b"",
    ]
    return nl.join(chunks), f"multipart/form-data; boundary={boundary}"


def find_jar(release_dir, mod_version, target):
    pattern = f"*-v{mod_version}-mc{target}.jar"
    matches = [p for p in release_dir.glob(pattern) if "-sources" not in p.name and "-dev" not in p.name]
    if len(matches) != 1: raise RuntimeError(f"Expected one release jar for {target} ({pattern}), found: {', '.join(p.name for p in matches) or 'none'}")
    return matches[0]


def game_version_ids(token, names):
    status, data, raw = request_json(f"{API_BASE}/api/game/versions", token)
    if status != 200 or not isinstance(data, list): raise RuntimeError(f"Could not read CurseForge game versions: HTTP {status}: {raw}")
    by_name = {}
    for item in data:
        if isinstance(item, dict) and item.get("name") and item.get("id") is not None: by_name.setdefault(item["name"], item["id"])
    missing = [name for name in names if name not in by_name]
    if missing: raise RuntimeError(f"CurseForge game-version tags not found: {', '.join(missing)}")
    return [by_name[name] for name in names]


def duplicate_response(status, raw):
    text = raw.lower()
    return status in (400, 409) and any(term in text for term in ("already exists", "duplicate", "same file"))


def publish(project, token, mod_version, target, jar_path, changelog, tag_ids):
    metadata = {
        "displayName": f"Carpet DDS Addition {mod_version} - Minecraft {target}",
        "changelog": changelog,
        "changelogType": "markdown",
        "gameVersions": tag_ids,
        "releaseType": "release",
        "isMarkedForManualRelease": False,
        "relations": {"projects": [{"slug": "carpet", "projectID": CARPET_PROJECT_ID, "type": "requiredDependency"}]},
    }
    body, content_type = multipart_body(metadata, jar_path)
    status, data, raw = request_json(f"{API_BASE}/api/projects/{project}/upload-file", token, "POST", body, {"Content-Type": content_type})
    if 200 <= status < 300:
        print(f"[published] {target}: {jar_path.name} -> {data.get('id', 'unknown') if isinstance(data, dict) else 'unknown'}")
        return
    if duplicate_response(status, raw):
        print(f"[skip] {target}: CurseForge reports an existing/duplicate file")
        return
    raise RuntimeError(f"Failed to publish {target}: HTTP {status}: {raw}")


def main():
    parser = argparse.ArgumentParser(description="Publish Carpet DDS Addition release jars to CurseForge.")
    parser.add_argument("--project", required=True, help="CurseForge numeric project ID")
    parser.add_argument("--version", required=True, help="DDS mod version, e.g. 1.6.0")
    parser.add_argument("--release-dir", required=True, type=Path)
    parser.add_argument("--config", required=True, type=Path, help="Target -> Minecraft version-name mapping JSON")
    parser.add_argument("--changelog-file", type=Path)
    args = parser.parse_args()
    token = os.environ.get("CURSEFORGE_TOKEN")
    if not token: raise RuntimeError("CURSEFORGE_TOKEN is not set")
    if not args.project.isdigit(): raise RuntimeError("--project must be a numeric CurseForge project ID")
    if not args.release_dir.is_dir(): raise RuntimeError(f"Release directory does not exist: {args.release_dir}")
    config = json.loads(args.config.read_text(encoding="utf-8"))
    changelog = args.changelog_file.read_text(encoding="utf-8").strip() if args.changelog_file and args.changelog_file.exists() else ""
    if not changelog: changelog = f"Carpet DDS Addition {args.version}"
    all_names = list(dict.fromkeys([name for versions in config.values() for name in versions] + ["Fabric", "Server"]))
    ids = dict(zip(all_names, game_version_ids(token, all_names)))
    for target, versions in config.items():
        jar = find_jar(args.release_dir, args.version, target)
        names = list(dict.fromkeys(list(versions) + ["Fabric", "Server"]))
        print(f"[prepare] {target}: {jar.name} -> {', '.join(names)}")
        publish(args.project, token, args.version, target, jar, changelog, [ids[name] for name in names])


if __name__ == "__main__":
    try: main()
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)
