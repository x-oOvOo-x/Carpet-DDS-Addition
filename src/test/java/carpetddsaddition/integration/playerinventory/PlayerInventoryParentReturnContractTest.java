package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class PlayerInventoryParentReturnContractTest {
    private static final Path ROOT=findRepositoryRoot(),JAVA=ROOT.resolve("src/main/java");

    @Test void parentTokenComesOnlyFromAnActualEnderNavigation() throws IOException {
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String navigation=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String roots=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryRootReturns.java");
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String qcaReopen=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessReopenCompat.java");

        assertTrue(compat.contains("attachSupportedRoot(ref, menu, slot, serverPlayer, kind)"));
        assertTrue(navigation.contains("kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST"));
        assertTrue(navigation.contains("DdsPlayerInventoryRootReturns.capture"));
        assertTrue(roots.contains("DdsPlayerInventoryStructureRequestGuard.parentMatches("));
        assertTrue(roots.contains("capture.sourceProvider, enderSession.providerId()"));
        assertTrue(roots.contains("capture.sourceTarget, enderSession.liveTarget()"));
        assertTrue(roots.contains("capture.sourceGeneration, enderSession.generation()"));
        assertTrue(reopen.contains("forInventory"));
        assertTrue(reopen.contains("menu.stillValid(candidate)"));
        assertTrue(qcaReopen.contains("DdsPlayerInventoryReopenCompat.forInventory"));
    }

    @Test void offlineOrgMenusCannotDetachTheQcaSourceGuardOrReopen() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String reopen=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        assertTrue(compat.contains("isOrgOfflineMenu(AbstractContainerMenu menu)"));
        assertTrue(menus.contains("QuickContainerAccessCompat.isOrgOfflineMenu(currentMenu)"));
        assertTrue(session.contains("vanillaEnderRoot ? null : menu"));
        assertFalse(session.contains("authorizedOfflineOrgRoot"));
        assertFalse(session.contains("preserveOfflineOrgSourceUntilOpen"));
        assertFalse(compat.contains("hasAuthorizedNestedParent("));
        assertFalse(reopen.contains("offlineOrg("));
    }

    @Test void directEnderNeverSynthesizesAParentFromPreviousSession() throws IOException {
        String sessions=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        assertFalse(sessions.contains("inheritRoot("));
        assertFalse(sessions.contains("previous.rootReturn()"));
        assertTrue(network.contains("session.rootReturn()"));
    }

    private static String read(String path) throws IOException { return Files.readString(JAVA.resolve(path)); }
    private static Path findRepositoryRoot(){
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
