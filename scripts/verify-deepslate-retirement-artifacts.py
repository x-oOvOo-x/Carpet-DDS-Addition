#!/usr/bin/env python3
"""Check the deepslate stonecutter rule retirement in built runtime JARs."""

import json
from pathlib import Path
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
VERSIONS = {"1.21.11": True, "26.1.2": False, "26.2": False, "26.3": False}
COMPAT_CLASS = (
    "carpetddsaddition/feature/stonecuttercutsdeepslate/"
    "compat/StonecutterCutsDeepslateCompat.class"
)


def audit(root, directory):
    rules = json.loads((root / "src/main/rules/rules.json").read_text(encoding="utf-8"))["rules"]
    rule = next(rule for rule in rules if rule["name"] == "stonecutterCutsDeepslate")
    if (rule.get("minMc"), rule.get("maxMc")) != (11700, 12111):
        raise ValueError("deepslate rule must be supported only in Minecraft 1.17 through 1.21.11")

    properties = (root / "gradle.properties").read_text(encoding="utf-8").splitlines()
    version = next(line.split("=", 1)[1].strip() for line in properties
                   if line.startswith("mod_version="))

    for mc, should_include in VERSIONS.items():
        jar = directory / f"carpet-dds-addition-v{version}-mc{mc}.jar"
        with ZipFile(jar) as archive:
            contains_legacy_compat = COMPAT_CLASS in archive.namelist()
        if contains_legacy_compat != should_include:
            raise ValueError(
                f"MC {mc}: legacy deepslate compat class "
                f"{'missing' if should_include else 'still packaged'}"
            )
        print(f"MC {mc}: deepslate legacy implementation "
              f"{'retained' if should_include else 'absent'} - OK")


def main():
    audit(ROOT, ROOT / "build/releases")


if __name__ == "__main__":
    main()
