package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessOfflineOrgLeaseContractTest {
    private static final Path ROOT=findRepositoryRoot(),JAVA=ROOT.resolve("src/main/java");

    @Test void orgOfflineMenusRemainNativeWithoutQcaOrLeaseHooks() throws IOException {
        String compat=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String network=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String mixins=Files.readString(ROOT.resolve("src/main/resources/carpet-dds-addition.mixins.json"));

        assertTrue(compat.contains("isOrgOfflineMenu(AbstractContainerMenu menu)"));
        assertTrue(compat.contains("type.endsWith(\".OfflinePlayerInventory\")"));
        assertTrue(compat.contains("type.endsWith(\".OfflinePlayerEnderChestInventory\")"));
        assertTrue(compat.contains("|| isOrgOfflineMenu(menu) || !menu.stillValid(player)"));
        assertTrue(menus.contains("if (QuickContainerAccessCompat.isOrgOfflineMenu(currentMenu)) return false"));
        assertTrue(menus.contains("closeForSwitch(player, currentMenu)"));
        assertTrue(network.contains("rejectTransition(viewer, request, \"unsupported_target\")"));
        assertFalse(menus.contains("QuickContainerAccessOfflineOrgLease."));
        assertFalse(session.contains("QuickContainerAccessOfflineOrgLease."));
        assertFalse(session.contains("authorizedOfflineOrgRoot"));
        assertFalse(session.contains("preserveOfflineOrgSourceUntilOpen"));
        assertFalse(mixins.contains("\"integration.org.OfflinePlayerAccessorLeaseMixin\""));
        assertFalse(mixins.contains("\"integration.org.OfflinePlayerStopOpenLeaseMixin\""));
        assertFalse(mixins.contains("\"integration.org.OrgOfflineServerCloseMixin\""));
    }

    private static String read(String path) throws IOException { return Files.readString(JAVA.resolve(path)); }
    private static Path findRepositoryRoot(){
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
