#!/usr/bin/env python3
"""Fix verify-framework.py initialization order for Phase 6B."""

from pathlib import Path
import sys

ROOT = Path.cwd()
VERIFIER = ROOT / "scripts/verify-framework.py"

EARLY_ANCHOR = 'settings_gradle_file = ROOT / "settings.gradle"\ncommon_gradle_file = ROOT / "common.gradle"\nroot_build_gradle_file = ROOT / "build.gradle"\n\n\n'
EARLY_REPLACEMENT = 'settings_gradle_file = ROOT / "settings.gradle"\ncommon_gradle_file = ROOT / "common.gradle"\nroot_build_gradle_file = ROOT / "build.gradle"\n\njava_root = (\n    ROOT\n    / "src"\n    / "main"\n    / "java"\n)\n\n\n'
LATE_BLOCK = 'java_root = (\n    ROOT\n    / "src"\n    / "main"\n    / "java"\n)\n\n\n'


def fail(message: str) -> None:
    print(f"VERIFY-FRAMEWORK ORDER FIX FAILED: {message}", file=sys.stderr)
    raise SystemExit(1)


if not (ROOT / ".git").is_dir():
    fail("Run from the Carpet-DDS-Addition repository root.")

if not VERIFIER.is_file():
    fail("Missing scripts/verify-framework.py")

text = VERIFIER.read_text(encoding="utf-8")

if text.count(EARLY_ANCHOR) != 1:
    fail(
        "Could not uniquely locate the top-level Gradle path constants; "
        f"found {text.count(EARLY_ANCHOR)} matches"
    )

if text.count(LATE_BLOCK) != 1:
    fail(
        "Could not uniquely locate the late java_root definition; "
        f"found {text.count(LATE_BLOCK)} matches"
    )

text = text.replace(
    EARLY_ANCHOR,
    EARLY_REPLACEMENT,
    1,
)

last_index = text.rfind(LATE_BLOCK)

if last_index < 0:
    fail("Late java_root definition disappeared unexpectedly")

text = (
    text[:last_index]
    + text[last_index + len(LATE_BLOCK):]
)

if text.count("java_root = (") != 1:
    fail(
        "Expected exactly one java_root definition after repair; found "
        f"{text.count('java_root = (')}"
    )

canonical_section = text.find(
    "# Canonical network contract tests"
)
java_root_definition = text.find(
    "java_root = ("
)

if java_root_definition < 0 or canonical_section < 0:
    fail("Could not validate repaired section ordering")

if java_root_definition > canonical_section:
    fail(
        "java_root is still defined after the canonical network test section"
    )

compile(
    text,
    str(VERIFIER),
    "exec",
)

VERIFIER.write_text(
    text,
    encoding="utf-8",
    newline="\n",
)

print("verify-framework.py initialization order repaired.")
print()
print("java_root is now defined before Phase 6A/6B network checks.")
print("No Gradle, Java, protocol, or runtime code was changed.")
print()
print("Run:")
print(r"  python .\scripts\verify-framework.py")
print(r"  python .\scripts\verify-preprocessor-roundtrip.py")
print(r"  .\gradlew.bat testNetworkContracts")
