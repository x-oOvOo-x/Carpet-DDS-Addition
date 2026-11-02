/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class DdsClientOwnershipHandoffContractTest {
    private static Path root() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent()) {
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        }
        throw new IllegalStateException("repo root not found");
    }

    private static String read(String path) throws IOException {
        return Files.readString(root().resolve(path));
    }

    private static String compactContract() throws IOException {
        return read("config/dds-client-ownership-contract.json").replaceAll("\\s+", "");
    }

    private static String target(String contract, String version) {
        String marker = "\"minecraft\":\"" + version + "\"";
        int markerIndex = contract.indexOf(marker);
        assertTrue(markerIndex >= 0, version);
        int start = contract.lastIndexOf('{', markerIndex);
        int end = contract.indexOf('}', markerIndex);
        assertTrue(start >= 0 && end > markerIndex, version);
        return contract.substring(start, end + 1);
    }

    @Test
    void processed12111MetadataDeclaresTweakLogicalOwner() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("fabric.mod.json")) {
            assertNotNull(in);
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("\"dds:network_ownership\""));
            assertTrue(json.contains("\"contract\": 1"));
            assertTrue(json.contains("\"logical_client_owner\": \"tweak\""));
            assertTrue(json.contains("\"wire_mode\": \"DDS_PAYLOAD_CODEC\""));
        }
    }

    @Test
    void canonicalContractDrivesPermanentProductionOwnershipMarkers() throws IOException {
        String contract = compactContract();
        for (String version : new String[]{
                "26.3", "26.2", "26.1.2", "1.21.11", "1.21.10", "1.21.9", "1.21.8", "1.21.5",
                "1.21.4", "1.21.3", "1.21.1", "1.20.6"
        }) {
            String target = target(contract, version);
            assertTrue(target.contains("\"wireMode\":\"DDS_PAYLOAD_CODEC\""), version);
            assertTrue(target.contains("\"ddsPublishesTweakOwnership\":true"), version);
        }

        for (String version : new String[]{"1.20.4", "1.20.2"}) {
            String target = target(contract, version);
            assertTrue(target.contains("\"wireMode\":\"DDS_PAYLOAD_READER_MAP\""), version);
            assertTrue(target.contains("\"tweakNodeStatus\":\"active\""), version);
            assertTrue(target.contains("\"ddsPublishesTweakOwnership\":true"), version);
        }

        for (String version : new String[]{
                "1.20.1", "1.19.4", "1.18.2", "1.17.1",
                "1.16.5", "1.15.2", "1.14.4"
        }) {
            String target = target(contract, version);
            assertTrue(target.contains("\"wireMode\":\"LEGACY_CUSTOM_PAYLOAD\""), version);
            assertTrue(target.contains("\"tweakNodeStatus\":\"active\""), version);
            assertTrue(target.contains("\"ddsPublishesTweakOwnership\":true"), version);
        }

        String gradle = read("common.gradle");
        assertTrue(gradle.contains("config/dds-client-ownership-contract.json"));
        assertTrue(gradle.contains("productionHandoffVersions"));
        assertTrue(gradle.contains("wire_mode           : handoffWireMode"));
        assertFalse(gradle.contains("preCutoverMode"));
        assertFalse(gradle.contains("preCutoverEligible"));
        assertFalse(gradle.contains("ownershipMarker.pre_cutover_test = true"));
        assertFalse(gradle.contains("verifyPreCutoverArtifact"));

        String rootGradle = read("build.gradle");
        assertFalse(rootGradle.contains("buildPreCutoverAndGather"));
        assertFalse(rootGradle.contains("preCutoverArtifactVerificationTaskPaths"));
        assertTrue(rootGradle.contains("gatherRuntimeJars('releases')"));
    }

    @Test
    void sourceMetadataDoesNotHardcodeGeneratedOwnershipMarker() throws IOException {
        String metadata = read("src/main/resources/fabric.mod.json");
        assertTrue(metadata.contains("\"custom\": {\"modmenu:parent\": \"carpet\"}"));
        assertFalse(metadata.contains("dds:network_ownership"));
    }

    @Test
    void processedMixinConfigHasNoCdaLogicalClientHooks() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("carpet-dds-addition.mixins.json")) {
            assertNotNull(in);
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(com.google.gson.JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("client").size() == 0, "no CDA client hook may remain");
            for (String name : new String[]{
                    "network.ClientboundCustomPayloadPacketMixin",
                    "network.ServerboundCustomPayloadPacketMixin",
                    "network.ServerCustomPayloadMixin"
            }) assertTrue(json.contains("\"" + name + "\""), name);
        }
    }
    @Test
    void serverProtocolSurvivesCdaLogicalClientPhysicalRetirement() throws IOException {
        String plugin = read("src/main/java/carpetddsaddition/mixin/DdsMixinConfigPlugin.java");
        String json = read("src/main/resources/carpet-dds-addition.mixins.json");
        String gradle = read("common.gradle");
        assertFalse(plugin.contains("LOGICAL_CLIENT_MIXINS"));
        assertFalse(plugin.contains("logicalClientHandedOff"));
        assertTrue(gradle.contains("final Set<String> logicalClientMixins = [] as Set"));
        assertTrue(com.google.gson.JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("client").size() == 0);
        for (String name : new String[]{
                "network.ClientboundCustomPayloadPacketMixin",
                "network.ServerboundCustomPayloadPacketMixin",
                "network.ServerCustomPayloadMixin",
                "feature.quickcontaineraccess.ServerContainerClickMixin"
        }) assertTrue(json.contains("\"" + name + "\""), name);
        assertTrue(gradle.contains("handoffWireMode"));
        assertTrue(read("src/main/java/carpetddsaddition/network/DdsServerNetwork.java")
                .contains("DdsProtocol.HELLO_C2S.equals(packetId)"));
    }
    @Test
    void p7UndoClientSourceRemovedButServerAuthorityPreserved() throws IOException {
        assertFalse(Files.exists(root().resolve(
                "src/main/java/carpetddsaddition/mixin/feature/undoredo/KeyboardHandlerMixin.java")));
        assertFalse(Files.exists(root().resolve(
                "src/main/java/carpetddsaddition/feature/undoredo/network/UndoRedoClientNetwork.java")));
        String mixins = read("src/main/resources/carpet-dds-addition.mixins.json");
        assertFalse(mixins.contains("feature.undoredo.KeyboardHandlerMixin"));
        String server = read("src/main/java/carpetddsaddition/feature/undoredo/network/UndoRedoServerNetwork.java");
        assertTrue(server.contains("UndoManager.undo(player)"));
        assertTrue(server.contains("UndoManager.redo(player)"));
        String bootstrap = read("src/main/java/carpetddsaddition/core/bootstrap/DdsNetworkBootstrap.java");
        assertTrue(bootstrap.contains("UndoRedoServerNetwork.registerPackets()"));
    }

    @Test
    void p7QcaClientRemovedAndServerAuthorityRemains() throws IOException {
        for (String path : new String[]{
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessClient.java",
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessDtCompat.java",
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessClientNetwork.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/ClientboundContainerClosePacketAccessor.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/ClientContainerCloseMixin.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/ClientPredictionMixin.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/CreativeModeInventoryScreenAccessor.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/CreativeSlotWrapperAccessor.java",
                "src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/ContainerScreenInputMixin.java"
        }) assertFalse(Files.exists(root().resolve(path)), path);
        String mixins = read("src/main/resources/carpet-dds-addition.mixins.json");
        assertFalse(mixins.contains("feature.quickcontaineraccess.Client"));
        assertFalse(mixins.contains("feature.quickcontaineraccess.Creative"));
        assertFalse(mixins.contains("feature.quickcontaineraccess.ContainerScreenInputMixin"));
        assertTrue(mixins.contains("feature.quickcontaineraccess.ServerContainerClickMixin"));
        assertTrue(read("src/main/java/carpetddsaddition/core/bootstrap/DdsNetworkBootstrap.java")
                .contains("QuickContainerAccessServerNetwork.registerPackets()"));
        assertTrue(read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java")
                .contains("STORAGE_CLICK_C2S"));
    }

    @Test
    void clientNetworkFilesRemovedButServerHandshakeRemains() throws IOException {
        for (String name : new String[]{
                "src/main/java/carpetddsaddition/mixin/network/ClientCustomPayloadMixin.java",
                "src/main/java/carpetddsaddition/mixin/network/ClientHandshakeMixin.java",
                "src/main/java/carpetddsaddition/network/DdsClientNetwork.java",
                "src/main/java/carpetddsaddition/network/DdsClientState.java"
        }) assertFalse(Files.exists(root().resolve(name)), name);
        String server = read("src/main/java/carpetddsaddition/network/DdsServerNetwork.java");
        assertTrue(server.contains("DdsProtocol.HELLO_C2S.equals(packetId)"));
        assertTrue(server.contains("DdsProtocol.HELLO_S2C"));
        assertTrue(read("src/main/java/carpetddsaddition/network/DdsPacketRegistry.java")
                .contains("clientS2CPackets()"));
    }

}
