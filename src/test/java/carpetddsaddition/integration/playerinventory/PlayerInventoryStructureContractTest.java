package carpetddsaddition.integration.playerinventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class PlayerInventoryStructureContractTest {
    private static final Path ROOT=findRepositoryRoot();

    @Test void structureTransportIsIndependentFromQcaAndUsesStrictRequestIdentity() throws IOException {
        String packets=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerPackets.java");
        String network=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String sessions=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");
        String bootstrap=read("src/main/java/carpetddsaddition/core/bootstrap/DdsNetworkBootstrap.java");

        assertTrue(packets.contains("player_inventory_canonical_ender_c2s"));
        assertTrue(packets.contains("player_inventory_return_parent_c2s"));
        assertTrue(packets.contains("player_inventory_structure_result_s2c"));
        assertTrue(network.contains("session_generation"));
        assertTrue(network.contains("request_id"));
        assertTrue(network.contains("status"));
        assertTrue(network.contains("accepted"));
        assertTrue(network.contains("rejected"));
        assertTrue(network.contains("stale_session"));
        assertFalse(network.contains("QuickContainerAccess"));
        assertTrue(sessions.contains("current(ServerPlayer viewer, int containerId, UUID expectedTarget, long expectedGeneration)"));
        assertTrue(bootstrap.contains("DdsPlayerInventoryEnderNetwork.registerPackets()"));
    }

    @Test void canonicalUpgradeDoesNotInventParentOrBypassProviderSlotPolicy() throws IOException {
        String canonical=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventoryCanonicalEnderMenu.java");
        String sessions=read("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventorySessions.java");

        assertTrue(canonical.contains("ProviderSlotPolicy"));
        assertTrue(canonical.contains("mayPickup"));
        assertTrue(canonical.contains("mayPlace"));
        assertFalse(sessions.contains("inheritRoot("));
        assertFalse(sessions.contains("previous.rootReturn()"));
    }

    private static String read(String path) throws IOException { return Files.readString(ROOT.resolve(path)); }
    private static Path findRepositoryRoot() {
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
