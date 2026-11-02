package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class DdsPlayerInventorySessionContractTest {
    private static final Path ROOT=findRepositoryRoot(),JAVA=ROOT.resolve("src/main/java");

    @Test void currentSessionRevalidatesMenuTargetAndLiveInstance() throws IOException {
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(sessions.contains("menu == null || menu.containerId != containerId"));
        assertTrue(sessions.contains("DdsPlayerInventoryStructureRequestGuard.bindingMatches("));
        assertTrue(sessions.contains("session.generation() <= 0L"));
        assertTrue(sessions.contains("DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches("));
        assertTrue(sessions.contains("expectedTarget != null && !expectedTarget.equals(session.targetUuid())"));
        assertTrue(sessions.contains("menu.stillValid(viewer)"));
        assertTrue(sessions.contains("DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches("));
        assertTrue(sessions.contains("session.targetType() == DdsPlayerInventorySession.TargetType.OFFLINE_PLAYER"));
    }

    @Test void providerKindsAreExplicitAndCanonicalEnderIsNotInferredFromInventory() throws IOException {
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(sessions.contains("PlayerEnderChestScreenHandler"));
        assertTrue(sessions.contains("InventoryKind.ENDER_CHEST"));
        assertTrue(sessions.contains("canonical != target.getEnderChestInventory()"));
        assertTrue(sessions.contains("QuickContainerAccessMenuFactory$PortableEnderContainer"));
        assertTrue(sessions.contains("!PlayerInventoryExtension.enabled()"));
        assertTrue(sessions.contains("size != 27 && size != 54"));
        assertTrue(sessions.contains("PlayerInventoryScreenHandler"));
        assertTrue(sessions.contains("private static int providerSlotCount(AbstractContainerMenu menu, Container container)"));
        assertTrue(sessions.contains("kind = providerSlotCount(menu, container) <= 27"));
        assertTrue(sessions.contains("WithButtonPlayerInventoryScreenHandler"));
    }

    @Test void providerConstructorsPublishContextBeforeVanillaOpenScreen() throws IOException {
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String gcaInventory=read("carpetddsaddition/mixin/integration/carpet/gca/GcaPlayerInventoryMenuContextMixin.java");
        String chest=read("carpetddsaddition/mixin/integration/playerinventory/PlayerInventoryChestMenuContextMixin.java");
        String org=read("carpetddsaddition/mixin/integration/org/OrgPlayerMenuContextMixin.java");
        String mixins=Files.readString(ROOT.resolve("src/main/resources/carpet-dds-addition.mixins.json"));
        assertTrue(network.contains("sendContextFromMenu(ServerPlayer viewer, AbstractContainerMenu menu)"));
        assertTrue(network.contains("SESSION_V3_CAPABILITY_S2C"));
        assertTrue(network.contains("session_generation"));
        assertTrue(network.contains("state_revision"));
        assertTrue(network.contains("\"event\",\"clear\""));
        assertTrue(network.contains("DdsFakePlayerPackets.CONTEXT_S2C"));
        assertTrue(network.contains("canonical_size"));
        assertTrue(gcaInventory.contains("@Inject(method = \"<init>\", at = @At(\"RETURN\"))"));
        assertTrue(gcaInventory.contains("DdsPlayerInventorySessions.openingViewer()"));
        assertTrue(chest.contains("sendContextFromMenu"));
        assertTrue(org.contains("PlayerEnderChestScreenHandler"));
        assertTrue(org.contains("WithButtonPlayerInventoryScreenHandler"));
        assertFalse(org.contains("OfflinePlayerInventoryScreenHandler"));
        assertTrue(mixins.contains("integration.org.OrgPlayerMenuContextMixin"));
        assertTrue(mixins.contains("integration.playerinventory.ServerPlayerMenuOpenContextMixin"));
        assertTrue(mixins.contains("integration.playerinventory.PlayerInventoryChestMenuContextMixin"));
        assertFalse(mixins.contains("integration.carpet.gca.GcaChestMenuContextMixin"));
    }

    @Test void sessionGenerationIsConnectionLocalAndPreparedSeparatelyFromActiveAuthority() throws IOException {
        String session=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySession.java");
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(session.contains("viewerConnection"));
        assertTrue(session.contains("generation"));
        assertTrue(sessions.contains("PENDING"));
        assertTrue(sessions.contains("ACTIVE"));
        assertTrue(sessions.contains("GENERATIONS"));
        assertTrue(sessions.contains("prepare(ServerPlayer viewer, AbstractContainerMenu menu)"));
        assertFalse(sessions.contains("public static ServerPlayer viewer(AbstractContainerMenu menu)"));
    }

    @Test void extensionRuleRefreshesCanonicalOpenCapabilityAndClosesInvalidRemoteSessions() throws IOException {
        String extension=read("carpetddsaddition/feature/playerinventoryextension/PlayerInventoryExtension.java");
        assertTrue(extension.contains("QuickContainerAccess.onRuleChanged()"));
        assertTrue(extension.contains("DdsServerNetwork.refreshCapabilities(server)"));
    }

    @Test void carriedScratchCapabilityRequiresExactServerMenuProtocol() throws IOException {
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        String marker=read("carpetddsaddition/core/inventory/DdsCarriedScratchSwap.java");
        String canonical=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        String qca=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenuFactory.java");
        String gca=read("carpetddsaddition/mixin/integration/carpet/gca/GcaPlayerInventoryScratchMixin.java");
        assertTrue(marker.contains("interface DdsCarriedScratchSwap"));
        assertTrue(sessions.contains("menu instanceof DdsCarriedScratchSwap"));
        assertFalse(sessions.contains("kind, container instanceof DdsDirectSlotAccess"));
        assertTrue(canonical.contains("implements DdsEnderMenuFactory,DdsCarriedScratchSwap"));
        assertTrue(qca.contains("PortableEnderMenu extends ChestMenu implements DdsCarriedScratchSwap"));
        assertTrue(gca.contains("DdsCarriedScratchSwap"));
        assertTrue(gca.contains("DDS_SORT_SCRATCH_BUTTON = 9"));
    }

    @Test void liveRealEnderCanCanonicalizeWithoutBecomingFake() throws IOException {
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String menu=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(network.contains("session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_FAKE"));
        assertTrue(network.contains("session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_PLAYER"));
        assertTrue(network.contains("ServerPlayer target = session.liveTarget()"));
        assertTrue(menu.contains("private final ServerPlayer target"));
        assertTrue(menu.contains("public ServerPlayer target()"));
        assertFalse(menu.contains("private final EntityPlayerMPFake target"));
        assertTrue(sessions.contains("target instanceof EntityPlayerMPFake"));
        assertTrue(sessions.contains("DdsPlayerInventorySession.live(canonicalMenu.providerId(), menu.containerId, target,"));
    }

    @Test void orgOfflineMenusDoNotCreateEnhancedSessions() throws IOException {
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        assertTrue(sessions.contains("QuickContainerAccessCompat.isOrgOfflineMenu(menu)"));
        assertTrue(compat.contains("isOrgOfflineMenu(AbstractContainerMenu menu)"));
        assertFalse(sessions.contains("resolveOrgOfflineEnder(menu)"));
        assertFalse(sessions.contains("DdsPlayerInventorySession.offline(\"org\""));
        assertFalse(network.contains("openOfflineCanonicalEnder("));
        assertTrue(network.contains("rejectTransition(viewer, request, \"unsupported_target\")"));
    }

    @Test void liveRealAndFakeSessionsRemainExplicitlyDistinguished() throws IOException {
        String session=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySession.java");
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        assertTrue(session.contains("LIVE_FAKE"));
        assertTrue(session.contains("LIVE_PLAYER"));
        assertTrue(sessions.contains("target instanceof EntityPlayerMPFake"));
        assertFalse(sessions.contains("DdsPlayerInventorySession.offline("));
    }

    private static String read(String path) throws IOException { return Files.readString(JAVA.resolve(path)); }
    private static Path findRepositoryRoot() {
        for (Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
