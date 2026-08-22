#!/usr/bin/env python3

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from pathlib import Path
from typing import Sequence


ROOT = Path(__file__).resolve().parents[1]

SETTINGS_FILE = ROOT / "settings.json"
MAIN_PROJECT_FILE = ROOT / "versions" / "mainProject"
VERIFY_FRAMEWORK_SCRIPT = ROOT / "scripts" / "verify-framework.py"

DEFAULT_TARGET = "26.2"


class VerificationError(RuntimeError):
    pass


def print_header(title: str) -> None:
    line = "=" * 78
    print()
    print(line)
    print(title)
    print(line)
    print()


def command_text(args: Sequence[str | os.PathLike[str]]) -> str:
    return " ".join(str(arg) for arg in args)


def run(
    args: Sequence[str | os.PathLike[str]],
    *,
    check: bool = True,
    capture: bool = False,
) -> subprocess.CompletedProcess[str]:
    printable = command_text(args)

    print(f"> {printable}")

    result = subprocess.run(
        [str(arg) for arg in args],
        cwd=ROOT,
        check=False,
        text=True,
        encoding="utf-8",
        errors="replace",
        stdout=subprocess.PIPE if capture else None,
        stderr=subprocess.PIPE if capture else None,
    )

    if check and result.returncode != 0:
        if capture:
            if result.stdout:
                print(result.stdout, end="")
            if result.stderr:
                print(result.stderr, end="", file=sys.stderr)

        raise VerificationError(
            f"Command failed with exit code {result.returncode}: "
            f"{printable}"
        )

    return result


def git_output(*args: str) -> str:
    result = run(
        ["git", *args],
        capture=True,
    )

    return result.stdout


def git_exit_code(*args: str) -> int:
    result = run(
        ["git", *args],
        check=False,
        capture=True,
    )

    return result.returncode


def gradle_wrapper() -> Path:
    if os.name == "nt":
        wrapper = ROOT / "gradlew.bat"
    else:
        wrapper = ROOT / "gradlew"

    if not wrapper.exists():
        raise VerificationError(
            f"Gradle wrapper not found: {wrapper}"
        )

    return wrapper


def load_settings() -> dict:
    if not SETTINGS_FILE.exists():
        raise VerificationError(
            f"Missing settings file: {SETTINGS_FILE}"
        )

    return json.loads(
        SETTINGS_FILE.read_text(encoding="utf-8")
    )


def read_main_project() -> str:
    if not MAIN_PROJECT_FILE.exists():
        raise VerificationError(
            f"Missing main project file: {MAIN_PROJECT_FILE}"
        )

    return MAIN_PROJECT_FILE.read_text(
        encoding="utf-8"
    ).strip()


def require_main_project(expected: str) -> None:
    actual = read_main_project()

    if actual != expected:
        raise VerificationError(
            f"Expected mainProject={expected}, "
            f"but found mainProject={actual}"
        )


def require_clean_tracked_worktree() -> None:
    """
    Require all tracked files to be clean.

    Untracked files are intentionally allowed so this script can be tested
    before it is committed. setCoreVersion must never modify untracked files,
    and final round-trip validation only concerns tracked repository state.
    """

    unstaged = git_exit_code(
        "diff",
        "--quiet",
        "--",
    )

    staged = git_exit_code(
        "diff",
        "--cached",
        "--quiet",
        "--",
    )

    if unstaged == 0 and staged == 0:
        return

    print()
    print("Tracked working tree is not clean:")
    print()

    status = git_output(
        "status",
        "--short",
        "--untracked-files=no",
    )

    if status:
        print(status, end="")

    raise VerificationError(
        "Commit or restore tracked changes before running "
        "the round-trip verifier."
    )


def require_clean_roundtrip_result() -> None:
    """
    Verify the repository returned byte-for-byte to its tracked baseline.
    """

    print_header("Checking final Git state")

    run(
        [
            "git",
            "-c",
            "core.safecrlf=false",
            "diff",
            "--check",
        ]
    )

    unstaged = git_exit_code(
        "diff",
        "--quiet",
        "--",
    )

    staged = git_exit_code(
        "diff",
        "--cached",
        "--quiet",
        "--",
    )

    if unstaged == 0 and staged == 0:
        print("OK: tracked Git state returned to baseline.")
        return

    print()
    print("ERROR: round-trip left tracked changes.")
    print()

    status = git_output(
        "status",
        "--short",
        "--untracked-files=no",
    )

    if status:
        print(status, end="")

    print()
    print("Diff stat:")
    print()

    stat = git_output(
        "-c",
        "core.safecrlf=false",
        "diff",
        "--stat",
    )

    if stat:
        print(stat, end="")

    raise VerificationError(
        "Preprocessor round-trip is not reversible."
    )


def gradle_args(
    task: str,
    *,
    refresh_dependencies: bool,
    rerun_tasks: bool,
) -> list[str]:
    args = [
        str(gradle_wrapper()),
        task,
        "--stacktrace",
        "--no-parallel",
    ]

    if refresh_dependencies:
        args.append("--refresh-dependencies")

    if rerun_tasks:
        args.append("--rerun-tasks")

    return args


def run_gradle(
    task: str,
    *,
    refresh_dependencies: bool = False,
    rerun_tasks: bool = False,
) -> None:
    run(
        gradle_args(
            task,
            refresh_dependencies=refresh_dependencies,
            rerun_tasks=rerun_tasks,
        )
    )


def verify_framework() -> None:
    print_header("Checking DDS framework invariants")

    if not VERIFY_FRAMEWORK_SCRIPT.exists():
        raise VerificationError(
            f"Missing verifier: {VERIFY_FRAMEWORK_SCRIPT}"
        )

    run(
        [
            sys.executable,
            str(VERIFY_FRAMEWORK_SCRIPT),
        ]
    )


def build_all(
    *,
    refresh_dependencies: bool,
    rerun_tasks: bool,
) -> None:
    print_header("Building all supported Minecraft versions")

    run_gradle(
        "buildAndGather",
        refresh_dependencies=refresh_dependencies,
        rerun_tasks=rerun_tasks,
    )


def switch_core(
    version: str,
    *,
    rerun_tasks: bool,
) -> None:
    print_header(f"Switching core to {version}")

    run_gradle(
        f":{version}:setCoreVersion",
        rerun_tasks=rerun_tasks,
    )

    require_main_project(version)

    print(f"OK: mainProject={version}")


def attempt_recovery(
    canonical: str,
    *,
    rerun_tasks: bool,
) -> None:
    """
    Best-effort recovery after a failed forward/return switch.

    Never git-restore files automatically: if recovery fails, preserving the
    generated source tree is valuable for diagnosing the preprocessor bug.
    """

    try:
        current = read_main_project()
    except Exception:
        return

    if current == canonical:
        return

    print()
    print(
        f"Attempting best-effort recovery: "
        f"{current} -> {canonical}"
    )
    print()

    try:
        run_gradle(
            f":{canonical}:setCoreVersion",
            rerun_tasks=rerun_tasks,
        )

        if read_main_project() == canonical:
            print(
                f"Recovery succeeded: mainProject={canonical}"
            )
        else:
            print(
                "WARNING: recovery command completed but "
                "mainProject is still unexpected.",
                file=sys.stderr,
            )

    except Exception as exc:
        print(
            f"WARNING: automatic recovery failed: {exc}",
            file=sys.stderr,
        )
        print(
            "Generated source state has been preserved for diagnosis.",
            file=sys.stderr,
        )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Build every DDS version and verify that switching the "
            "preprocessor core to a modern unobfuscated version and back "
            "is Git-clean and reversible."
        )
    )

    parser.add_argument(
        "--target",
        default=DEFAULT_TARGET,
        help=(
            "Temporary core version used for the round-trip "
            f"(default: {DEFAULT_TARGET})"
        ),
    )

    parser.add_argument(
        "--skip-build",
        action="store_true",
        help=(
            "Skip buildAndGather and only run the core round-trip. "
            "Useful for quick preprocessor development."
        ),
    )

    parser.add_argument(
        "--refresh-dependencies",
        action="store_true",
        help=(
            "Pass --refresh-dependencies to buildAndGather."
        ),
    )

    parser.add_argument(
        "--rerun-tasks",
        action="store_true",
        help=(
            "Pass --rerun-tasks to Gradle invocations. "
            "Useful for strict release verification."
        ),
    )

    return parser.parse_args()


def main() -> int:
    args = parse_args()

    settings = load_settings()

    canonical = settings.get("preprocessMainProject")
    versions = settings.get("versions", [])

    if not isinstance(canonical, str) or not canonical:
        raise VerificationError(
            "settings.json does not define a valid coreVersion."
        )

    if canonical not in versions:
        raise VerificationError(
            f"Canonical core {canonical} is not present "
            "in settings.json versions."
        )

    if args.target not in versions:
        raise VerificationError(
            f"Round-trip target {args.target} is not present "
            "in settings.json versions."
        )

    if args.target == canonical:
        raise VerificationError(
            "Round-trip target must differ from canonical core."
        )

    print_header("DDS Preprocessor Round-Trip Verification")

    print(f"Repository : {ROOT}")
    print(f"Canonical  : {canonical}")
    print(f"Target     : {args.target}")
    print(
        f"Full build : {'no' if args.skip_build else 'yes'}"
    )
    print(
        f"Refresh    : "
        f"{'yes' if args.refresh_dependencies else 'no'}"
    )
    print(
        f"Rerun      : "
        f"{'yes' if args.rerun_tasks else 'no'}"
    )

    require_clean_tracked_worktree()
    require_main_project(canonical)

    switched_away = False

    try:
        verify_framework()

        if not args.skip_build:
            build_all(
                refresh_dependencies=args.refresh_dependencies,
                rerun_tasks=args.rerun_tasks,
            )

        switch_core(
            args.target,
            rerun_tasks=args.rerun_tasks,
        )

        switched_away = True

        switch_core(
            canonical,
            rerun_tasks=args.rerun_tasks,
        )

        switched_away = False

        require_main_project(canonical)
        require_clean_roundtrip_result()

    except Exception:
        if switched_away:
            attempt_recovery(
                canonical,
                rerun_tasks=args.rerun_tasks,
            )

        raise

    print_header("VERIFICATION PASSED")

    print("Framework invariants : PASS")

    if args.skip_build:
        print("All-version build    : SKIPPED")
    else:
        print("All-version build    : PASS")

    print(
        f"Core round-trip      : "
        f"{canonical} -> {args.target} -> {canonical}"
    )
    print("Round-trip result    : PASS")
    print("Git tracked diff     : CLEAN")
    print("git diff --check     : CLEAN")
    print()
    print("Preprocessor infrastructure verification completed successfully.")

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except KeyboardInterrupt:
        print(
            "\nVerification interrupted by user.",
            file=sys.stderr,
        )
        raise SystemExit(130)
    except VerificationError as exc:
        print()
        print(f"VERIFICATION FAILED: {exc}", file=sys.stderr)
        raise SystemExit(1)