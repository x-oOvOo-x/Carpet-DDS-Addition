#!/usr/bin/env python3
"""Check permanent logical-client retirement and server preservation in runtime JARs."""
import argparse
import json
import re
from pathlib import Path
from zipfile import ZipFile

REMOVED = (
    "carpetddsaddition/mixin/feature/undoredo/KeyboardHandlerMixin.class",
    "carpetddsaddition/feature/undoredo/network/UndoRedoClientNetwork.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/ClientboundContainerClosePacketAccessor.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/ClientContainerCloseMixin.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/ClientPredictionMixin.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/CreativeModeInventoryScreenAccessor.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/CreativeSlotWrapperAccessor.class",
    "carpetddsaddition/mixin/feature/quickcontaineraccess/ContainerScreenInputMixin.class",
    "carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessClient.class",
    "carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessClientNetwork.class",
    "carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessDtCompat.class",
    "carpetddsaddition/mixin/network/ClientCustomPayloadMixin.class",
    "carpetddsaddition/mixin/network/ClientHandshakeMixin.class",
    "carpetddsaddition/network/DdsClientNetwork.class",
    "carpetddsaddition/network/DdsClientState.class",
)
RETAINED = (
    "carpetddsaddition/feature/undoredo/network/UndoRedoServerNetwork.class",
    "carpetddsaddition/feature/undoredo/network/UndoRedoPackets.class",
    "carpetddsaddition/core/bootstrap/DdsNetworkBootstrap.class",
    "carpetddsaddition/network/DdsPacketRegistry.class",
    "carpetddsaddition/network/DdsServerNetwork.class",
    "carpetddsaddition/network/DdsProtocol.class",
    "carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.class",
    "carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessPackets.class",
    "carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.class",
    "carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerPackets.class",
    "carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.class",
)
RETIRED_SERVER_MENU = (
    "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerMenu.class",
    "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerEnderChestMenu.class",
    "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerEquipmentSlot.class",
    "carpetddsaddition/integration/gca/fakeplayer/DdsMappedContainer.class",
    "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerAccess.class",
    "carpetddsaddition/feature/fakeplayerinventory/FakePlayerInventory.class",
    "carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerMenuOpenState.class",
)
RETIRED_LEGACY_REMOTE = (
    "carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessOwnerEnderActions.class",
)
RETIRED_WIRE_IDS = (b"fake_player_open_menu_s2c", b"qca_owner_ender_action_c2s")
REMOVED_MIXIN = "feature.undoredo.KeyboardHandlerMixin"
REMOVED_MIXINS = (REMOVED_MIXIN,
    "feature.quickcontaineraccess.ClientboundContainerClosePacketAccessor",
    "feature.quickcontaineraccess.ClientContainerCloseMixin",
    "feature.quickcontaineraccess.ClientPredictionMixin",
    "feature.quickcontaineraccess.CreativeModeInventoryScreenAccessor",
    "feature.quickcontaineraccess.CreativeSlotWrapperAccessor",
    "feature.quickcontaineraccess.ContainerScreenInputMixin",
    "network.ClientCustomPayloadMixin",
    "network.ClientHandshakeMixin",
)
JAR_VERSION = re.compile(r"-mc(.+)\.jar$")


def audit(directory, config, *, ownership_contract=None):
    targets = config["activeVersions"]
    if len(targets) != len(set(targets)):
        raise ValueError("duplicate active Minecraft version in settings")
    ownership_targets = {}
    if ownership_contract is not None:
        nodes = ownership_contract.get("targets", [])
        ownership_targets = {node["minecraft"]: node for node in nodes}
        if (len(nodes) != len(ownership_targets) or set(ownership_targets) != set(targets)
                or any(node.get("ddsStatus") != "active"
                       or node.get("tweakNodeStatus") != "active"
                       or node.get("wireMode") not in {
                           "DDS_PAYLOAD_CODEC", "DDS_PAYLOAD_READER_MAP",
                           "LEGACY_CUSTOM_PAYLOAD"}
                       or node.get("ddsPublishesTweakOwnership") is not True
                       for node in nodes)):
            raise ValueError("ownership contract must match all active published targets and wire modes")
    files = {}
    for path in Path(directory).glob("carpet-dds-addition-v*.jar"):
        match = JAR_VERSION.search(path.name)
        if match is None:
            raise ValueError(f"bad output JAR name: {path.name}")
        version = match.group(1)
        if version in files:
            raise ValueError(f"duplicate runtime JAR for {version}")
        files[version] = path
    if set(files) != set(targets):
        raise ValueError(f"incorrect 21-target JAR set: missing={sorted(set(targets)-set(files))}, extra={sorted(set(files)-set(targets))}")

    for version, jar in files.items():
        with ZipFile(jar) as archive:
            names = set(archive.namelist())
            for item in REMOVED:
                if item in names:
                    raise ValueError(f"{version}: obsolete logical-client class remains: {item}")
            for item in RETIRED_SERVER_MENU + RETIRED_LEGACY_REMOTE:
                if item in names:
                    raise ValueError(f"{version}: retired server feature class remains: {item}")
            for item in RETAINED:
                if item not in names:
                    raise ValueError(f"{version}: required server/wire class missing: {item}")
            for name in names:
                if name.endswith(".class"):
                    bytecode = archive.read(name)
                    for packet_id in RETIRED_WIRE_IDS:
                        if packet_id in bytecode:
                            raise ValueError(f"{version}: retired wire ID {packet_id.decode()} remains in {name}")
            try:
                mixins = json.loads(archive.read("carpet-dds-addition.mixins.json"))
            except KeyError as error:
                raise ValueError(f"{version}: missing mixin JSON") from error
            if ownership_contract is not None:
                metadata = json.loads(archive.read("fabric.mod.json"))
                custom = metadata.get("custom", {})
                marker = custom.get("dds:network_ownership") if isinstance(custom, dict) else None
                expected = ownership_targets[version]["wireMode"]
                if (not isinstance(marker, dict)
                        or type(marker.get("contract")) is not int or marker["contract"] != 1
                        or marker.get("logical_client_owner") != "tweak"
                        or marker.get("wire_mode") != expected
                        or marker.get("pre_cutover_test", False) is not False):
                    raise ValueError(f"{version}: invalid production ownership marker for {expected}")
            client_hooks = mixins.get("client", [])
            if client_hooks:
                raise ValueError(f"{version}: retired logical-client mixin still registered: {client_hooks}")
            shared = set(mixins.get("mixins", []))
            expected_shared = {
                "network.ServerCustomPayloadMixin",
                "network.ServerboundCustomPayloadPacketMixin",
                "network.ClientboundCustomPayloadPacketMixin",
                "feature.quickcontaineraccess.ServerContainerClickMixin",
            }
            if not expected_shared <= shared:
                raise ValueError(f"{version}: required shared/server mixin missing: "
                                 f"{sorted(expected_shared - shared)}")
    return {"jar_audited": len(files), "removed_client_classes": len(REMOVED),
            "server_shared_classes_preserved": len(RETAINED),
            "retired_server_menu_classes": len(RETIRED_SERVER_MENU),
            "retired_legacy_remote_classes": len(RETIRED_LEGACY_REMOTE),
            "retired_wire_ids_absent": len(RETIRED_WIRE_IDS),
            "ownership_marker_audited": len(files) if ownership_contract is not None else 0,
            "runtime_attestation": False, "release_authorized": False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--directory", default="build/releases")
    parser.add_argument("--settings", default="settings.json")
    parser.add_argument("--ownership-contract", type=Path,
                        help="require the canonical production ownership marker in every JAR")
    args = parser.parse_args()
    try:
        contract = (json.loads(args.ownership_contract.read_text("utf-8"))
                    if args.ownership_contract is not None else None)
        print(json.dumps(audit(args.directory,
                               json.loads(Path(args.settings).read_text("utf-8")),
                               ownership_contract=contract), indent=2))
    except (ValueError, OSError, json.JSONDecodeError) as error:
        parser.exit(1, f"Client-retirement JAR audit FAILED: {error}\n")


if __name__ == "__main__":
    main()
