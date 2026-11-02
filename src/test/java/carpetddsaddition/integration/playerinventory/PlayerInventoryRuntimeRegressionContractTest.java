package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class PlayerInventoryRuntimeRegressionContractTest {
    private static final Path ROOT=findRepositoryRoot(), JAVA=ROOT.resolve("src/main/java");

    @Test void strictEnderAdvertisesOnlyVisibleControlsAndRejectsRemovedBatchActions() throws IOException {
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String actions=read("carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");

        assertTrue(network.contains("STRICT_ENDER_ACTIONS=actions(DdsFakePlayerAction.TOGGLE_SNEAK,DdsFakePlayerAction.DISCONNECT)"));
        assertTrue(network.contains("strictSupportedActions(viewer,session)"));

        int strictStart=actions.indexOf("private static boolean handleStrictSessionRequest");
        int strictEnd=actions.indexOf("private static boolean isStrictInventoryAction",strictStart);
        assertTrue(strictStart>=0&&strictEnd>strictStart);
        String strict=actions.substring(strictStart,strictEnd);
        assertTrue(strict.contains("case TOGGLE_SNEAK"));
        assertTrue(strict.contains("case DISCONNECT"));
        assertFalse(strict.contains("case ENDER_DROP_ALL"));
        assertFalse(strict.contains("case ENDER_STORE_ALL"));
        assertFalse(strict.contains("case ENDER_TAKE_ALL"));
    }

    @Test void nestedProviderEnderBackPrefersCapturedProviderRootOverCanonicalExpansion() throws IOException {
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String navigation=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");

        assertTrue(reopen.contains("PlayerEnderChestContainer"));
        assertTrue(reopen.contains("DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()"));
        int root=navigation.indexOf("DdsReopenableMenu root = ROOTS.get(session.sourceRef)");
        int parent=navigation.indexOf("QuickContainerAccessSession.PathNode parent = session.path.parent",root);
        assertTrue(root>=0&&parent>root);
        String back=navigation.substring(navigation.indexOf("static boolean navigateBack"),navigation.indexOf("static void clear"));
        assertTrue(back.indexOf("root.ddsReopen(player)")<back.indexOf("QuickContainerAccessMenus.navigateBack(player, menu, parent)"));
    }

    @Test void strictProviderActionsCarryGenerationAndCoverInventoryAndEnder() throws IOException {
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String actions=read("carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");

        assertTrue(network.contains("strictSupportedActions(viewer,session)"));
        assertTrue(network.contains("InventoryKind.ENDER_CHEST"));
        assertTrue(network.contains("INVENTORY_ACTIONS"));
        assertTrue(network.contains("STRICT_ENDER_ACTIONS"));

        assertTrue(actions.contains("\"session_generation\""));
        assertTrue(actions.contains("DdsPlayerInventorySessions.current("));
        assertTrue(actions.contains("viewer, containerId, expectedTarget, generation"));
        assertTrue(actions.contains("handleStrictSessionRequest"));
        assertTrue(actions.contains("InventoryKind.ENDER_CHEST"));
    }

    @Test void qcaRemoteAndNestedEnderUseProviderAuthorizedCapacity() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String factory=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenuFactory.java");

        assertTrue(session.contains("remoteEnderVisibleSize"));
        assertTrue(session.contains("DdsCanonicalSlotMapping"));
        assertTrue(menus.contains("destination.openedSize"));
        assertTrue(menus.contains("providerAuthorizedEnderSize(viewer, currentMenu, owner)"));
        assertTrue(factory.contains("canonical.getContainerSize() < openedSize"));
        assertTrue(factory.contains("new PortableEnderContainer(owner, canonical, openedSize)"));
        assertTrue(factory.contains("implements Container, DdsInventoryOwnerView, DdsDirectSlotAccess, DdsCanonicalSlotMapping"));
    }

    @Test void gcaReopenedInventoryKeepsItsRecognizableProviderTitle() throws IOException {
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        assertTrue(reopen.contains("Component.translatable(\"gca.player.inventory\", owner.getDisplayName())"));
        assertTrue(reopen.contains("Component.translatable(\"gca.player.ender_chest\", owner.getDisplayName())"));
    }

    @Test void fakeIdentityAuthorityRemainsIndependentOfPlayerInventoryExtension() throws IOException {
        String identity=read("carpetddsaddition/network/DdsFakePlayerIdentitySync.java");
        String bootstrap=read("carpetddsaddition/core/bootstrap/DdsNetworkBootstrap.java");

        assertTrue(identity.contains("all online Carpet fakes, independent of inventory rules"));
        assertTrue(identity.contains("DdsServerNetwork.doesClientSupportS2C(viewer, SNAPSHOT_S2C)"));
        assertTrue(identity.contains("player instanceof EntityPlayerMPFake"));
        assertFalse(identity.contains("PlayerInventoryExtension"));
        assertTrue(bootstrap.contains("DdsFakePlayerIdentitySync.registerPackets()"));
    }

    @Test void gcaDeniedSneakEnderIsTerminalWithoutExtensionOrLegacyUiGates() throws IOException {
        String guard=read("carpetddsaddition/integration/gca/fakeplayer/DdsGcaDeniedEnderIntentGuard.java");
        String bridge=read("carpetddsaddition/integration/gca/fakeplayer/DdsGcaFakePlayerBridge.java");
        String mixin=read("carpetddsaddition/mixin/integration/carpet/gca/FakePlayerInteractionMixin.java");

        assertTrue(guard.contains("viewer.isShiftKeyDown()"));
        assertTrue(guard.contains("!viewer.isSpectator()"));
        assertTrue(guard.contains("target.isAlive()"));
        assertTrue(guard.contains("DdsGcaFakePlayerBridge.shouldSuppressSneakControllerFallback(viewer)"));
        assertFalse(guard.contains("FakePlayerInventory"));
        assertFalse(guard.contains("PlayerInventoryExtension"));

        int bridgeStart=bridge.indexOf("public static boolean shouldSuppressSneakControllerFallback");
        int bridgeEnd=bridge.indexOf("private static boolean gcaEnderChestRuleAllows",bridgeStart);
        assertTrue(bridgeStart>=0&&bridgeEnd>bridgeStart);
        String gate=bridge.substring(bridgeStart,bridgeEnd);
        assertTrue(gate.contains("OPEN_ENDER_CHEST_RULE_FIELD != null"));
        assertTrue(gate.contains("isGcaInventoryRuleEnabled()"));
        assertTrue(gate.contains("!gcaEnderChestRuleAllows(viewer)"));
        assertFalse(gate.contains("supportsEnhancedMenu"));
        assertFalse(gate.contains("supportsPlayerInventoryExtension"));

        assertTrue(mixin.contains("if (!DdsGcaDeniedEnderIntentGuard.shouldConsume(viewer, target)) return;"));
        assertFalse(mixin.contains("DdsFakePlayerAccess"));
        assertTrue(mixin.contains("cir.setReturnValue(InteractionResult.CONSUME)"));
    }

    @Test void extensionCanonicalCanExpandOnlyExplicitWholeEnderProviders() throws IOException {
        String menu=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");

        assertTrue(menu.contains("providerAuthorizesWholeEnder"));
        assertTrue(menu.contains("\"org\".equals(providerId)"));
        assertTrue(menu.contains("PlayerEnderChestScreenHandler"));
        assertTrue(menu.contains("DdsRemotePlayerOwnerResolver.owner(menu) == target"));
        assertTrue(menu.contains("\"gca\".equals(providerId)"));
        assertTrue(menu.contains("DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()"));
        assertFalse(menu.contains("DdsGcaFakePlayerBridge.shouldOpenGcaEnderChest(viewer)"));
        assertTrue(menu.contains("PlayerEnderChestContainer"));
        assertTrue(menu.contains("new ProviderSlotPolicy(new Slot(canonical, i, 0, 0))"));
        assertTrue(network.contains("source, viewer, target, session.providerId(), canonical"));
    }

    @Test void gcaProviderReopenUsesVisibleRowsAndPersistentRuleAuthorization() throws IOException {
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String bridge=read("carpetddsaddition/integration/gca/fakeplayer/DdsGcaFakePlayerBridge.java");

        assertTrue(reopen.contains("providerRows(menu, source)"));
        assertTrue(reopen.contains("slot.container == source"));
        assertTrue(reopen.contains("return rows == 3 || rows == 6 ? rows : 0"));
        assertFalse(reopen.contains("source.getContainerSize() / 9"));
        assertTrue(reopen.contains("DdsGcaFakePlayerBridge.isGcaInventoryRuleEnabled()"));
        assertTrue(reopen.contains("DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()"));
        assertFalse(reopen.contains("getField(field).getBoolean(null)"));

        assertTrue(bridge.contains("shouldOpenGcaEnderChest(ServerPlayer viewer)"));
        assertTrue(bridge.contains("isGcaEnderChestRuleEnabled()"));
        assertTrue(bridge.contains("\"ender_chest\".equals(value)"));
    }

    private static String read(String path) throws IOException {
        return Files.readString(JAVA.resolve(path));
    }

    private static Path findRepositoryRoot() {
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
