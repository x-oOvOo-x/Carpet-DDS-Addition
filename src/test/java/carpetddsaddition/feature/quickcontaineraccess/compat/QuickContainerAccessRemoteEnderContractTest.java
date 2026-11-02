package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessRemoteEnderContractTest {
    private static final Path ROOT=findRepositoryRoot(),JAVA=ROOT.resolve("src/main/java");

    @Test void remoteProviderEnderReopensCanonicalTwentySevenOrFiftyFourSlotStorage() throws IOException {
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String factory=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenuFactory.java");
        assertTrue(compat.contains("openRemoteOwnerEnder"));
        assertTrue(compat.contains("PlayerInventoryExtension.enabled()"));
        assertTrue(compat.contains("if (!PlayerInventoryExtension.enabled()"));
        assertFalse(compat.contains("remoteFakePlayerEnderChestEnabled()"));
        assertTrue(compat.contains("remoteOwnerForMenu"));
        assertTrue(compat.contains("owner instanceof EntityPlayerMPFake"));
        assertTrue(compat.contains("isRemoteEnderMenu"));
        assertTrue(menus.contains("openRemoteEnder"));
        assertFalse(menus.contains("attachRemoteEnderRoot"));
        assertTrue(menus.contains("QuickContainerAccessItems.MenuKind.ENDER_CHEST, true"));
        assertTrue(factory.contains("new PortableEnderMenu(MenuType.GENERIC_9x6"));
        assertTrue(factory.contains("new PortableEnderMenu(MenuType.GENERIC_9x3"));
        assertTrue(factory.contains("SORT_SCRATCH_BUTTON=9"));
        assertTrue(factory.contains("swapWithCarried"));
        assertTrue(factory.contains("implements Container, DdsInventoryOwnerView, DdsDirectSlotAccess"));
    }

    @Test void gcaAndOrgOwnershipAreResolvedWithoutMakingEitherAUiProvider() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String org=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessOrgCompat.java");
        String resolver=read("carpetddsaddition/core/inventory/DdsRemotePlayerOwnerResolver.java");
        String gca=read("carpetddsaddition/mixin/integration/carpet/gca/GcaPlayerContainerContextMixin.java");
        assertTrue(session.contains("QuickContainerAccessOrgCompat.owner(value)"));
        assertTrue(session.contains("remoteOwnerForMenu"));
        assertTrue(org.contains("DdsRemotePlayerOwnerResolver.owner(value)"));
        assertTrue(resolver.contains("PlayerInventoryScreenHandler"));
        assertTrue(resolver.contains("PlayerEnderChestScreenHandler"));
        assertTrue(resolver.contains("WithButtonPlayerInventoryScreenHandler"));
        assertFalse(resolver.contains("OfflinePlayerInventory"));
        assertFalse(resolver.contains("OfflinePlayerEnderChestInventory"));
        assertFalse(resolver.contains("fabricPlayer"));
        assertTrue(read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java")
                .contains("isOrgOfflineMenu(AbstractContainerMenu menu)"));
        assertTrue(resolver.contains("getDeclaredField(name)"));
        assertTrue(gca.contains("implements DdsInventoryOwnerView, DdsDirectSlotAccess"));
    }

    @Test void enderBackExistsOnlyForAnActuallyCapturedProviderInventoryParent() throws IOException {
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String navigation=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String reopen=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessReopenCompat.java");

        // A direct sneak-opened Provider Ender is canonicalized, but must not manufacture a back target.
        assertFalse(menus.contains("attachRemoteEnderRoot"));
        assertFalse(navigation.contains("attachRemoteEnderRoot"));
        assertFalse(reopen.contains("forRemoteEnder"));

        // Right-clicking an Ender Chest from an already-open Provider inventory captures that real root.
        assertTrue(compat.contains("rootRefForMenu"));
        assertTrue(compat.contains("QuickContainerAccessNavigation.attachSupportedRoot(ref, menu, slot, serverPlayer, kind)"));
        assertTrue(navigation.contains("if (session.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST)"));
        assertTrue(navigation.contains("return root != null && root.ddsCanReopen(session.player)"));
        assertTrue(reopen.contains("forInventory"));
    }

    @Test void nestedProviderRootsCoverOnlineRealAndFakeOnly() throws IOException {
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String navigation=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String canonical=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        assertTrue(reopen.contains("DdsPlayerInventorySessions.current("));
        assertTrue(reopen.contains("TargetType.LIVE_PLAYER"));
        assertTrue(reopen.contains("TargetType.LIVE_FAKE"));
        assertFalse(reopen.contains("offlineOrg("));
        assertTrue(reopen.contains("DdsFakePlayerServerNetwork.sendContextFromMenu"));
        assertTrue(navigation.contains("if (root != null) return root.ddsCanReopen(viewer)"));
        assertTrue(canonical.contains("implements DdsEnderMenuFactory,DdsCarriedScratchSwap,DdsReopenableMenu"));
    }

    @Test void qcaProviderBackDoesNotDependOnExtensionSessionWhenOff() throws IOException {
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String navigation=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String registry=read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        assertTrue(reopen.contains("!QuickContainerAccess.enabled()"));
        assertTrue(reopen.contains("DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID()) : null"));
        assertTrue(reopen.contains("if (PlayerInventoryExtension.enabled() && (session == null"));
        assertTrue(reopen.contains("(session == null || \"gca\".equals(session.providerId()))"));
        assertTrue(reopen.contains("DdsRemotePlayerOwnerResolver.owner(menu) == owner"));
        assertTrue(reopen.contains("candidate.connection != connection"));
        assertTrue(reopen.contains("menu.stillValid(candidate)"));
        assertTrue(reopen.contains("DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()"));
        assertTrue(navigation.contains("DdsReopenableMenu root = ROOTS.get(session.sourceRef)"));
        assertTrue(registry.contains("player -> PlayerInventoryExtension.enabled() || QuickContainerAccess.enabled()"));
    }

    @Test void canonicalGcaEnderPreservesProviderTitleAcrossNestedReturn() throws IOException {
        String canonical=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        assertTrue(canonical.contains("Component.translatable(\"gca.player.ender_chest\", target.getDisplayName())"));
        assertTrue(canonical.contains("providerTitle(providerId, target)"));
        assertTrue(network.contains("DdsPlayerInventoryCanonicalEnderMenu.providerTitle(session.providerId(), target)"));
        assertFalse(canonical.contains("}, target.getName())).isPresent()"));
    }

    @Test void retiredBatchPacketHasNoHandlerWhileStrictProviderOpenStillWorks() throws IOException {
        assertFalse(Files.exists(JAVA.resolve(
                "carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessOwnerEnderActions.java")));
        String network=read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        assertFalse(network.contains("QuickContainerAccessPackets.OWNER_ENDER_ACTION_C2S"));
        assertFalse(network.contains("(player, data) -> {}, false, true, false, player -> false"));
        assertFalse(network.contains("handleOwnerEnderAction"));
        assertFalse(compat.contains("handleRemoteOwnerEnderAction"));
        assertTrue(network.contains("handleOpenOwnerEnder"));
        assertTrue(compat.contains("QuickContainerAccessMenus.openRemoteEnder(viewer, menu, owner)"));
        assertTrue(compat.contains("QuickContainerAccessSession.isRemoteEnderMenu(viewer, menu, owner)"));
    }

    @Test void failedCanonicalOwnerEnderOpenReportsBackwardCompatibleRejectMarker() throws IOException {
        String network=read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        assertTrue(network.contains("sendOwnerEnderRejected(player, containerId)"));
        assertTrue(network.contains("\"owner_ender_rejected\""));
        assertTrue(network.contains("\"request_container_id\""));
        assertTrue(network.contains("reply.putInt(\"rejected\", 0)"));
        assertTrue(network.contains("QuickContainerAccessCompat.canNavigateBack(player.containerMenu, player)"));
    }

    @Test void canonicalOwnerEnderPacketRemainsExtensionOnlyAfterBatchWireSunset() throws IOException {
        String packets=read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessPackets.java");
        String network=read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        String access=read("carpetddsaddition/feature/quickcontaineraccess/QuickContainerAccess.java");
        assertTrue(packets.contains("qca_open_owner_ender_c2s"));
        assertFalse(packets.contains("qca_owner_ender_action_c2s"));
        assertTrue(network.contains("handleOpenOwnerEnder"));
        assertTrue(network.contains("player -> PlayerInventoryExtension.enabled()"));
        assertTrue(network.contains("PlayerInventoryExtension.enabled() || QuickContainerAccess.enabled()"));
        assertFalse(network.contains("remoteFakePlayerEnderChestEnabled()"));
        assertFalse(access.contains("remoteFakePlayerEnderChestEnabled()"));
        assertFalse(Files.readString(ROOT.resolve("src/main/rules/rules.json"))
                .contains("remoteFakePlayerEnderChest"));
    }

    private static String read(String path) throws IOException { return Files.readString(JAVA.resolve(path)); }
    private static Path findRepositoryRoot() {
        for (Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
