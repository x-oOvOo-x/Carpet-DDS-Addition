package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class PlayerInventoryStructureLifecycleContractTest {
    private static final Path ROOT=findRepositoryRoot(), JAVA=ROOT.resolve("src/main/java");

    @Test void canonicalTransferCommitsCarriedOnlyAfterSuccessfulOpen() throws IOException {
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");

        int saved=network.indexOf("ItemStack carried = source.getCarried().copy()");
        int cleared=network.indexOf("source.setCarried(ItemStack.EMPTY)", saved);
        int opened=network.indexOf("viewer.openMenu", cleared);
        int failed=network.indexOf("restoreCarried(viewer, source, carried)", opened);
        int committed=network.indexOf("viewer.containerMenu.setCarried(carried)", failed);
        int accepted=network.indexOf("accept(viewer, request)", committed);

        assertTrue(saved >= 0 && cleared > saved && opened > cleared);
        assertTrue(failed > opened && committed > failed && accepted > committed);
        assertTrue(network.contains("viewer.getInventory().add(remaining)"));
        assertTrue(network.contains("viewer.drop(remaining, false, true)"));
    }

    @Test void returnParentUsesSameCarriedTransactionAndNeverAcceptsFailedReopen() throws IOException {
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        int returnHandler=network.indexOf("private static void handleReturnParent");
        int saved=network.indexOf("ItemStack carried = source.getCarried().copy()", returnHandler);
        int cleared=network.indexOf("source.setCarried(ItemStack.EMPTY)", saved);
        int reopen=network.indexOf("root.ddsReopen(viewer)", cleared);
        int failed=network.indexOf("restoreCarried(viewer, source, carried)", reopen);
        int committed=network.indexOf("viewer.containerMenu.setCarried(carried)", failed);
        int accepted=network.indexOf("accept(viewer, request)", committed);

        assertTrue(returnHandler >= 0 && saved > returnHandler && cleared > saved);
        assertTrue(reopen > cleared && failed > reopen && committed > failed && accepted > committed);
    }

    @Test void canonicalContainerLifecycleIsBalancedAndProviderPolicyIsRetained() throws IOException {
        String canonical=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");

        assertEquals(1, count(canonical, "canonical.startOpen(viewer)"));
        assertEquals(1, count(canonical, "canonical.stopOpen(player)"));
        assertTrue(canonical.contains("boolean complete = true"));
        assertTrue(canonical.contains("if (complete) return out"));
        assertTrue(canonical.contains("if (!providerAuthorizesWholeEnder(menu, viewer, target, providerId, canonical)) return null"));
        assertTrue(canonical.contains("if (out[i] == null) out[i] = new ProviderSlotPolicy(new Slot(canonical, i, 0, 0))"));
        assertTrue(canonical.contains("policy.mayPickup(player) && super.mayPickup(player)"));
        assertTrue(canonical.contains("policy.mayPlace(stack) && super.mayPlace(stack)"));
        assertTrue(canonical.contains("policies[slot].mayPlace(stack) && delegate.canPlaceItem(slot, stack)"));
    }

    @Test void canonicalMenuRevalidatesProviderWholeEnderAuthorization() throws IOException {
        String canonical=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");

        int stillValid=canonical.indexOf("@Override public boolean stillValid(Player player)");
        int removed=canonical.indexOf("@Override public void removed(Player player)",stillValid);
        assertTrue(stillValid>=0&&removed>stillValid);
        String gate=canonical.substring(stillValid,removed);
        assertTrue(gate.contains("\"gca\".equals(providerId) || \"org\".equals(providerId)"));
        assertTrue(gate.contains("providerAuthorizesWholeEnder(authorizationMenu, viewer, target, providerId, canonical)"));
        assertTrue(gate.contains("authorizationMenu.stillValid(viewer)"));
    }

    @Test void structuralTransportIsRuleAndStrictCapabilityGatedWithoutQcaAuthority() throws IOException {
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");

        assertTrue(network.contains("player -> PlayerInventoryExtension.enabled()"));
        assertTrue(network.contains("SESSION_V3_CAPABILITY_S2C"));
        assertTrue(network.contains("STRUCTURE_RESULT_S2C"));
        assertTrue(network.contains("if (!strictNegotiated(viewer))"));
        assertFalse(network.contains("QuickContainerAccess"));
        assertTrue(sessions.contains("if (!PlayerInventoryExtension.enabled()"));
    }

    @Test void rejectAndDisconnectClearOnlyStructuralTransitionState() throws IOException {
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String roots=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryRootReturns.java");
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        String mixin=read("carpetddsaddition/mixin/integration/playerinventory/PlayerListMixin.java");
        String mixins=Files.readString(ROOT.resolve("src/main/resources/carpet-dds-addition.mixins.json"));

        assertTrue(network.contains("rejectTransition(ServerPlayer viewer, Request request, String reason)"));
        assertTrue(network.contains("DdsPlayerInventoryRootReturns.reject(viewer, request.sessionGeneration)"));
        assertTrue(network.contains("private static void reject(ServerPlayer viewer, Request request, String reason)"));
        assertTrue(roots.contains("nextGenerationMatches("));
        assertTrue(roots.contains("public static void clear(ServerPlayer viewer)"));
        assertTrue(sessions.contains("public static void clearViewer(ServerPlayer viewer)"));
        assertTrue(network.contains("REQUESTS.clear(viewer.connection)"));
        assertTrue(mixin.contains("@Inject(method = \"remove\", at = @At(\"HEAD\"))"));
        assertTrue(network.contains("DdsPlayerInventorySessions.clearViewer(viewer)"));
        assertTrue(mixin.contains("DdsPlayerInventoryEnderNetwork.onDisconnect(player)"));
        assertFalse(mixin.contains("DdsPlayerInventorySessions.clearViewer(player)"));
        assertTrue(mixins.contains("integration.playerinventory.PlayerListMixin"));
    }

    private static int count(String text, String token) {
        int count=0, from=0;
        while ((from=text.indexOf(token, from)) >= 0) {
            count++;
            from += token.length();
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return Files.readString(JAVA.resolve(path));
    }

    private static Path findRepositoryRoot() {
        for (Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
