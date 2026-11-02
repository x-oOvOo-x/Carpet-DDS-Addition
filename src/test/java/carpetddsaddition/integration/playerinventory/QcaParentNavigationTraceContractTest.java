package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class QcaParentNavigationTraceContractTest {
    private static final Path ROOT = findRepositoryRoot();

    @Test void logsAllStagesOnlyWhenExplicitlyEnabled() throws IOException {
        String navigation = read("feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String network = read("feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        String reopen = read("integration/playerinventory/DdsPlayerInventoryReopenCompat.java");

        for (String source : new String[]{navigation, network, reopen})
            assertTrue(source.contains("Boolean.getBoolean(\"dds.playerinventory.trace\")"));

        assertTrue(navigation.contains("qca_parent_capture"));
        assertTrue(navigation.contains("qca_can_back"));
        assertTrue(navigation.contains("qca_back_reopen"));
        assertTrue(network.contains("qca_open_request"));
        assertTrue(network.contains("qca_back_request"));
        assertTrue(network.contains("qca_navigation_state"));
        assertTrue(reopen.contains("qca_parent_reopen reject=precondition"));
        assertTrue(reopen.contains("qca_parent_reopen reject=session"));
        assertTrue(reopen.contains("qca_parent_reopen reject=provider"));
        assertTrue(reopen.contains("qca_parent_reopen reject=constructor"));
        assertTrue(reopen.contains("qca_parent_reopen captured"));
    }

    @Test void originalPermissionAndSessionGatesRemainInPlace() throws IOException {
        String navigation = read("feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java");
        String reopen = read("integration/playerinventory/DdsPlayerInventoryReopenCompat.java");
        String network = read("feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");

        assertTrue(reopen.contains("viewer.containerMenu != menu || !present(owner)"));
        assertTrue(reopen.contains("DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID())"));
        assertTrue(reopen.contains("session.viewerConnection() != viewer.connection"));
        assertTrue(reopen.contains("DdsRemotePlayerOwnerResolver.owner(menu) == owner"));
        assertTrue(reopen.contains("menu.stillValid(candidate)"));
        assertTrue(reopen.contains("candidate.connection != connection"));
        assertTrue(navigation.contains("ROOTS.get(session.sourceRef)"));
        assertTrue(navigation.contains("root.ddsCanReopen(session.player)"));
        assertTrue(network.contains("player -> QuickContainerAccess.enabled()"));
        assertTrue(network.contains("QuickContainerAccessCompat.navigateBack(player, containerId)"));
    }

    @Test void traceEditsNeverIntroduceInvalidReplayModMarkers() throws IOException {
        for (String path : new String[]{
                "feature/quickcontaineraccess/compat/QuickContainerAccessNavigation.java",
                "feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java",
                "integration/playerinventory/DdsPlayerInventoryReopenCompat.java"}) {
            for (String line : read(path).split("\\R"))
                assertFalse(line.startsWith("//$") && !line.startsWith("//$$"), path + ": " + line);
        }
    }

    private static String read(String path) throws IOException {
        return Files.readString(ROOT.resolve("src/main/java/carpetddsaddition/" + path));
    }

    private static Path findRepositoryRoot() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main")))
                return p;
        throw new IllegalStateException("Repository root not found");
    }
}
