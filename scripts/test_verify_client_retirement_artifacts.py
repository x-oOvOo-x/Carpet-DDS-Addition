"""Offline tests for permanent runtime JAR source-removal integrity."""
import json
import inspect
import tempfile
import unittest
from pathlib import Path
from zipfile import ZipFile

from verify_client_retirement_artifacts import REMOVED, REMOVED_MIXIN, REMOVED_MIXINS, RETAINED, RETIRED_SERVER_MENU, RETIRED_LEGACY_REMOTE, RETIRED_WIRE_IDS, audit


class ClientRetirementJarAuditTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.version = "1.21.9"
        self.jar = self.root / f"carpet-dds-addition-v1.7.1-mc{self.version}.jar"
        self.config = {"activeVersions": [self.version]}
        self.ownership_contract = {"targets": [{
            "minecraft": self.version, "ddsStatus": "active", "tweakNodeStatus": "active",
            "wireMode": "DDS_PAYLOAD_CODEC", "ddsPublishesTweakOwnership": True}]}
        self.write_jar()

    def test_audit_exposes_permanent_ownership_contract(self):
        self.assertIn("ownership_contract", inspect.signature(audit).parameters)

    def write_jar(self, *, extra=(), missing=(), client=(), shared=None, marker="default", class_payloads=None):
        if shared is None:
            shared = [
                "network.ServerCustomPayloadMixin",
                "network.ServerboundCustomPayloadPacketMixin",
                "network.ClientboundCustomPayloadPacketMixin",
                "feature.quickcontaineraccess.ServerContainerClickMixin",
            ]
        with ZipFile(self.jar, "w") as out:
            for path in set(RETAINED).difference(missing).union(extra):
                out.writestr(path, (class_payloads or {}).get(path, b"placeholder"))
            out.writestr("carpet-dds-addition.mixins.json",
                         json.dumps({"mixins": shared, "client": list(client)}))
            if marker == "default":
                marker = {"contract": 1, "logical_client_owner": "tweak",
                          "wire_mode": "DDS_PAYLOAD_CODEC"}
            out.writestr("fabric.mod.json", json.dumps({
                "custom": {} if marker is None else {"dds:network_ownership": marker}}))

    def test_safe_jar_is_ready_for_review_not_release(self):
        result = audit(self.root, self.config)
        self.assertEqual(result["jar_audited"], 1)
        self.assertEqual(result["removed_client_classes"], 15)
        self.assertFalse(result["runtime_attestation"])
        self.assertFalse(result["release_authorized"])

    def test_fails_obsolete_keyboard_class(self):
        self.write_jar(extra=[REMOVED[0]])
        with self.assertRaisesRegex(ValueError, "obsolete logical-client"):
            audit(self.root, self.config)

    def test_fails_obsolete_client_network_class(self):
        self.write_jar(extra=[REMOVED[1]])
        with self.assertRaisesRegex(ValueError, "obsolete logical-client"):
            audit(self.root, self.config)

    def test_fails_keyboard_mixin_registered(self):
        self.write_jar(client=[REMOVED_MIXIN])
        with self.assertRaisesRegex(ValueError, "retired logical-client mixin"):
            audit(self.root, self.config)

    def test_fails_retired_qca_client_class(self):
        self.write_jar(extra=[REMOVED[2]])
        with self.assertRaisesRegex(ValueError, "obsolete logical-client"):
            audit(self.root, self.config)

    def test_fails_retired_qca_mixin_registration(self):
        self.write_jar(client=[REMOVED_MIXINS[1]])
        with self.assertRaisesRegex(ValueError, "retired logical-client mixin"):
            audit(self.root, self.config)

    def test_fails_retired_network_client_class(self):
        self.write_jar(extra=[REMOVED[-1]])
        with self.assertRaisesRegex(ValueError, "obsolete logical-client"):
            audit(self.root, self.config)

    def test_fails_retired_network_mixin(self):
        self.write_jar(client=[REMOVED_MIXINS[-1]])
        with self.assertRaisesRegex(ValueError, "retired logical-client mixin"):
            audit(self.root, self.config)

    def test_fails_server_class_deleted(self):
        self.write_jar(missing=[RETAINED[0]])
        with self.assertRaisesRegex(ValueError, "required server/wire"):
            audit(self.root, self.config)

    def test_fails_server_payload_mixin_deleted(self):
        self.write_jar(shared=[])
        with self.assertRaisesRegex(ValueError, "shared/server mixin missing"):
            audit(self.root, self.config)

    def test_fails_missing_qca_server_class(self):
        self.write_jar(missing=["carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.class"])
        with self.assertRaisesRegex(ValueError, "required server/wire"):
            audit(self.root, self.config)

    def test_fails_retired_fakeplayer_menu_class_inside_jar(self):
        self.write_jar(extra=[RETIRED_SERVER_MENU[0]])
        with self.assertRaisesRegex(ValueError, "retired server feature"):
            audit(self.root, self.config)

    def test_fails_retired_fakeplayer_rule_class_inside_jar(self):
        self.write_jar(extra=[RETIRED_SERVER_MENU[-1]])
        with self.assertRaisesRegex(ValueError, "retired server feature"):
            audit(self.root, self.config)

    def test_fails_retired_remote_ender_batch_class_inside_jar(self):
        self.write_jar(extra=[RETIRED_LEGACY_REMOTE[0]])
        with self.assertRaisesRegex(ValueError, "retired server feature"):
            audit(self.root, self.config)

    def test_fails_retired_legacy_menu_fifo_class_inside_jar(self):
        self.write_jar(extra=[RETIRED_SERVER_MENU[-1]])
        with self.assertRaisesRegex(ValueError, "retired server feature"):
            audit(self.root, self.config)

    def test_fails_retired_wire_ids_in_bytecode(self):
        for packet_id in RETIRED_WIRE_IDS:
            with self.subTest(packet_id=packet_id):
                self.write_jar(class_payloads={RETAINED[-1]: b"header" + packet_id + b"tail"})
                with self.assertRaisesRegex(ValueError, "retired wire ID"):
                    audit(self.root, self.config)

    def test_fails_missing_serverbound_payload_mixin(self):
        self.write_jar(shared=[
            "network.ServerCustomPayloadMixin",
            "network.ClientboundCustomPayloadPacketMixin",
            "feature.quickcontaineraccess.ServerContainerClickMixin",
        ])
        with self.assertRaisesRegex(ValueError, "shared/server mixin missing"):
            audit(self.root, self.config)

    def test_fails_missing_version(self):
        self.config["activeVersions"].append("1.14.4")
        with self.assertRaisesRegex(ValueError, "incorrect 21-target"):
            audit(self.root, self.config)

    def test_fails_duplicate_version(self):
        self.config["activeVersions"].append(self.version)
        with self.assertRaisesRegex(ValueError, "duplicate active"):
            audit(self.root, self.config)


    def test_owner_marker_matches_contract(self):
        result = audit(self.root, self.config, ownership_contract=self.ownership_contract)
        self.assertEqual(result["ownership_marker_audited"], 1)

    def test_owner_marker_rejects_preview_jar(self):
        self.write_jar(marker={"contract": 1, "logical_client_owner": "tweak",
                               "wire_mode": "DDS_PAYLOAD_CODEC", "pre_cutover_test": True})
        with self.assertRaisesRegex(ValueError, "invalid production ownership marker"):
            audit(self.root, self.config, ownership_contract=self.ownership_contract)

    def test_owner_marker_rejects_missing_marker(self):
        self.write_jar(marker=None)
        with self.assertRaisesRegex(ValueError, "invalid production ownership marker"):
            audit(self.root, self.config, ownership_contract=self.ownership_contract)

    def test_owner_marker_rejects_wrong_owner(self):
        self.write_jar(marker={"contract": 1, "logical_client_owner": "dds",
                               "wire_mode": "DDS_PAYLOAD_CODEC"})
        with self.assertRaisesRegex(ValueError, "invalid production ownership marker"):
            audit(self.root, self.config, ownership_contract=self.ownership_contract)

    def test_owner_marker_rejects_non_integer_contract(self):
        self.write_jar(marker={"contract": True, "logical_client_owner": "tweak",
                               "wire_mode": "DDS_PAYLOAD_CODEC"})
        with self.assertRaisesRegex(ValueError, "invalid production ownership marker"):
            audit(self.root, self.config, ownership_contract=self.ownership_contract)

    def test_owner_contract_rejects_missing_target(self):
        self.ownership_contract["targets"].clear()
        with self.assertRaisesRegex(ValueError, "ownership contract must match"):
            audit(self.root, self.config, ownership_contract=self.ownership_contract)


if __name__ == "__main__":
    unittest.main()
