#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Carpet-DDS-Addition fake-player API scanner.

Purpose
-------
Inspect the actual compile-time APIs for every supported Minecraft version
before writing cross-version compatibility code.

The scanner does NOT guess Gradle cache paths.

Instead it:
1. reads settings.json;
2. injects a temporary Gradle init script;
3. asks each version project for its real main compileClasspath and Java toolchain;
4. locates the class file that actually contains each target class;
5. runs javap -private -s;
6. saves raw signatures;
7. generates:
   - report.md
   - matrix.csv
   - breakpoints.md
   - raw/<version>/*.txt

This is especially useful for DDS fake-player inventory compatibility:
MenuType, MenuScreens, AbstractContainerMenu, screens/widgets, Inventory,
network payload packets, Carpet EntityPlayerActionPack, etc.

Optional GCA support
--------------------
DDS does not necessarily compile against GCA, so GCA classes may not be on
the Gradle compileClasspath.

Pass one or more GCA jars/directories with:

    --extra-classpath path\to\gca.jar

The scanner will then also try to inspect GCA target classes such as
dev.dubhe.gugle.carpet.GcaSetting.

Examples
--------

Scan the first important compatibility boundary:

    py scripts\scan_fakeplayer_api.py ^
        --versions 26.2 26.1.2 1.21.11

Scan every DDS-supported version:

    py scripts\scan_fakeplayer_api.py --all

Scan every version and include a local GCA jar:

    py scripts\scan_fakeplayer_api.py --all ^
        --extra-classpath run\mods\gca.jar

Include bytecode in raw javap output:

    py scripts\scan_fakeplayer_api.py ^
        --versions 26.2 26.1.2 1.21.11 ^
        --bytecode

Output directory defaults to:

    build/api-scan/fakeplayer
"""

from __future__ import annotations

import argparse
import csv
import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile

from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, Sequence


# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

SCRIPT_DIR = Path(__file__).resolve().parent
ROOT = SCRIPT_DIR.parent
SETTINGS_JSON = ROOT / "settings.json"


# ---------------------------------------------------------------------------
# Target specification
# ---------------------------------------------------------------------------

@dataclass(frozen=True)
class ApiTarget:
    key: str
    class_name: str
    category: str
    focus_patterns: tuple[str, ...] = ()


TARGETS: tuple[ApiTarget, ...] = (
    # -----------------------------------------------------------------------
    # Menu / container API
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.menu_type",
        "net.minecraft.world.inventory.MenuType",
        "Minecraft / menu",
        (
            r"\bMenuType\(",
            r"\bcreate\(",
            r"MenuSupplier",
            r"FeatureFlagSet",
        ),
    ),
    ApiTarget(
        "mc.menu_screens",
        "net.minecraft.client.gui.screens.MenuScreens",
        "Minecraft / client menu",
        (
            r"\bregister\(",
            r"\bcreate\(",
            r"ScreenConstructor",
        ),
    ),
    ApiTarget(
        "mc.abstract_container_menu",
        "net.minecraft.world.inventory.AbstractContainerMenu",
        "Minecraft / menu",
        (
            r"\bAbstractContainerMenu\(",
            r"\baddSlot\(",
            r"\baddDataSlots\(",
            r"\bbroadcastChanges\(",
            r"\bquickMoveStack\(",
            r"\bclicked\(",
            r"\bremoved\(",
        ),
    ),
    ApiTarget(
        "mc.abstract_container_screen",
        "net.minecraft.client.gui.screens.inventory.AbstractContainerScreen",
        "Minecraft / client menu",
        (
            r"\bAbstractContainerScreen\(",
            r"\brenderBg\(",
            r"\brender\(",
            r"\bkeyPressed\(",
            r"\bmouseClicked\(",
            r"\bhoveredSlot\b",
            r"\bimageWidth\b",
            r"\bimageHeight\b",
            r"\bleftPos\b",
            r"\btopPos\b",
        ),
    ),

    # -----------------------------------------------------------------------
    # Widgets / rendering
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.abstract_button",
        "net.minecraft.client.gui.components.AbstractButton",
        "Minecraft / widget",
        (
            r"\bAbstractButton\(",
            r"\bonPress\(",
            r"\brenderWidget\(",
            r"\brenderContents\(",
            r"\bupdateWidgetNarration\(",
            r"\bdefaultButtonNarrationText\(",
        ),
    ),
    ApiTarget(
        "mc.edit_box",
        "net.minecraft.client.gui.components.EditBox",
        "Minecraft / widget",
        (
            r"\bEditBox\(",
            r"\bsetResponder\(",
            r"\bsetFilter\(",
            r"\bsetValue\(",
            r"\bisFocused\(",
            r"\bcanConsumeInput\(",
            r"\bsetMaxLength\(",
        ),
    ),
    ApiTarget(
        "mc.gui_graphics",
        "net.minecraft.client.gui.GuiGraphics",
        "Minecraft / rendering",
        (
            r"\bblit\(",
            r"\brenderItem\(",
            r"\brenderOutline\(",
            r"\bfill\(",
        ),
    ),

    # -----------------------------------------------------------------------
    # Inventory / slot API
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.inventory",
        "net.minecraft.world.entity.player.Inventory",
        "Minecraft / inventory",
        (
            r"\bInventory\(",
            r"\bgetItem\(",
            r"\bsetItem\(",
            r"\bgetContainerSize\(",
            r"\bselected\b",
            r"\bselectedSlot\b",
            r"\bgetSelected\(",
            r"\bgetSelectedSlot\(",
        ),
    ),
    ApiTarget(
        "mc.slot",
        "net.minecraft.world.inventory.Slot",
        "Minecraft / inventory",
        (
            r"\bSlot\(",
            r"\bmayPlace\(",
            r"\bmayPickup\(",
            r"\bgetNoItemIcon\(",
            r"\bgetMaxStackSize\(",
        ),
    ),
    ApiTarget(
        "mc.inventory_menu",
        "net.minecraft.world.inventory.InventoryMenu",
        "Minecraft / inventory",
        (
            r"EMPTY_ARMOR_SLOT",
            r"EMPTY_ARMOR",
            r"SHIELD",
        ),
    ),

    # -----------------------------------------------------------------------
    # Player interaction / menu opening
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.player",
        "net.minecraft.world.entity.player.Player",
        "Minecraft / player",
        (
            r"\binteractOn\(",
            r"\bgetInventory\(",
            r"\bisShiftKeyDown\(",
        ),
    ),
    ApiTarget(
        "mc.server_player",
        "net.minecraft.server.level.ServerPlayer",
        "Minecraft / player",
        (
            r"\bopenMenu\(",
            r"\bcloseContainer\(",
            r"\bkill\(",
            r"\bcontainerMenu\b",
        ),
    ),

    # -----------------------------------------------------------------------
    # Registry / identifiers
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.resource_location",
        "net.minecraft.resources.ResourceLocation",
        "Minecraft / identifier",
        (
            r"\bResourceLocation\(",
            r"\bfromNamespaceAndPath\(",
            r"\bparse\(",
        ),
    ),
    ApiTarget(
        "mc.identifier",
        "net.minecraft.resources.Identifier",
        "Minecraft / identifier",
        (
            r"\bIdentifier\(",
            r"\bfromNamespaceAndPath\(",
            r"\bparse\(",
        ),
    ),
    ApiTarget(
        "mc.registry",
        "net.minecraft.core.Registry",
        "Minecraft / registry",
        (
            r"\bregister\(",
        ),
    ),
    ApiTarget(
        "mc.built_in_registries",
        "net.minecraft.core.registries.BuiltInRegistries",
        "Minecraft / registry",
        (
            r"\bMENU\b",
        ),
    ),

    # -----------------------------------------------------------------------
    # Vanilla custom payload networking
    # -----------------------------------------------------------------------
    ApiTarget(
        "mc.custom_packet_payload",
        "net.minecraft.network.protocol.common.custom.CustomPacketPayload",
        "Minecraft / network",
        (
            r"\btype\(",
            r"\bType\b",
            r"\bcodec\(",
        ),
    ),
    ApiTarget(
        "mc.serverbound_custom_payload",
        "net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket",
        "Minecraft / network",
        (
            r"\bServerboundCustomPayloadPacket\(",
            r"\bpayload\(",
        ),
    ),
    ApiTarget(
        "mc.clientbound_custom_payload",
        "net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket",
        "Minecraft / network",
        (
            r"\bClientboundCustomPayloadPacket\(",
            r"\bpayload\(",
        ),
    ),

    # -----------------------------------------------------------------------
    # Carpet fake-player action API
    # -----------------------------------------------------------------------
    ApiTarget(
        "carpet.action_pack",
        "carpet.helpers.EntityPlayerActionPack",
        "Carpet",
        (
            r"\bstart\(",
            r"\bstopAll\(",
            r"\bdrop\(",
            r"\bsetSlot\(",
            r"\bactions\b",
        ),
    ),
    ApiTarget(
        "carpet.action",
        "carpet.helpers.EntityPlayerActionPack$Action",
        "Carpet",
        (
            r"\bcontinuous\(",
            r"\binterval\(",
            r"\bonce\(",
            r"\bisContinuous\b",
            r"\binterval\b",
            r"\boffset\b",
            r"\blimit\b",
        ),
    ),
    ApiTarget(
        "carpet.action_type",
        "carpet.helpers.EntityPlayerActionPack$ActionType",
        "Carpet",
        (),
    ),

    # -----------------------------------------------------------------------
    # GCA - found only if its jar is supplied/on classpath.
    # -----------------------------------------------------------------------
    ApiTarget(
        "gca.setting",
        "dev.dubhe.gugle.carpet.GcaSetting",
        "GCA",
        (
            r"\bopenFakePlayerInventory\b",
            r"\bopenFakePlayerEnderChest\b",
        ),
    ),
)


# ---------------------------------------------------------------------------
# Utility
# ---------------------------------------------------------------------------

def fail(message: str, exit_code: int = 1) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(exit_code)


def run(
    command: Sequence[str],
    *,
    cwd: Path = ROOT,
    check: bool = True,
    capture: bool = True,
) -> subprocess.CompletedProcess[str]:
    kwargs = dict(
        cwd=str(cwd),
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
    )

    if capture:
        kwargs["stdout"] = subprocess.PIPE
        kwargs["stderr"] = subprocess.STDOUT

    proc = subprocess.run(
        list(command),
        **kwargs,
    )

    if check and proc.returncode != 0:
        output = proc.stdout or ""
        print(output, file=sys.stderr)
        fail(
            "command failed "
            f"({proc.returncode}): "
            + " ".join(command)
        )

    return proc


def sha256_text(text: str) -> str:
    return hashlib.sha256(
        text.encode("utf-8")
    ).hexdigest()


def safe_name(value: str) -> str:
    return re.sub(
        r"[^A-Za-z0-9_.-]+",
        "_",
        value,
    )


def class_entry_name(class_name: str) -> str:
    return (
        class_name
        .replace(".", "/")
        + ".class"
    )


def normalize_signature(raw: str) -> str:
    """
    Keep the structural javap signature while discarding noise that should not
    define an API generation boundary.
    """
    lines = []

    for line in raw.splitlines():
        stripped = line.strip()

        if not stripped:
            continue

        if stripped.startswith("Compiled from "):
            continue

        if stripped.startswith("descriptor:"):
            # Keep descriptors. They are useful for exact overload changes.
            lines.append(stripped)
            continue

        if stripped in {"{", "}"}:
            continue

        # Ignore bytecode when --bytecode was requested. The matrix should
        # still describe API shape, not implementation details.
        if re.match(r"^\d+:", stripped):
            continue

        if stripped.startswith(
            (
                "Code:",
                "LineNumberTable:",
                "LocalVariableTable:",
                "StackMapTable:",
            )
        ):
            continue

        lines.append(stripped)

    return "\n".join(lines)


def focused_lines(
    normalized: str,
    patterns: Iterable[str],
) -> list[str]:
    patterns = tuple(patterns)

    if not patterns:
        return normalized.splitlines()

    regexes = [
        re.compile(pattern)
        for pattern in patterns
    ]

    result: list[str] = []
    previous_was_signature = False

    for line in normalized.splitlines():
        matches = any(
            regex.search(line)
            for regex in regexes
        )

        # javap -s prints descriptor on the line after the declaration.
        if matches:
            result.append(line)
            previous_was_signature = True
            continue

        if (
            previous_was_signature
            and line.startswith("descriptor:")
        ):
            result.append(line)
        previous_was_signature = False

    return result


# ---------------------------------------------------------------------------
# Project configuration
# ---------------------------------------------------------------------------

def load_versions() -> list[str]:
    if not SETTINGS_JSON.exists():
        fail(
            f"missing {SETTINGS_JSON.relative_to(ROOT)}; "
            "run this script from the DDS repository"
        )

    data = json.loads(
        SETTINGS_JSON.read_text(
            encoding="utf-8"
        )
    )

    versions = data.get("versions")

    if not isinstance(versions, list):
        fail("settings.json versions is not a list")

    result = [
        str(version)
        for version in versions
    ]

    if not result:
        fail("settings.json has no versions")

    return result


def resolve_gradle_wrapper() -> Path:
    candidates = (
        ROOT / "gradlew.bat",
        ROOT / "gradlew",
    )

    for path in candidates:
        if path.exists():
            return path

    fail("Gradle wrapper not found")


# ---------------------------------------------------------------------------
# Gradle classpath discovery
# ---------------------------------------------------------------------------

INIT_SCRIPT = r"""
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile

allprojects { p ->
    p.plugins.withId('java') {
        if (p.tasks.findByName('ddsDumpApiClasspath') == null) {
            p.tasks.register('ddsDumpApiClasspath') {
                doLast {
                    def sourceSets =
                        p.extensions.getByType(SourceSetContainer)
                    def main =
                        sourceSets.getByName('main')

                    println(
                        'DDS_API_BEGIN\t' + p.name
                    )

                    main.compileClasspath.files.each { f ->
                        println(
                            'DDS_API_CP\t'
                            + f.absolutePath
                        )
                    }

                    def compileJava =
                        p.tasks.named(
                            'compileJava',
                            JavaCompile
                        ).get()

                    def compiler =
                        compileJava.javaCompiler.orNull

                    if (compiler != null) {
                        println(
                            'DDS_API_JAVA\t'
                            + compiler.metadata
                                .installationPath
                                .asFile
                                .absolutePath
                        )
                    }

                    println(
                        'DDS_API_END\t' + p.name
                    )
                }
            }
        }
    }
}
"""


@dataclass
class VersionClasspath:
    version: str
    entries: list[Path]
    java_home: Path | None


def discover_classpaths(
    versions: Sequence[str],
) -> dict[str, VersionClasspath]:
    wrapper = resolve_gradle_wrapper()

    with tempfile.TemporaryDirectory(
        prefix="dds-api-scan-"
    ) as temp:
        init_path = (
            Path(temp)
            / "dds-api-scan.init.gradle"
        )
        init_path.write_text(
            INIT_SCRIPT,
            encoding="utf-8",
        )

        command = [
            str(wrapper),
            "--console=plain",
            "--no-daemon",
            "-I",
            str(init_path),
        ]

        for version in versions:
            command.append(
                f":{version}:ddsDumpApiClasspath"
            )

        print(
            "Resolving Gradle compile classpaths..."
        )

        proc = run(
            command,
            capture=True,
        )

    output = proc.stdout or ""

    result: dict[str, VersionClasspath] = {}

    current_version: str | None = None
    current_entries: list[Path] = []
    current_java: Path | None = None

    for raw in output.splitlines():
        if raw.startswith("DDS_API_BEGIN\t"):
            current_version = (
                raw.split("\t", 1)[1].strip()
            )
            current_entries = []
            current_java = None
            continue

        if (
            raw.startswith("DDS_API_CP\t")
            and current_version is not None
        ):
            value = (
                raw.split("\t", 1)[1].strip()
            )
            current_entries.append(Path(value))
            continue

        if (
            raw.startswith("DDS_API_JAVA\t")
            and current_version is not None
        ):
            value = (
                raw.split("\t", 1)[1].strip()
            )
            current_java = Path(value)
            continue

        if (
            raw.startswith("DDS_API_END\t")
            and current_version is not None
        ):
            result[current_version] = (
                VersionClasspath(
                    version=current_version,
                    entries=current_entries,
                    java_home=current_java,
                )
            )
            current_version = None
            current_entries = []
            current_java = None

    missing = [
        version
        for version in versions
        if version not in result
    ]

    if missing:
        debug_log = (
            ROOT
            / "build"
            / "api-scan"
            / "gradle-classpath-output.log"
        )
        debug_log.parent.mkdir(
            parents=True,
            exist_ok=True,
        )
        debug_log.write_text(
            output,
            encoding="utf-8",
        )

        fail(
            "could not obtain classpath for: "
            + ", ".join(missing)
            + f"; Gradle output saved to {debug_log}"
        )

    return result


# ---------------------------------------------------------------------------
# Class lookup
# ---------------------------------------------------------------------------

class ClassLocator:
    def __init__(
        self,
        extra_entries: Sequence[Path],
    ) -> None:
        self.extra_entries = list(extra_entries)
        self._jar_name_cache: dict[
            Path,
            set[str],
        ] = {}

    def _jar_entries(
        self,
        jar: Path,
    ) -> set[str]:
        cached = self._jar_name_cache.get(jar)

        if cached is not None:
            return cached

        try:
            with zipfile.ZipFile(jar, "r") as zf:
                names = set(zf.namelist())
        except (
            OSError,
            zipfile.BadZipFile,
        ):
            names = set()

        self._jar_name_cache[jar] = names
        return names

    def contains_class(
        self,
        entry: Path,
        class_name: str,
    ) -> bool:
        class_entry = class_entry_name(
            class_name
        )

        if entry.is_dir():
            return (
                entry
                / class_entry
            ).exists()

        if (
            entry.is_file()
            and entry.suffix.lower()
            in {".jar", ".zip"}
        ):
            return (
                class_entry
                in self._jar_entries(entry)
            )

        return False

    def locate(
        self,
        class_name: str,
        classpath: Sequence[Path],
    ) -> Path | None:
        for entry in (
            list(classpath)
            + self.extra_entries
        ):
            if self.contains_class(
                entry,
                class_name,
            ):
                return entry

        return None


# ---------------------------------------------------------------------------
# javap
# ---------------------------------------------------------------------------

def resolve_javap(
    java_home: Path | None,
) -> str:
    candidates: list[Path] = []

    if java_home is not None:
        candidates.extend(
            (
                java_home / "bin" / "javap.exe",
                java_home / "bin" / "javap",
            )
        )

    env_java_home = os.environ.get(
        "JAVA_HOME"
    )

    if env_java_home:
        env_home = Path(env_java_home)
        candidates.extend(
            (
                env_home / "bin" / "javap.exe",
                env_home / "bin" / "javap",
            )
        )

    for candidate in candidates:
        if candidate.exists():
            return str(candidate)

    found = shutil.which("javap")

    if found:
        return found

    fail(
        "javap not found; install a JDK "
        "or configure the project's Java toolchain"
    )


@dataclass
class ScanResult:
    version: str
    target: ApiTarget
    present: bool
    container: Path | None
    raw: str
    normalized: str
    signature_hash: str
    focused: list[str]
    error: str = ""


def javap_class(
    *,
    javap: str,
    class_name: str,
    container: Path,
    bytecode: bool,
) -> tuple[bool, str]:
    command = [
        javap,
        "-classpath",
        str(container),
        "-p",
        "-s",
    ]

    if bytecode:
        command.append("-c")

    command.append(class_name)

    proc = run(
        command,
        check=False,
        capture=True,
    )

    output = proc.stdout or ""

    return (
        proc.returncode == 0,
        output,
    )


# ---------------------------------------------------------------------------
# Reports
# ---------------------------------------------------------------------------

def version_sort_key(version: str) -> tuple[int, ...]:
    return tuple(
        int(part)
        for part in version.split(".")
    )


def write_matrix(
    output_dir: Path,
    versions: Sequence[str],
    results: dict[
        tuple[str, str],
        ScanResult,
    ],
) -> None:
    path = output_dir / "matrix.csv"

    with path.open(
        "w",
        encoding="utf-8-sig",
        newline="",
    ) as file:
        writer = csv.writer(file)

        writer.writerow(
            [
                "category",
                "target_key",
                "class_name",
                *versions,
            ]
        )

        for target in TARGETS:
            row = [
                target.category,
                target.key,
                target.class_name,
            ]

            for version in versions:
                result = results[
                    (version, target.key)
                ]

                if not result.present:
                    row.append("MISSING")
                else:
                    row.append(
                        result.signature_hash[:12]
                    )

            writer.writerow(row)


def build_groups(
    versions: Sequence[str],
    target: ApiTarget,
    results: dict[
        tuple[str, str],
        ScanResult,
    ],
) -> list[tuple[list[str], str]]:
    groups: list[
        tuple[list[str], str]
    ] = []

    current_versions: list[str] = []
    current_key: str | None = None

    for version in versions:
        result = results[
            (version, target.key)
        ]

        key = (
            "MISSING"
            if not result.present
            else result.signature_hash
        )

        if (
            current_key is None
            or key == current_key
        ):
            current_versions.append(version)
            current_key = key
            continue

        groups.append(
            (
                current_versions,
                current_key,
            )
        )
        current_versions = [version]
        current_key = key

    if current_versions:
        groups.append(
            (
                current_versions,
                current_key or "MISSING",
            )
        )

    return groups


def format_version_group(
    versions: Sequence[str],
) -> str:
    if len(versions) == 1:
        return versions[0]

    return (
        f"{versions[0]} → {versions[-1]}"
    )


def write_breakpoints(
    output_dir: Path,
    versions: Sequence[str],
    results: dict[
        tuple[str, str],
        ScanResult,
    ],
) -> None:
    path = output_dir / "breakpoints.md"
    lines = [
        "# DDS fake-player API breakpoints",
        "",
        "Versions are shown in the scan order.",
        "A new hash means the public/private bytecode signature changed.",
        "",
    ]

    current_category: str | None = None

    for target in TARGETS:
        if target.category != current_category:
            current_category = target.category
            lines.extend(
                [
                    f"## {current_category}",
                    "",
                ]
            )

        lines.extend(
            [
                f"### `{target.key}`",
                "",
                f"`{target.class_name}`",
                "",
            ]
        )

        groups = build_groups(
            versions,
            target,
            results,
        )

        for group_versions, key in groups:
            group_text = (
                format_version_group(
                    group_versions
                )
            )

            if key == "MISSING":
                lines.append(
                    f"- **{group_text}**: `MISSING`"
                )
            else:
                lines.append(
                    f"- **{group_text}**: "
                    f"`{key[:12]}`"
                )

        lines.append("")

    path.write_text(
        "\n".join(lines),
        encoding="utf-8",
    )


def write_report(
    output_dir: Path,
    versions: Sequence[str],
    results: dict[
        tuple[str, str],
        ScanResult,
    ],
) -> None:
    path = output_dir / "report.md"

    lines = [
        "# DDS fake-player API scan",
        "",
        "## Versions",
        "",
        "```text",
        *versions,
        "```",
        "",
        "## Interpretation",
        "",
        "- Same hash across adjacent versions: API signature is structurally identical for that class.",
        "- Different hash: inspect the focused signatures and raw javap output before deciding the compat boundary.",
        "- `MISSING`: the class name does not exist on that version's compile classpath.",
        "- GCA targets require a GCA jar on the classpath or `--extra-classpath`.",
        "",
    ]

    for version in versions:
        lines.extend(
            [
                f"# Minecraft {version}",
                "",
            ]
        )

        current_category: str | None = None

        for target in TARGETS:
            if target.category != current_category:
                current_category = target.category
                lines.extend(
                    [
                        f"## {current_category}",
                        "",
                    ]
                )

            result = results[
                (version, target.key)
            ]

            lines.extend(
                [
                    f"### `{target.key}`",
                    "",
                    f"Class: `{target.class_name}`",
                    "",
                ]
            )

            if not result.present:
                lines.extend(
                    [
                        "**MISSING**",
                        "",
                    ]
                )
                continue

            lines.extend(
                [
                    f"Signature hash: `{result.signature_hash[:12]}`",
                    "",
                    f"Container: `{result.container}`",
                    "",
                ]
            )

            if result.focused:
                lines.append("```text")
                lines.extend(result.focused)
                lines.append("```")
                lines.append("")
            else:
                lines.extend(
                    [
                        "_No focused line matched; see raw output._",
                        "",
                    ]
                )

    path.write_text(
        "\n".join(lines),
        encoding="utf-8",
    )


# ---------------------------------------------------------------------------
# Main scanning
# ---------------------------------------------------------------------------

def scan(
    versions: Sequence[str],
    *,
    output_dir: Path,
    extra_classpath: Sequence[Path],
    bytecode: bool,
) -> None:
    classpaths = discover_classpaths(
        versions
    )

    locator = ClassLocator(
        extra_classpath
    )

    results: dict[
        tuple[str, str],
        ScanResult,
    ] = {}

    raw_root = output_dir / "raw"
    raw_root.mkdir(
        parents=True,
        exist_ok=True,
    )

    for version in versions:
        version_cp = classpaths[version]
        javap = resolve_javap(
            version_cp.java_home
        )

        print(
            f"\n[{version}] "
            f"javap: {javap}"
        )

        version_raw = (
            raw_root
            / safe_name(version)
        )
        version_raw.mkdir(
            parents=True,
            exist_ok=True,
        )

        for target in TARGETS:
            container = locator.locate(
                target.class_name,
                version_cp.entries,
            )

            if container is None:
                result = ScanResult(
                    version=version,
                    target=target,
                    present=False,
                    container=None,
                    raw="",
                    normalized="",
                    signature_hash="",
                    focused=[],
                    error="class not found",
                )
                results[
                    (version, target.key)
                ] = result
                print(
                    f"  MISSING  {target.key}"
                )
                continue

            ok, raw = javap_class(
                javap=javap,
                class_name=target.class_name,
                container=container,
                bytecode=bytecode,
            )

            raw_path = (
                version_raw
                / (
                    safe_name(target.key)
                    + ".txt"
                )
            )
            raw_path.write_text(
                raw,
                encoding="utf-8",
            )

            if not ok:
                result = ScanResult(
                    version=version,
                    target=target,
                    present=False,
                    container=container,
                    raw=raw,
                    normalized="",
                    signature_hash="",
                    focused=[],
                    error="javap failed",
                )
                results[
                    (version, target.key)
                ] = result
                print(
                    f"  ERROR    {target.key}"
                )
                continue

            normalized = normalize_signature(
                raw
            )
            signature_hash = sha256_text(
                normalized
            )
            focused = focused_lines(
                normalized,
                target.focus_patterns,
            )

            result = ScanResult(
                version=version,
                target=target,
                present=True,
                container=container,
                raw=raw,
                normalized=normalized,
                signature_hash=signature_hash,
                focused=focused,
            )

            results[
                (version, target.key)
            ] = result

            print(
                f"  OK       "
                f"{target.key:<34} "
                f"{signature_hash[:12]}"
            )

    output_dir.mkdir(
        parents=True,
        exist_ok=True,
    )

    write_matrix(
        output_dir,
        versions,
        results,
    )
    write_breakpoints(
        output_dir,
        versions,
        results,
    )
    write_report(
        output_dir,
        versions,
        results,
    )

    print()
    print("API scan complete:")
    print(
        "  "
        + str(output_dir / "report.md")
    )
    print(
        "  "
        + str(output_dir / "breakpoints.md")
    )
    print(
        "  "
        + str(output_dir / "matrix.csv")
    )
    print(
        "  "
        + str(output_dir / "raw")
    )


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def parse_args(
    supported_versions: Sequence[str],
) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Scan Minecraft/Carpet/GCA APIs "
            "for DDS fake-player compatibility"
        )
    )

    group = parser.add_mutually_exclusive_group()

    group.add_argument(
        "--all",
        action="store_true",
        help=(
            "scan every version from settings.json"
        ),
    )

    group.add_argument(
        "--versions",
        nargs="+",
        metavar="VERSION",
        help=(
            "specific versions to scan, "
            "e.g. 26.2 26.1.2 1.21.11"
        ),
    )

    parser.add_argument(
        "--output",
        type=Path,
        default=(
            ROOT
            / "build"
            / "api-scan"
            / "fakeplayer"
        ),
        help=(
            "output directory "
            "(default: build/api-scan/fakeplayer)"
        ),
    )

    parser.add_argument(
        "--extra-classpath",
        action="append",
        default=[],
        type=Path,
        metavar="PATH",
        help=(
            "extra jar/directory to scan, "
            "for example a GCA jar; repeatable"
        ),
    )

    parser.add_argument(
        "--bytecode",
        action="store_true",
        help=(
            "also include javap -c bytecode "
            "in raw output"
        ),
    )

    args = parser.parse_args()

    if args.all:
        versions = list(
            supported_versions
        )
    elif args.versions:
        versions = list(args.versions)
    else:
        # Safe first-pass default: inspect the current important boundary.
        preferred = [
            "26.2",
            "26.1.2",
            "1.21.11",
        ]
        versions = [
            version
            for version in preferred
            if version in supported_versions
        ]

    unknown = [
        version
        for version in versions
        if version not in supported_versions
    ]

    if unknown:
        fail(
            "unsupported version(s): "
            + ", ".join(unknown)
        )

    args.resolved_versions = versions

    extra: list[Path] = []

    for value in args.extra_classpath:
        path = (
            value
            if value.is_absolute()
            else ROOT / value
        ).resolve()

        if not path.exists():
            fail(
                f"extra classpath does not exist: {path}"
            )

        extra.append(path)

    args.resolved_extra_classpath = extra

    output = args.output

    if not output.is_absolute():
        output = (
            ROOT
            / output
        ).resolve()

    args.resolved_output = output

    return args


def main() -> None:
    supported_versions = load_versions()
    args = parse_args(
        supported_versions
    )

    print(
        "DDS fake-player API scanner"
    )
    print(
        "Repository: "
        + str(ROOT)
    )
    print(
        "Versions: "
        + ", ".join(
            args.resolved_versions
        )
    )

    if args.resolved_extra_classpath:
        print("Extra classpath:")
        for entry in (
            args.resolved_extra_classpath
        ):
            print(f"  {entry}")

    scan(
        args.resolved_versions,
        output_dir=args.resolved_output,
        extra_classpath=(
            args.resolved_extra_classpath
        ),
        bytecode=args.bytecode,
    )


if __name__ == "__main__":
    main()
