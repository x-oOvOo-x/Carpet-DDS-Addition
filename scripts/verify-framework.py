#!/usr/bin/env python3

from pathlib import Path
import json
import re
import sys


ROOT = Path(__file__).resolve().parents[1]

# DDS development/canonical target.
#
# New rules are designed and validated around Minecraft 1.21.11 before
# compatibility work proceeds toward other supported versions.
CORE_VERSION = "1.21.11"

# ReplayMod/Fallen-Breath Preprocessor main project.
#
# IMPORTANT:
# This is NOT the same concept as CORE_VERSION.
#
# The root src/main source tree is written in the preprocessor form whose
# active/default branch corresponds to Minecraft 1.15.2. Changing
# versions/mainProject to 1.21.11 would cause the raw root source to be
# compiled directly as 1.21.11 and bypass the required preprocessing.
PREPROCESS_MAIN_PROJECT = "1.15.2"

# Historical mapping graph anchor.
#
# Existing graph:
#
#     1.14.4 <- 1.15.2 -> 1.16.1 -> ... -> 1.21.11 -> 26.1.2 -> 26.2
#
MAPPING_ANCHOR = "1.15.2"


# ---------------------------------------------------------------------------
# Load project configuration
# ---------------------------------------------------------------------------

settings_file = ROOT / "settings.json"
rules_file = ROOT / "src/main/rules/rules.json"

settings = json.loads(
    settings_file.read_text(
        encoding="utf-8"
    )
)

rules = json.loads(
    rules_file.read_text(
        encoding="utf-8"
    )
)

errors = []

versions = settings.get("versions", [])


# ---------------------------------------------------------------------------
# Minecraft version helpers
# ---------------------------------------------------------------------------

def version_key(version):
    """
    Convert a supported stable Minecraft version into a comparable tuple.

    Examples:

        1.14.4   -> (1, 14, 4)
        1.18     -> (1, 18)
        1.21.11  -> (1, 21, 11)
        26.1.2   -> (26, 1, 2)
        26.2     -> (26, 2)

    DDS currently uses numeric stable-version build targets only.
    """

    if not isinstance(version, str):
        raise ValueError(
            f"unsupported Minecraft version value: {version!r}"
        )

    parts = version.split(".")

    if (
        not parts
        or any(
            not part.isdigit()
            for part in parts
        )
    ):
        raise ValueError(
            f"unsupported Minecraft version format: {version!r}"
        )

    return tuple(
        int(part)
        for part in parts
    )


# ---------------------------------------------------------------------------
# Project version configuration
# ---------------------------------------------------------------------------

if settings.get("coreVersion") != CORE_VERSION:
    errors.append(
        "settings.json coreVersion must be "
        f"{CORE_VERSION}"
    )


if (
    settings.get("preprocessMainProject")
    != PREPROCESS_MAIN_PROJECT
):
    errors.append(
        "settings.json preprocessMainProject must be "
        f"{PREPROCESS_MAIN_PROJECT}"
    )


if not isinstance(versions, list):
    errors.append(
        "settings.json versions must be a list"
    )
    versions = []


if len(versions) != len(set(versions)):
    errors.append(
        "settings.json contains duplicate version nodes"
    )


try:
    expected_version_order = sorted(
        versions,
        key=version_key,
        reverse=True,
    )
except ValueError as exc:
    errors.append(str(exc))
    expected_version_order = list(versions)


if versions != expected_version_order:
    errors.append(
        "settings.json versions must be ordered "
        "from newest to oldest"
    )


if CORE_VERSION not in versions:
    errors.append(
        f"core version {CORE_VERSION} is missing "
        "from settings.json versions"
    )


if PREPROCESS_MAIN_PROJECT not in versions:
    errors.append(
        f"preprocess main project "
        f"{PREPROCESS_MAIN_PROJECT} is missing "
        "from settings.json versions"
    )


if MAPPING_ANCHOR not in versions:
    errors.append(
        f"mapping anchor {MAPPING_ANCHOR} is missing "
        "from settings.json versions"
    )


# ---------------------------------------------------------------------------
# ReplayMod/Fallen-Breath Preprocessor main project
# ---------------------------------------------------------------------------

main_project_file = (
    ROOT
    / "versions"
    / "mainProject"
)


if not main_project_file.exists():
    errors.append(
        "missing versions/mainProject required "
        "by com.replaymod.preprocess"
    )
else:
    main_project = main_project_file.read_text(
        encoding="utf-8"
    ).strip()

    if main_project != PREPROCESS_MAIN_PROJECT:
        errors.append(
            "versions/mainProject "
            f"({main_project}) must be "
            f"{PREPROCESS_MAIN_PROJECT}"
        )

    if (
        main_project
        != settings.get("preprocessMainProject")
    ):
        errors.append(
            "versions/mainProject must match "
            "settings.json preprocessMainProject"
        )


# ---------------------------------------------------------------------------
# Java runtime toolchain invariant
# ---------------------------------------------------------------------------

#
# Compilation compatibility and Minecraft runtime Java selection intentionally
# share common.gradle's javaCompatibility value. Loom game runs must use an
# explicit Gradle JavaLauncher instead of inheriting the Gradle daemon JVM.
#
settings_gradle_file = ROOT / "settings.gradle"
common_gradle_file = ROOT / "common.gradle"
root_build_gradle_file = ROOT / "build.gradle"

java_root = (
    ROOT
    / "src"
    / "main"
    / "java"
)


if not settings_gradle_file.exists():
    errors.append(
        "missing settings.gradle"
    )
else:
    settings_gradle_text = settings_gradle_file.read_text(
        encoding="utf-8"
    )

    if (
        "org.gradle.toolchains.foojay-resolver-convention"
        not in settings_gradle_text
    ):
        errors.append(
            "settings.gradle must configure the Foojay "
            "toolchain resolver"
        )


if not common_gradle_file.exists():
    errors.append(
        "missing common.gradle"
    )
else:
    common_gradle_text = common_gradle_file.read_text(
        encoding="utf-8"
    )

    required_runtime_markers = (
        "final int javaLevel =",
        "Integer.parseInt(javaCompatibility.majorVersion)",
        "javaToolchains.launcherFor",
        "JavaLanguageVersion.of(",
        "org.gradle.jvm.toolchain.JvmVendorSpec.AZUL",
        "final List<String> gameRunTaskNames =",
        "'runClient'",
        "'runServer'",
        "javaLauncher.set(gameJavaLauncher)",
        "tasks.register('verifyJavaRuntime')",
        "tasks.named(",
        "runTask.javaLauncher",
        "installationPath",
        "System.getProperty('java.home')",
        "getPreferGradleTask().set(true)",
    )

    for marker in required_runtime_markers:
        if marker not in common_gradle_text:
            errors.append(
                "common.gradle missing Java runtime "
                f"toolchain invariant: {marker}"
            )

    loom_game_run_binding = (
        "tasks.withType(JavaExec).configureEach {\n"
        "    if (gameRunTaskNames.contains(name)) {\n"
        "        javaLauncher.set(gameJavaLauncher)\n"
        "    }\n"
        "}"
    )

    if common_gradle_text.count(
        loom_game_run_binding
    ) != 1:
        errors.append(
            "common.gradle must bind gameJavaLauncher "
            "to DDS Loom game/audit runs exactly once"
        )

    if re.search(
        r"java\s*\{\s*toolchain\s*\{",
        common_gradle_text,
    ):
        errors.append(
            "common.gradle must not apply the game runtime "
            "toolchain to Java compilation"
        )


if not root_build_gradle_file.exists():
    errors.append(
        "missing build.gradle"
    )
else:
    root_build_gradle_text = root_build_gradle_file.read_text(
        encoding="utf-8"
    )

    if "tasks.register('verifyJavaRuntimes')" not in root_build_gradle_text:
        errors.append(
            "build.gradle missing verifyJavaRuntimes task"
        )

    # The all-runtime verifier is intentionally opt-in. A normal build on a
    # fresh machine must not force Gradle to provision every supported JDK.
    if root_build_gradle_text.count(
        "dependsOn(javaRuntimeVerificationTaskPaths)"
    ) != 1:
        errors.append(
            "verifyJavaRuntimes must be the only root task "
            "depending on javaRuntimeVerificationTaskPaths"
        )


# ---------------------------------------------------------------------------
# Canonical network contract tests
# ---------------------------------------------------------------------------

#
# Phase 6A invariant:
#
# Shared network behavior is tested once against the canonical 1.21.11
# preprocessed output. Cross-version compatibility continues to be checked by
# the normal all-version build.
#
network_test_files = (
    "DdsProtocolContractTest.java",
    "DdsClientStateTest.java",
    "DdsPacketRegistryContractTest.java",
    "DdsFixedWindowLimiterContractTest.java",
    "DdsServerRoutePolicyTest.java",
)


network_test_root = (
    ROOT
    / "src"
    / "test"
    / "java"
    / "carpetddsaddition"
    / "network"
)


for test_file_name in network_test_files:
    test_file = (
        network_test_root
        / test_file_name
    )

    if not test_file.exists():
        errors.append(
            "missing canonical network contract test: "
            f"{test_file_name}"
        )


route_policy_file = (
    java_root
    / "carpetddsaddition"
    / "network"
    / "DdsServerRoutePolicy.java"
)


if not route_policy_file.exists():
    errors.append(
        "missing shared network route policy: "
        "carpetddsaddition/network/DdsServerRoutePolicy.java"
    )
else:
    route_policy_text = route_policy_file.read_text(
        encoding="utf-8"
    )

    required_route_policy_markers = (
        "isProtocolCompatible(",
        "canDispatchEnabled(",
        "canDispatchDisabledGrace(",
        "satisfiesAdvertisement(",
    )

    for marker in required_route_policy_markers:
        if marker not in route_policy_text:
            errors.append(
                "DdsServerRoutePolicy missing Phase 6B "
                f"invariant: {marker}"
            )


if common_gradle_file.exists():
    required_test_markers = (
        "final boolean networkContractTestTarget =",
        "project.minecraft_version == '1.21.11'",
        "org.junit:junit-bom:5.13.4",
        "org.junit.jupiter:junit-jupiter",
        "'src/test/java'",
        "tasks.named('test', Test)",
        "useJUnitPlatform()",
        "javaLauncher.set(gameJavaLauncher)",
    )

    for marker in required_test_markers:
        if marker not in common_gradle_text:
            errors.append(
                "common.gradle missing Phase 6A "
                f"test invariant: {marker}"
            )


if root_build_gradle_file.exists():
    if "tasks.register('testNetworkContracts')" not in root_build_gradle_text:
        errors.append(
            "build.gradle missing testNetworkContracts task"
        )

    if "dependsOn(':1.21.11:test')" not in root_build_gradle_text:
        errors.append(
            "testNetworkContracts must run the "
            "canonical 1.21.11 test task"
        )


# ---------------------------------------------------------------------------
# Per-version Gradle metadata
# ---------------------------------------------------------------------------

try:
    modern_rule_api_start = version_key(
        "1.19.2"
    )
except ValueError:
    modern_rule_api_start = (1, 19, 2)


required_version_properties = (
    "minecraft_version",
    "minecraft_dependency",
    "game_versions",
    "carpet_core_version",
    "carpet_dependency",
    "loader_dependency",
    "rule_api",
)


for version in versions:
    properties_file = (
        ROOT
        / "versions"
        / version
        / "gradle.properties"
    )

    if not properties_file.exists():
        errors.append(
            f"missing "
            f"{properties_file.relative_to(ROOT)}"
        )
        continue

    text = properties_file.read_text(
        encoding="utf-8"
    )

    for key in required_version_properties:
        if not re.search(
            rf"(?m)^{re.escape(key)}=.+$",
            text,
        ):
            errors.append(
                f"{properties_file.relative_to(ROOT)} "
                f"missing {key}"
            )

    try:
        current_version_key = version_key(
            version
        )
    except ValueError as exc:
        errors.append(str(exc))
        continue

    # Carpet's modern public settings API is used by DDS from the
    # Minecraft 1.19.2 anchor onward.
    #
    # This is determined from the actual Minecraft version instead of
    # the index inside settings.json, because settings.json is maintained
    # newest -> oldest.
    expected_api = (
        "modern"
        if current_version_key
        >= modern_rule_api_start
        else "legacy"
    )

    match = re.search(
        r"(?m)^rule_api=(.+)$",
        text,
    )

    if match:
        actual_api = (
            match
            .group(1)
            .strip()
        )

        if actual_api != expected_api:
            errors.append(
                f"{properties_file.relative_to(ROOT)} "
                f"should use rule_api={expected_api}, "
                f"found rule_api={actual_api}"
            )


# ---------------------------------------------------------------------------
# Rule specification validation
# ---------------------------------------------------------------------------

rule_list = rules.get(
    "rules",
    [],
)


if not isinstance(rule_list, list):
    errors.append(
        "rules.json rules must be a list"
    )
    rule_list = []


names = [
    rule.get("name")
    for rule in rule_list
    if isinstance(rule, dict)
]


if len(names) != len(set(names)):
    errors.append(
        "rules.json contains duplicate rule names"
    )


allowed_types = {
    "boolean",
    "int",
    "double",
    "String",
}


for rule in rule_list:
    if not isinstance(rule, dict):
        errors.append(
            f"invalid rule entry: {rule!r}"
        )
        continue

    rule_name = rule.get("name")

    if not re.fullmatch(
        r"[A-Za-z_$][A-Za-z0-9_$]*",
        rule_name or "",
    ):
        errors.append(
            "invalid Java rule identifier: "
            f"{rule_name!r}"
        )

    rule_type = rule.get("type")

    if rule_type not in allowed_types:
        errors.append(
            "unsupported cross-version rule type "
            f"for {rule_name}: {rule_type}"
        )

    for bound in (
        "minMc",
        "maxMc",
    ):
        if (
            bound in rule
            and not isinstance(
                rule[bound],
                int,
            )
        ):
            errors.append(
                f"rule {rule_name} {bound} "
                "must be an integer MC version"
            )

    min_mc = rule.get("minMc")
    max_mc = rule.get("maxMc")

    if (
        isinstance(min_mc, int)
        and isinstance(max_mc, int)
        and min_mc > max_mc
    ):
        errors.append(
            f"rule {rule_name} has "
            "minMc greater than maxMc"
        )

    categories = rule.get(
        "categories"
    )

    if not categories:
        errors.append(
            f"rule {rule_name} "
            "has no categories"
        )

    description = rule.get(
        "description"
    )

    if (
        not isinstance(
            description,
            dict,
        )
        or not isinstance(
            description.get("en_us"),
            str,
        )
        or not description["en_us"].strip()
    ):
        errors.append(
            f"rule {rule_name} must provide "
            "description.en_us"
        )

    if isinstance(
        description,
        dict,
    ):
        for language, value in description.items():
            if not re.fullmatch(
                r"[a-z]{2}_[a-z]{2}",
                language,
            ):
                errors.append(
                    f"rule {rule_name} has "
                    "invalid language key "
                    f"{language!r}"
                )

            if (
                not isinstance(
                    value,
                    str,
                )
                or not value.strip()
            ):
                errors.append(
                    f"rule {rule_name} has "
                    f"empty translation "
                    f"{language}"
                )

    display_name = rule.get(
        "displayName"
    )

    if display_name is not None:
        if not isinstance(
            display_name,
            dict,
        ):
            errors.append(
                f"rule {rule_name} displayName "
                "must be an object"
            )
        else:
            for language, value in display_name.items():
                if not re.fullmatch(
                    r"[a-z]{2}_[a-z]{2}",
                    language,
                ):
                    errors.append(
                        f"rule {rule_name} has "
                        "invalid displayName language key "
                        f"{language!r}"
                    )

                if (
                    not isinstance(
                        value,
                        str,
                    )
                    or not value.strip()
                ):
                    errors.append(
                        f"rule {rule_name} has "
                        f"empty displayName "
                        f"{language}"
                    )


# ---------------------------------------------------------------------------
# Required framework rule invariants
# ---------------------------------------------------------------------------

if "icePlaceWater" not in names:
    errors.append(
        "icePlaceWater rule was renamed "
        "or removed"
    )
else:
    ice_place_water = next(
        rule
        for rule in rule_list
        if (
            isinstance(rule, dict)
            and rule.get("name")
            == "icePlaceWater"
        )
    )

    if (
        ice_place_water.get("default")
        is not False
    ):
        errors.append(
            "icePlaceWater must remain "
            "disabled by default"
        )


# ---------------------------------------------------------------------------
# Version macro architectural boundary
# ---------------------------------------------------------------------------

if not java_root.exists():
    errors.append(
        "missing src/main/java"
    )
else:
    for java_file in java_root.rglob(
        "*.java"
    ):
        text = java_file.read_text(
            encoding="utf-8"
        )

        if "//#if" not in text:
            continue

        relative_path = (
            java_file
            .relative_to(java_root)
            .as_posix()
        )

        rooted_path = (
            "/"
            + relative_path
        )

        version_boundary_markers = (
            "/platform/",
            "/integration/",
            "/compat/",
            "/mixin/",
            "/network/",
        )

        if not any(
            marker in rooted_path
            for marker in version_boundary_markers
        ):
            errors.append(
                "version macro leaked into "
                "business source: "
                f"{relative_path}"
            )


# ---------------------------------------------------------------------------
# DDS network architecture boundary
# ---------------------------------------------------------------------------

#
# Phase 3 invariant:
#
# Shared transport/session code must remain feature-agnostic. Feature and
# integration packet ids and business behavior belong to their vertical
# slices.
#
shared_network_files = (
    "DdsProtocol.java",
    "DdsPacketRegistry.java",
    "DdsClientState.java",
    "DdsClientNetwork.java",
    "DdsServerNetwork.java",
    "DdsServerRoutePolicy.java",
)


network_root = (
    java_root
    / "carpetddsaddition"
    / "network"
)


for file_name in shared_network_files:
    network_file = network_root / file_name

    if not network_file.exists():
        errors.append(
            "missing shared network source: "
            f"carpetddsaddition/network/{file_name}"
        )
        continue

    network_text = network_file.read_text(
        encoding="utf-8"
    )

    if "import carpetddsaddition.feature." in network_text:
        errors.append(
            "shared network source imports feature code: "
            f"carpetddsaddition/network/{file_name}"
        )

    if "import carpetddsaddition.integration." in network_text:
        errors.append(
            "shared network source imports integration code: "
            f"carpetddsaddition/network/{file_name}"
        )


feature_packet_owners = {
    "qca_storage_click_c2s": (
        "carpetddsaddition/feature/"
        "quickcontaineraccess/network/"
        "QuickContainerAccessPackets.java"
    ),
    "undo_c2s": (
        "carpetddsaddition/feature/"
        "undoredo/network/"
        "UndoRedoPackets.java"
    ),
    "redo_c2s": (
        "carpetddsaddition/feature/"
        "undoredo/network/"
        "UndoRedoPackets.java"
    ),
    "fake_player_action_c2s": (
        "carpetddsaddition/integration/"
        "gca/fakeplayer/network/"
        "DdsFakePlayerPackets.java"
    ),
}


protocol_file = (
    network_root
    / "DdsProtocol.java"
)


if protocol_file.exists():
    protocol_text = protocol_file.read_text(
        encoding="utf-8"
    )

    for packet_id in feature_packet_owners:
        if packet_id in protocol_text:
            errors.append(
                "DdsProtocol must not own feature packet id "
                f"{packet_id}"
            )


java_source_text = {}


if java_root.exists():
    for java_file in java_root.rglob("*.java"):
        relative = (
            java_file
            .relative_to(java_root)
            .as_posix()
        )

        java_source_text[relative] = (
            java_file.read_text(
                encoding="utf-8"
            )
        )


for packet_id, expected_owner in feature_packet_owners.items():
    owners = [
        relative
        for relative, source_text
        in java_source_text.items()
        if f'"{packet_id}"' in source_text
    ]

    if owners != [expected_owner]:
        errors.append(
            "feature packet id "
            f"{packet_id} must be owned only by "
            f"{expected_owner}; found {owners}"
        )


obsolete_network_symbols = (
    "DdsProtocol.QCA_STORAGE_CLICK_C2S",
    "DdsProtocol.UNDO_C2S",
    "DdsProtocol.REDO_C2S",
    "DdsProtocol.FAKE_PLAYER_ACTION_C2S",
    "DdsProtocol.localC2SPackets(",
    "DdsProtocol.localS2CPackets(",
    "DdsClientNetwork.canUseQcaStorageClick(",
    "DdsClientNetwork.sendQcaStorageClick(",
    "DdsClientNetwork.canUseUndoRedoShortcut(",
    "DdsClientNetwork.sendUndo(",
    "DdsClientNetwork.sendRedo(",
    "DdsClientNetwork.canUseFakePlayerActions(",
    "DdsClientNetwork.sendFakePlayerAction(",
)


for relative, source_text in java_source_text.items():
    for obsolete_symbol in obsolete_network_symbols:
        if obsolete_symbol in source_text:
            errors.append(
                "obsolete pre-Phase-3 network ownership remains in "
                f"{relative}: {obsolete_symbol}"
            )


# ---------------------------------------------------------------------------
# DDS network control-plane hardening
# ---------------------------------------------------------------------------

#
# Phase 5 invariant:
#
# Business traffic keeps Protocol v1's 160 requests/second ceiling. Client-
# triggered HELLO/control feedback has an independent 8 events/second ceiling.
# Server-initiated protocol rule-state broadcasts intentionally bypass the
# client-triggered control limiter.
#
server_network_file = (
    java_root
    / "carpetddsaddition"
    / "network"
    / "DdsServerNetwork.java"
)


if not server_network_file.exists():
    errors.append(
        "missing shared network source: "
        "carpetddsaddition/network/DdsServerNetwork.java"
    )
else:
    server_network_text = server_network_file.read_text(
        encoding="utf-8"
    )

    required_hardening_markers = (
        "MAX_REQUESTS_PER_SECOND = 160",
        "MAX_CONTROL_REQUESTS_PER_SECOND = 8",
        "allowControlTraffic()",
        "private static ClientState clientState(",
        "private static final class FixedWindowLimiter",
        "controlLimiter.allow()",
        "requestLimiter.allow()",
    )

    for marker in required_hardening_markers:
        if marker not in server_network_text:
            errors.append(
                "DdsServerNetwork missing Phase 5 "
                f"hardening invariant: {marker}"
            )

    if server_network_text.count(
        "new ClientState()"
    ) != 1:
        errors.append(
            "DdsServerNetwork must create ClientState "
            "only through the connection-state helper"
        )

    if server_network_text.count(
        "MAX_REQUESTS_PER_SECOND = 160"
    ) != 1:
        errors.append(
            "DDS Protocol v1 business limiter must remain 160/s"
        )

    if server_network_text.count(
        "MAX_CONTROL_REQUESTS_PER_SECOND = 8"
    ) != 1:
        errors.append(
            "DDS control-plane limiter must remain 8/s"
        )

    server_broadcast = (
        "if (listener != null) {\n"
        "                sendHello(listener, enabled);\n"
        "            }"
    )

    if server_broadcast not in server_network_text:
        errors.append(
            "server-initiated DDS protocol rule broadcasts "
            "must bypass the client control limiter"
        )


# ---------------------------------------------------------------------------
# Mixin architecture and audit contract
# ---------------------------------------------------------------------------

#
# Phase 7 invariant:
#
# DDS keeps strict Mixin application semantics:
#
#     required = true
#     injectors.defaultRequire = 1
#
# Every configured Mixin must have a corresponding source file and every
# Mixin source must be declared in the configuration.
#
# DDS additionally provides explicit development-only server and client
# Mixin audit runs. These audit runs are verification tools and are not
# executed as part of an ordinary build.
#

mixin_config_file = (
    ROOT
    / "src"
    / "main"
    / "resources"
    / "carpet-dds-addition.mixins.json"
)

mixin_root = (
    java_root
    / "carpetddsaddition"
    / "mixin"
)

mixin_audit_file = (
    java_root
    / "carpetddsaddition"
    / "core"
    / "mixin"
    / "DdsMixinAudit.java"
)

initializer_file = (
    java_root
    / "carpetddsaddition"
    / "CarpetDDSAddition.java"
)

declared_mixins = []


if not mixin_config_file.exists():
    errors.append(
        "missing carpet-dds-addition.mixins.json"
    )
else:
    try:
        mixin_config = json.loads(
            mixin_config_file.read_text(
                encoding="utf-8"
            )
        )
    except json.JSONDecodeError as exc:
        errors.append(
            "invalid carpet-dds-addition.mixins.json: "
            f"{exc}"
        )
        mixin_config = {}

    if not isinstance(
        mixin_config,
        dict,
    ):
        errors.append(
            "carpet-dds-addition.mixins.json "
            "must contain a JSON object"
        )
        mixin_config = {}

    if mixin_config.get("required") is not True:
        errors.append(
            "DDS Mixin config must remain required=true"
        )

    injectors = mixin_config.get(
        "injectors",
        {},
    )

    if not isinstance(
        injectors,
        dict,
    ):
        errors.append(
            "DDS Mixin config injectors must be an object"
        )
        injectors = {}

    if injectors.get("defaultRequire") != 1:
        errors.append(
            "DDS Mixin config must keep "
            "injectors.defaultRequire=1"
        )

    #
    # A project-wide global overwrite policy is intentionally forbidden.
    # DDS should prefer narrow injectors and accessors over replacement of
    # Minecraft methods.
    #
    if "overwrites" in mixin_config:
        errors.append(
            "DDS Mixin config must not add a global "
            "overwrites policy"
        )

    for section in (
        "mixins",
        "client",
        "server",
    ):
        entries = mixin_config.get(
            section,
            [],
        )

        if not isinstance(
            entries,
            list,
        ):
            errors.append(
                f"Mixin config {section} must be a list"
            )
            continue

        for entry in entries:
            if not isinstance(
                entry,
                str,
            ):
                errors.append(
                    f"invalid Mixin entry in {section}: "
                    f"{entry!r}"
                )
                continue

            if not entry.strip():
                errors.append(
                    f"empty Mixin entry in {section}"
                )
                continue

            declared_mixins.append(entry)

    if (
        len(declared_mixins)
        != len(set(declared_mixins))
    ):
        duplicate_mixins = sorted(
            {
                mixin_name
                for mixin_name in declared_mixins
                if declared_mixins.count(
                    mixin_name
                ) > 1
            }
        )

        errors.append(
            "Mixin config contains duplicate entries: "
            + ", ".join(
                duplicate_mixins
            )
        )

    for mixin_name in declared_mixins:
        mixin_source = (
            mixin_root
            / (
                mixin_name.replace(
                    ".",
                    "/",
                )
                + ".java"
            )
        )

        if not mixin_source.exists():
            errors.append(
                "Mixin config references missing source: "
                f"{mixin_name}"
            )


#
# Reverse validation:
#
# It is easy to create a Mixin source file and forget to add it to the JSON
# configuration. Detect that mistake before compilation/runtime.
#
if mixin_root.exists():
    for mixin_source in mixin_root.rglob(
        "*.java"
    ):
        source_text = mixin_source.read_text(
            encoding="utf-8"
        )

        relative_mixin_name = (
            mixin_source
            .relative_to(mixin_root)
            .with_suffix("")
            .as_posix()
            .replace(
                "/",
                ".",
            )
        )

        if (
            "@Mixin" in source_text
            and relative_mixin_name
            not in declared_mixins
        ):
            errors.append(
                "Mixin source is not declared in config: "
                f"{relative_mixin_name}"
            )

        #
        # @Overwrite is deliberately prohibited in DDS.
        #
        # This keeps compatibility closer to Carpet/TIS-style narrow hooks
        # and makes conflicts with other Carpet additions less likely.
        #
        if re.search(
            r"(?m)^\s*(?://\$\$\s*)?@Overwrite\b",
            source_text,
        ):
            errors.append(
                "DDS Mixins must not use @Overwrite: "
                f"{mixin_source.relative_to(java_root)}"
            )
else:
    errors.append(
        "missing carpetddsaddition/mixin source root"
    )


#
# Development-only Mixin audit entry point.
#
if not mixin_audit_file.exists():
    errors.append(
        "missing Phase 7 Mixin audit entry point: "
        "carpetddsaddition/core/mixin/"
        "DdsMixinAudit.java"
    )
else:
    mixin_audit_text = (
        mixin_audit_file.read_text(
            encoding="utf-8"
        )
    )

    required_mixin_audit_markers = (
        "MixinEnvironment",
        "getCurrentEnvironment()",
        ".audit()",
        "carpetddsaddition.mixin_audit",
        "isDevelopmentEnvironment()",
        "System.exit(0)",
        "System.exit(1)",
    )

    for marker in required_mixin_audit_markers:
        if marker not in mixin_audit_text:
            errors.append(
                "DdsMixinAudit missing Phase 7 "
                f"invariant: {marker}"
            )


#
# The audit must run before DDS network/rule/feature initialization.
#
if not initializer_file.exists():
    errors.append(
        "missing CarpetDDSAddition.java"
    )
else:
    initializer_text = (
        initializer_file.read_text(
            encoding="utf-8"
        )
    )

    audit_call = (
        "DdsMixinAudit.runIfRequested();"
    )

    network_bootstrap_call = (
        "DdsNetworkBootstrap.initialize();"
    )

    carpet_extension_call = (
        "CarpetServer.manageExtension(this);"
    )

    if audit_call not in initializer_text:
        errors.append(
            "CarpetDDSAddition must invoke the "
            "development Mixin audit"
        )
    else:
        audit_position = (
            initializer_text.find(
                audit_call
            )
        )

        if network_bootstrap_call in initializer_text:
            network_position = (
                initializer_text.find(
                    network_bootstrap_call
                )
            )

            if (
                network_position >= 0
                and audit_position
                > network_position
            ):
                errors.append(
                    "DdsMixinAudit must run before "
                    "DdsNetworkBootstrap initialization"
                )

        if carpet_extension_call in initializer_text:
            carpet_position = (
                initializer_text.find(
                    carpet_extension_call
                )
            )

            if (
                carpet_position >= 0
                and audit_position
                > carpet_position
            ):
                errors.append(
                    "DdsMixinAudit must run before "
                    "Carpet extension registration"
                )


#
# Loom audit run configurations.
#
if common_gradle_file.exists():
    required_mixin_run_markers = (
        "mixinAudit {",
        "mixinAuditClient {",
        "server()",
        "client()",
        "-Dcarpetddsaddition.mixin_audit=true",
        "'runMixinAudit'",
        "'runMixinAuditClient'",
    )

    for marker in required_mixin_run_markers:
        if marker not in common_gradle_text:
            errors.append(
                "common.gradle missing Phase 7 "
                f"Mixin audit invariant: {marker}"
            )

    #
    # Audit runs must use isolated directories rather than the normal
    # development world's ../../run directory.
    #
    required_audit_run_dirs = (
        'runDir "../../run/mixin-audit/${project.name}/server"',
        'runDir "../../run/mixin-audit/${project.name}/client"',
    )

    for marker in required_audit_run_dirs:
        if marker not in common_gradle_text:
            errors.append(
                "common.gradle missing isolated "
                f"Mixin audit run directory: {marker}"
            )


#
# Root verification tasks.
#
if root_build_gradle_file.exists():
    required_root_mixin_markers = (
        "mixinAuditTaskPaths",
        "tasks.register('auditMixinsCanonical')",
        "tasks.register('auditMixinsAll')",
        "':1.21.11:runMixinAudit'",
        "':1.21.11:runMixinAuditClient'",
    )

    for marker in required_root_mixin_markers:
        if marker not in root_build_gradle_text:
            errors.append(
                "build.gradle missing Phase 7 "
                f"Mixin audit invariant: {marker}"
            )

    #
    # Full Mixin auditing is intentionally opt-in. Ordinary buildAll must not
    # launch Minecraft audit JVMs.
    #
    build_all_match = re.search(
        r"tasks\.register\('buildAll'\)\s*\{"
        r"(?P<body>.*?)"
        r"\n\}",
        root_build_gradle_text,
        flags=re.DOTALL,
    )

    if build_all_match:
        build_all_body = (
            build_all_match.group("body")
        )

        if (
            "mixinAuditTaskPaths"
            in build_all_body
            or "runMixinAudit"
            in build_all_body
        ):
            errors.append(
                "buildAll must not depend on "
                "Mixin audit runs"
            )


# ---------------------------------------------------------------------------
# Known inaccessible nested Mixin target
# ---------------------------------------------------------------------------

#
# Minecraft 1.21.9+ exposes SculkSensorBlockEntity.VibrationUser as a
# protected nested class in the mappings used by DDS.
#
# Java code outside SculkSensorBlockEntity's package/inheritance hierarchy
# therefore cannot reference the nested class through a class literal.
#
# Mixin's string-target form is intentional here.
#
sculk_vibration_mixin = (
    mixin_root
    / "feature"
    / "undoredo"
    / "SculkSensorVibrationUserMixin.java"
)


if not sculk_vibration_mixin.exists():
    errors.append(
        "missing Undo/Redo Sculk vibration Mixin: "
        "carpetddsaddition/mixin/feature/undoredo/"
        "SculkSensorVibrationUserMixin.java"
    )
else:
    sculk_vibration_text = (
        sculk_vibration_mixin.read_text(
            encoding="utf-8"
        )
    )

    #
    # Match only an actual Mixin annotation line.
    #
    # Both raw Java:
    #
    #     @Mixin(...)
    #
    # and ReplayMod-preprocessor form:
    #
    #     //$$ @Mixin(...)
    #
    # are accepted.
    #
    # Text appearing inside explanatory comments must not count.
    #
    string_target_pattern = re.compile(
        r'(?m)^\s*'
        r'(?://\$\$\s*)?'
        r'@Mixin\(\s*'
        r'targets\s*=\s*'
        r'"net\.minecraft\.world\.level\.block\.entity\.'
        r'SculkSensorBlockEntity\$VibrationUser"'
        r'\s*\)'
    )

    class_literal_target_pattern = re.compile(
        r'(?m)^\s*'
        r'(?://\$\$\s*)?'
        r'@Mixin\(\s*'
        r'SculkSensorBlockEntity\.VibrationUser\.class'
        r'\s*\)'
    )

    if not string_target_pattern.search(
        sculk_vibration_text
    ):
        errors.append(
            "SculkSensorVibrationUserMixin must use the "
            "string target for the protected Minecraft "
            "VibrationUser nested class"
        )

    if class_literal_target_pattern.search(
        sculk_vibration_text
    ):
        errors.append(
            "SculkSensorVibrationUserMixin must not reference "
            "protected VibrationUser through a class literal"
        )

        # ---------------------------------------------------------------------------
        # DirectBlockDrops legacy Mixin placeholder invariant
        # ---------------------------------------------------------------------------

        #
        # Minecraft 1.18+ no longer has the legacy ServerTickList target used by
        # directBlockDrops.
        #
        # DDS keeps one shared Mixin configuration, so the legacy Mixin targets a
        # harmless DDS-owned placeholder on newer Minecraft versions.
        #
        # IMPORTANT:
        #
        # The placeholder must NOT live below carpetddsaddition.mixin.
        #
        # Sponge Mixin reserves every class inside its configured Mixin package and
        # rejects direct loading of such classes with IllegalClassLoadError.
        #
        direct_block_drops_placeholder = (
            java_root
            / "carpetddsaddition"
            / "feature"
            / "directblockdrops"
            / "compat"
            / "DirectBlockDropsLegacyServerTickListPlaceholder.java"
        )

        legacy_direct_block_drops_mixin = (
            mixin_root
            / "feature"
            / "directblockdrops"
            / "LegacyServerTickListMixin.java"
        )

        obsolete_direct_block_drops_placeholder = (
            mixin_root
            / "placeholder"
            / "DirectBlockDropsLegacyServerTickListPlaceholder.java"
        )


        if obsolete_direct_block_drops_placeholder.exists():
            errors.append(
                "DirectBlockDrops legacy placeholder must not live "
                "inside carpetddsaddition.mixin; Sponge Mixin reserves "
                "that package and rejects it as an ordinary target"
            )


        if not direct_block_drops_placeholder.exists():
            errors.append(
                "missing DirectBlockDrops legacy scheduled-tick placeholder: "
                "carpetddsaddition/feature/directblockdrops/compat/"
                "DirectBlockDropsLegacyServerTickListPlaceholder.java"
            )
        else:
            placeholder_text = (
                direct_block_drops_placeholder.read_text(
                    encoding="utf-8"
                )
            )

            required_placeholder_package = (
                "package "
                "carpetddsaddition.feature.directblockdrops.compat;"
            )

            if required_placeholder_package not in placeholder_text:
                errors.append(
                    "DirectBlockDropsLegacyServerTickListPlaceholder "
                    "must remain in the directblockdrops compat package"
                )

            if "@Mixin" in placeholder_text:
                errors.append(
                    "DirectBlockDropsLegacyServerTickListPlaceholder "
                    "must remain an ordinary no-op class, not a Mixin"
                )


        if not legacy_direct_block_drops_mixin.exists():
            errors.append(
                "missing DirectBlockDrops LegacyServerTickListMixin"
            )
        else:
            legacy_direct_block_drops_text = (
                legacy_direct_block_drops_mixin.read_text(
                    encoding="utf-8"
                )
            )

            required_placeholder_target = (
                '"carpetddsaddition.feature.directblockdrops.compat."'
            )

            obsolete_placeholder_target = (
                '"carpetddsaddition.mixin.placeholder."'
            )

            if (
                required_placeholder_target
                not in legacy_direct_block_drops_text
            ):
                errors.append(
                    "LegacyServerTickListMixin must target the "
                    "DirectBlockDrops compat placeholder on Minecraft 1.18+"
                )

            if (
                obsolete_placeholder_target
                in legacy_direct_block_drops_text
            ):
                errors.append(
                    "LegacyServerTickListMixin still references a "
                    "placeholder inside the reserved Mixin package"
                )


# ---------------------------------------------------------------------------
# Preprocessor mapping graph
# ---------------------------------------------------------------------------

#
# settings.json is intentionally maintained:
#
#     newest -> oldest
#
# Example:
#
#     26.2
#     26.1.2
#     1.21.11
#     ...
#     1.14.4
#
# This ordering is for:
#
# - human readability;
# - build ordering;
# - compatibility testing;
# - release/publishing order.
#
# It must NOT define the preprocessor mapping topology.
#
#
# Current mapping topology:
#
#                  1.14.4
#                     ↑
#                  1.15.2
#                     ↓
#                  1.16.1
#                     ↓
#                    ...
#                     ↓
#                  1.21.11
#                     ↓
#                  26.1.2
#                     ↓
#                   26.2
#
#
# PREPROCESS_MAIN_PROJECT = 1.15.2
#
# CORE_VERSION = 1.21.11 is the DDS development/reference version only.
#

try:
    chronological_versions = sorted(
        versions,
        key=version_key,
    )

    mapping_anchor_key = version_key(
        MAPPING_ANCHOR
    )

except ValueError as exc:
    errors.append(str(exc))

    chronological_versions = list(
        reversed(versions)
    )

    mapping_anchor_key = (
        1,
        15,
        2,
    )


older_than_anchor = [
    version
    for version in chronological_versions
    if (
        version_key(version)
        < mapping_anchor_key
    )
]


if older_than_anchor != ["1.14.4"]:
    errors.append(
        "preprocessor mapping architecture "
        "expects 1.14.4 to be the only "
        "supported version older than "
        f"{MAPPING_ANCHOR}"
    )


forward_versions = [
    version
    for version in chronological_versions
    if (
        version_key(version)
        >= mapping_anchor_key
    )
]


if (
    forward_versions
    and forward_versions[0]
    != MAPPING_ANCHOR
):
    errors.append(
        "preprocessor forward mapping chain "
        f"must start at {MAPPING_ANCHOR}"
    )


mapping_edges = [
    (
        MAPPING_ANCHOR,
        "1.14.4",
    )
]


mapping_edges += list(
    zip(
        forward_versions,
        forward_versions[1:],
    )
)


for source_version, target_version in mapping_edges:
    mapping_file = (
        ROOT
        / "versions"
        / (
            "mapping-"
            f"{source_version}-"
            f"{target_version}.txt"
        )
    )

    if not mapping_file.exists():
        errors.append(
            "missing preprocessor mapping bridge "
            f"{mapping_file.relative_to(ROOT)}"
        )


# ---------------------------------------------------------------------------
# Fabric API dependency guard
# ---------------------------------------------------------------------------

fabric_api_guard_files = [
    ROOT / "gradle.properties",
    ROOT / "common.gradle",
    ROOT / "src/main/resources/fabric.mod.json",
]


for guard_file in fabric_api_guard_files:
    if not guard_file.exists():
        errors.append(
            f"missing "
            f"{guard_file.relative_to(ROOT)}"
        )
        continue

    text = guard_file.read_text(
        encoding="utf-8"
    )

    if "fabric-api" in text.lower():
        errors.append(
            "Fabric API dependency leaked "
            "into core framework: "
            f"{guard_file.relative_to(ROOT)}"
        )


# ---------------------------------------------------------------------------
# Modrinth version configuration
# ---------------------------------------------------------------------------

modrinth_config_file = (
    ROOT
    / "scripts"
    / "modrinth-versions.json"
)


if not modrinth_config_file.exists():
    errors.append(
        "missing scripts/modrinth-versions.json"
    )
else:
    try:
        modrinth_config = json.loads(
            modrinth_config_file.read_text(
                encoding="utf-8"
            )
        )
    except json.JSONDecodeError as exc:
        errors.append(
            "invalid scripts/modrinth-versions.json: "
            f"{exc}"
        )
        modrinth_config = {}

    if isinstance(
        modrinth_config,
        dict,
    ):
        modrinth_targets = list(
            modrinth_config.keys()
        )

        if modrinth_targets != versions:
            errors.append(
                "scripts/modrinth-versions.json "
                "targets must match settings.json "
                "versions in the same "
                "newest-to-oldest order"
            )
    else:
        errors.append(
            "scripts/modrinth-versions.json "
            "must contain a JSON object"
        )


# ---------------------------------------------------------------------------
# Rule documentation synchronization
# ---------------------------------------------------------------------------

rule_documentation_files = (
    ("docs/rules.md", "English"),
    ("docs/rules-zh_cn.md", "Chinese"),
)

rule_heading_pattern = re.compile(
    r"(?m)^## .+\(([A-Za-z_$][A-Za-z0-9_$]*)\)\s*$"
)

expected_documented_rules = [
    name
    for name in names
    if isinstance(name, str)
]


for relative_path, language_name in rule_documentation_files:
    documentation_file = ROOT / relative_path

    if not documentation_file.exists():
        errors.append(
            f"missing {relative_path}"
        )
        continue

    documentation_text = documentation_file.read_text(
        encoding="utf-8"
    )

    documented_rules = rule_heading_pattern.findall(
        documentation_text
    )

    if len(documented_rules) != len(set(documented_rules)):
        duplicates = sorted(
            {
                name
                for name in documented_rules
                if documented_rules.count(name) > 1
            }
        )
        errors.append(
            f"{relative_path} contains duplicate rule sections: "
            + ", ".join(duplicates)
        )

    missing_rules = [
        name
        for name in expected_documented_rules
        if name not in documented_rules
    ]

    extra_rules = [
        name
        for name in documented_rules
        if name not in expected_documented_rules
    ]

    if missing_rules:
        errors.append(
            f"{relative_path} is missing rules: "
            + ", ".join(missing_rules)
        )

    if extra_rules:
        errors.append(
            f"{relative_path} contains unknown rules: "
            + ", ".join(extra_rules)
        )

    if (
        not missing_rules
        and not extra_rules
        and documented_rules != expected_documented_rules
    ):
        errors.append(
            f"{relative_path} rule section order must match rules.json"
        )


# ---------------------------------------------------------------------------
# Result
# ---------------------------------------------------------------------------

if errors:
    print(
        "\n".join(
            "ERROR: " + error
            for error in errors
        ),
        file=sys.stderr,
    )

    sys.exit(1)


print(
    f"OK: "
    f"{len(versions)} build nodes, "
    f"{len(names)} rule(s), "
    f"core={CORE_VERSION}, "
    f"preprocess-main={PREPROCESS_MAIN_PROJECT}, "
    "framework invariants satisfied"
)