package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessCarriedEnderOriginContractTest {
    private static final Path ROOT = findRoot();

    @Test void vanillaClicksCaptureOnlyRealEnderPickups() throws IOException {
        String s = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCarriedEnderOrigins.java");
        assertTrue(s.contains("MC >= 11404 && MC <= 260300"));
        assertTrue(s.contains("PENDING.put(viewer, new Pickup("));
        assertTrue(s.contains("\"PICKUP\".equals(type) && button == 0"));
        assertTrue(s.contains("\"SWAP\".equals(type) && button == 9"));
        assertTrue(s.contains("pending.slot.getItem() != pending.source"));
        assertTrue(s.contains("carried != pending.previouslyCarried"));
        assertTrue(s.contains("sameItemAndData(carried, pending.snapshot)"));
        assertTrue(s.contains("!previouslyCarried.isEmpty() && !slot.mayPlace(previouslyCarried)"));
        assertTrue(s.contains("!isEnder(origin.carried)"));
        assertTrue(s.contains("origin.carried != carried"));
        assertTrue(s.contains("origin.menu != menu"));
        assertTrue(s.contains("viewer.containerMenu != menu"));
    }

    @Test void stackedEnderBlocksUseVanillaRightClickInsteadOfProvenanceGate() throws IOException {
        String source = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        String items = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessItems.java");

        // The server must independently gate storage gestures on count==1.
        assertTrue(items.contains("return stack.getCount() == 1 && isStorageHost(stack);"));
        int start = source.indexOf("ServerPlayer carriedOwner = player;");
        int end = source.indexOf("StorageHandle carriedStorage =", start);
        assertTrue(start >= 0 && end > start);
        String gate = source.substring(start, end);
        assertTrue(gate.contains("QuickContainerAccessItems.isStorageHostCandidate(carried)"));
        assertTrue(gate.contains("QuickContainerAccessItems.isItem(carried, Items.ENDER_CHEST)"));
        assertTrue(gate.contains("QuickContainerAccessCarriedEnderOrigins.resolve(player, menu, carried)"));
        assertTrue(gate.contains("if (carriedOwner == null) return true;"));
        assertTrue(gate.indexOf("isStorageHostCandidate(carried)") < gate.indexOf("CarriedEnderOrigins.resolve"));
    }

    @Test void movingBetweenViewerAndFakeResetsOwnerAtEveryPickup() throws IOException {
        String origins = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCarriedEnderOrigins.java");
        String storage = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        assertTrue(origins.contains("ORIGINS.put(viewer, new Origin(pending.menu, carried, pending.owner,"));
        assertTrue(origins.contains("ServerPlayer owner = QuickContainerAccessSession.ownerFor(viewer, menu, slot.container);"));
        assertTrue(origins.contains("prune(viewer);"));
        assertTrue(origins.contains("QuickContainerAccessSession.carried(origin.menu, viewer) != origin.carried"));
        assertTrue(storage.contains("QuickContainerAccessCarriedEnderOrigins.resolve(player, menu, carried)"));
        assertTrue(storage.contains("if (carriedOwner == null) return true;"));
    }

    @Test void provenanceClassUsesTheLegacyPreprocessorCore() throws IOException {
        String source = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCarriedEnderOrigins.java");
        int gate = source.indexOf("//#if MC >= 11404 && MC <= 260300");
        int fallback = source.lastIndexOf("//#else");
        assertTrue(gate >= 0 && fallback > gate);
        String active = source.substring(gate, fallback);
        assertTrue(active.contains("final class QuickContainerAccessCarriedEnderOrigins {"));
        assertTrue(active.contains("    static void beforeVanillaClick("));
        assertTrue(active.contains("    private static Object currentSession("));
        assertTrue(active.contains("//#if MC >= 11904"));
        assertTrue(active.contains("//#elseif MC >= 12001"));
        assertTrue(active.contains("//$$         return DdsPlayerInventorySessions.current("));
        assertTrue(active.contains("        return null;"));
        assertTrue(source.substring(fallback).contains("//$$ final class QuickContainerAccessCarriedEnderOrigins"));
    }

    @Test void unknownProviderSourceCannotFallBackToViewerEnder() throws IOException {
        String session = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        int owner = session.indexOf("static ServerPlayer ownerFor(");
        int end = session.indexOf("private static ServerPlayer ownerOf(", owner);
        assertTrue(owner >= 0 && end > owner);
        String body = session.substring(owner, end);
        assertTrue(body.contains("if (container == inventory(viewer)) return viewer;"));
        assertTrue(body.contains("remoteOwnerForMenu(viewer, menu) != null || isPlayerInventoryProviderMenu(menu)"));
        assertTrue(body.contains("return null;"));
        assertTrue(body.contains("dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu"));
        assertTrue(body.contains("WithButtonPlayerInventoryScreenHandler"));
        assertTrue(body.contains("PlayerEnderChestScreenHandler"));
        assertTrue(body.contains("//#if MC >= 11404 && MC <= 260300"));
        assertTrue(body.indexOf("return null;") < body.indexOf("return owner != null ? owner : viewer;"));
    }

    @Test void remoteAuthorizationsStayServerBoundAndRevocable() throws IOException {
        String s = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCarriedEnderOrigins.java");
        String compat = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String server = read("src/main/java/carpetddsaddition/mixin/feature/quickcontaineraccess/ServerContainerClickMixin.java");
        String rule = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/QuickContainerAccess.java");
        assertTrue(s.contains("authorizedStorageEnderSize(viewer, menu, owner)"));
        assertTrue(s.contains("getPlayer(owner.getUUID()) == owner"));
        assertTrue(s.contains("DdsPlayerInventorySessions.current("));
        assertTrue(s.contains("QuickContainerAccessCompat.isOrgOfflineMenu(menu)"));
        assertTrue(s.contains("allowed <= 0 || allowed < origin.capacity"));
        assertTrue(compat.contains("QuickContainerAccessCarriedEnderOrigins.forgetMenu(menu, player)"));
        assertTrue(server.contains("at = @At(\"TAIL\")"));
        assertTrue(server.contains("QuickContainerAccessCompat.beforeVanillaClick("));
        assertTrue(rule.contains("QuickContainerAccessCompat.revokeCarriedOrigins();"));
    }

    @Test void legacyVersionsUseTheSameFailClosedSourceAndMenuChecks() throws IOException {
        String origins = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCarriedEnderOrigins.java");
        String storage = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        String compat = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String sessions = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        assertTrue(origins.contains("//#if MC >= 11404 && MC <= 260300"));
        assertTrue(origins.contains("//#if MC >= 11904"));
        assertTrue(origins.contains("private static Object currentSession("));
        assertTrue(origins.contains("viewer.serverLevel().getServer().getPlayerList().getPlayer(owner.getUUID()) == owner"));
        assertTrue(origins.contains("viewer.level.getServer().getPlayerList().getPlayer(owner.getUUID()) == owner"));
        assertTrue(origins.contains("origin.session != null && currentSession(viewer, menu, owner) != origin.session"));
        assertTrue(storage.contains("//#if MC >= 11404 && MC <= 260300"));
        assertTrue(storage.contains("if (carriedOwner == null) return true;"));
        assertTrue(compat.contains("QuickContainerAccessCarriedEnderOrigins.beforeVanillaClick(player, menu, slotId, button, clickType)"));
        assertTrue(compat.contains("QuickContainerAccessCarriedEnderOrigins.afterVanillaClick(player)"));
        assertTrue(compat.contains("QuickContainerAccessCarriedEnderOrigins.clearAll()"));
        assertTrue(sessions.contains("remoteOwnerForMenu(viewer, menu) != null || isPlayerInventoryProviderMenu(menu)"));
    }

    private static String read(String name) throws IOException {
        return Files.readString(ROOT.resolve(name));
    }
    private static Path findRoot() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        throw new IllegalStateException("Repository root not found");
    }
}
