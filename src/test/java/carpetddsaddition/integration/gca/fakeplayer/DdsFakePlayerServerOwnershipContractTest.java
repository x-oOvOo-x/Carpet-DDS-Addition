/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class DdsFakePlayerServerOwnershipContractTest {
    private static final Path ROOT=findRepositoryRoot(),FAKE=ROOT.resolve("src/main/java/carpetddsaddition/integration/gca/fakeplayer");
    @Test void fakePlayerClientImplementationHasMovedToTweak() throws IOException {
        assertFalse(Files.exists(FAKE.resolve("client")));assertFalse(Files.exists(FAKE.resolve("network/DdsFakePlayerClientNetwork.java")),"fake-player logical client network belongs only to DDS Tweak");
        assertFalse(Files.exists(FAKE.resolve("DdsFakePlayerMenu.java")));
        assertFalse(Files.exists(FAKE.resolve("DdsFakePlayerEnderChestMenu.java")));
        for(String p:new String[]{"src/main/java/carpetddsaddition/mixin/integration/carpet/gca/client/MenuScreensMixin.java","src/main/java/carpetddsaddition/mixin/integration/inventoryprofilesnext/IpnAreaTypesMixin.java","src/main/java/carpetddsaddition/mixin/integration/inventoryprofilesnext/IpnHintsManagerMixin.java","src/main/java/carpetddsaddition/mixin/integration/inventoryprofilesnext/DdsFakePlayerScreenIpnHintsMixin.java","src/main/java/carpetddsaddition/mixin/integration/inventoryprofilesnext/DdsFakePlayerEnderChestScreenIpnHintsMixin.java","src/main/java/carpetddsaddition/mixin/integration/minihud/MiniHudInventoryPreviewMixin.java","src/main/java/carpetddsaddition/platform/fabric/DdsBuiltinResourcePackCompat.java","src/main/java/carpetddsaddition/platform/fabric/DdsFabricClientInitializer.java"})assertFalse(Files.exists(ROOT.resolve(p)),p);
        String metadata=read("src/main/resources/fabric.mod.json"),mixins=read("src/main/resources/carpet-dds-addition.mixins.json");assertFalse(metadata.contains("DdsFabricClientInitializer"));assertFalse(mixins.contains("integration.carpet.gca.client.MenuScreensMixin"));assertFalse(mixins.contains("integration.inventoryprofilesnext.Ipn"));assertFalse(mixins.contains("integration.inventoryprofilesnext.DdsFakePlayer"));assertFalse(mixins.contains("integration.minihud.MiniHudInventoryPreviewMixin"));
    }
    @Test void serverRegistryAndActiveSessionPacketsSurviveClientRetirement() throws IOException {
        assertFalse(Files.exists(ROOT.resolve("src/main/java/carpetddsaddition/network/DdsClientNetwork.java")));
        String registry=read("src/main/java/carpetddsaddition/network/DdsPacketRegistry.java");
        String server=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        assertTrue(registry.contains("clientC2SPackets()"));
        assertTrue(registry.contains("clientS2CPackets()"));
        assertTrue(server.contains("ACTION_C2S, DdsFakePlayerActions::handleRequest, true, false, false"));
        assertFalse(server.contains("OPEN_MENU_S2C"));
        assertTrue(server.contains("DdsFakePlayerPackets.CONTEXT_S2C"));
        assertTrue(server.contains("DdsFakePlayerPackets.STATE_S2C"));
    }
    @Test void providerActionTransportValidatesCurrentRemoteFakeSession() throws IOException {
        String actions=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");
        String network=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String packets=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerPackets.java");
        String sessions=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(packets.contains("fake_player_provider_action_c2s"));
        assertTrue(network.contains("PROVIDER_ACTION_C2S, DdsFakePlayerActions::handleProviderRequest"));
        String provider=method(actions,"public static void handleProviderRequest","private static boolean handleStrictSessionRequest");
        assertTrue(provider.contains("containerId != current.containerId"));
        assertTrue(provider.contains("DdsPlayerInventorySessions.current(viewer, containerId, expectedTarget)"));
        assertTrue(provider.contains("session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_FAKE"));
        assertTrue(provider.contains("session.inventoryKind() != DdsPlayerInventorySession.InventoryKind.ENDER_CHEST"));
        assertTrue(provider.contains("session.liveTarget() instanceof EntityPlayerMPFake"));
        assertTrue(sessions.contains("menu.stillValid(viewer)"));
        assertTrue(sessions.contains("DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches("));
        assertTrue(provider.contains("isProviderAction(action)"));
        String gate=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsProviderStopGate.java");
        assertTrue(gate.contains("action == DdsFakePlayerAction.STOP_ATTACK || action == DdsFakePlayerAction.STOP_USE"));
        assertFalse(provider.contains("case DROP_ALL: case DISCONNECT: case SELECT_HOTBAR: return true"));
        assertTrue(actions.contains("pack.start(type, null)"));
    }
    @Test void legacyProviderStopFallsBackToCurrentProviderInventoryWithoutExtensionSession() throws IOException {
        String actions=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");
        String gate=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsProviderStopGate.java");
        String provider=method(actions,"public static void handleProviderRequest","private static boolean handleStrictSessionRequest");
        assertTrue(provider.contains("DdsRemotePlayerOwnerResolver.remoteInventoryOwnerForMenu(viewer, current)"));
        assertTrue(provider.contains("session != null"));
        assertFalse(provider.contains("if (session == null || session.targetType()"));
        assertTrue(provider.contains("server.getPlayerList().getPlayer(expectedTarget) == legacyTarget"));
        assertTrue(provider.contains("DdsProviderStopGate.allowsLegacy(action"));
        assertTrue(gate.contains("providerInventory && targetUuidMatches"));
        assertTrue(gate.contains("liveInstanceMatches && fake && alive"));
    }

    @Test void nativeMenuRetirementPreservesExactServerScratchInterface() throws IOException {
        for(String name:new String[]{"DdsFakePlayerMenu.java","DdsFakePlayerEnderChestMenu.java",
                "DdsMappedContainer.java","DdsFakePlayerEquipmentSlot.java","DdsFakePlayerAccess.java"})
            assertFalse(Files.exists(FAKE.resolve(name)),name);
        String marker=read("src/main/java/carpetddsaddition/core/inventory/DdsCarriedScratchSwap.java");
        String canonical=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        assertTrue(marker.contains("interface DdsCarriedScratchSwap"));
        assertTrue(canonical.contains("DdsCarriedScratchSwap"));
    }
    @Test void nativeGcaInventoryScratchNeverUsesViewerSlots() throws IOException {
        String context=read("src/main/java/carpetddsaddition/mixin/integration/carpet/gca/GcaPlayerInventoryMenuContextMixin.java");
        String scratch=read("src/main/java/carpetddsaddition/mixin/integration/carpet/gca/GcaPlayerInventoryScratchMixin.java");
        String mixins=read("src/main/resources/carpet-dds-addition.mixins.json");
        assertTrue(context.contains("implements DdsCarriedScratchSwap"));
        assertFalse(context.contains("@Inject(method = \"clicked\""),
                "PlayerInventoryMenu inherits clicked; injecting there crashes at runtime");
        assertTrue(scratch.contains("@Mixin(AbstractContainerMenu.class)"));
        assertTrue(scratch.contains("GCA_PLAYER_INVENTORY_MENU.equals(self.getClass().getName())"));
        assertTrue(scratch.contains("DDS_SORT_SCRATCH_BUTTON = 9"));
        assertTrue(scratch.contains("GCA_STORAGE_START = 18"));
        assertTrue(scratch.contains("GCA_STORAGE_END = 54"));
        assertTrue(scratch.contains("slotId >= GCA_STORAGE_START && slotId < GCA_STORAGE_END"));
        assertTrue(scratch.contains("menu.getCarried()"));
        assertTrue(scratch.contains("menu.setCarried(slotStack)"));
        assertFalse(scratch.contains("viewerInventory"));
        assertFalse(scratch.contains("moveItemStackTo"));
        assertTrue(mixins.contains("integration.carpet.gca.GcaPlayerInventoryScratchMixin"));
    }
    @Test void serverAuthorityAndProtocolRemainAfterLegacySenderRemoval() throws IOException {
        String actions=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");
        String network=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String packets=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerPackets.java");
        assertTrue(actions.contains("handleRequest(ServerPlayer viewer"));
        assertTrue(actions.contains("current.stillValid(viewer)"));
        assertTrue(actions.contains("handleStrictSessionRequest(viewer, current, action, value, data)"));
        assertFalse(network.contains("sendOpenMenu("));
        assertFalse(network.contains("DdsFakePlayerPackets.OPEN_MENU_S2C"));
        assertTrue(packets.contains("fake_player_action_c2s"));
        assertFalse(packets.contains("fake_player_open_menu_s2c"));
    }
    private static String method(String source,String startToken,String endToken){
        int start=source.indexOf(startToken);
        int end=source.indexOf(endToken,start);
        assertTrue(start>=0&&end>start);
        return source.substring(start,end);
    }
    private static String read(String p)throws IOException{return Files.readString(ROOT.resolve(p));}
    private static Path findRepositoryRoot(){for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;throw new IllegalStateException("Unable to locate repository root");}
}
