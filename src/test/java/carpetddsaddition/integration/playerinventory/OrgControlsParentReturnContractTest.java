package carpetddsaddition.integration.playerinventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class OrgControlsParentReturnContractTest {
    public static final class OrgControlsMenu {
        public OrgControlsMenu(int id, ServerPlayer target, ServerPlayer viewer) {}
    }
    public static final class VanillaStyleMenu {
        public VanillaStyleMenu(int id, Inventory viewerInventory, ServerPlayer target) {}
    }
    public static final class UnexpectedMenu {
        public UnexpectedMenu(int id, ServerPlayer target) {}
    }

    @Test void orgButtonsRequireExactTargetAndViewerConstructor() {
        Constructor<?> ctor = DdsPlayerInventoryReopenCompat.orgControlsConstructor(OrgControlsMenu.class);
        assertNotNull(ctor);
        assertArrayEquals(new Class<?>[]{int.class, ServerPlayer.class, ServerPlayer.class},
                ctor.getParameterTypes());
        assertNull(DdsPlayerInventoryReopenCompat.orgControlsConstructor(VanillaStyleMenu.class));
        assertNull(DdsPlayerInventoryReopenCompat.orgControlsConstructor(UnexpectedMenu.class));
    }

    @Test void orgReopenPreservesSessionAndParentAuthorization() throws IOException {
        String code = Files.readString(findRoot().resolve(
                "src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventoryReopenCompat.java"));
        assertTrue(code.contains("DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID())"));
        assertTrue(code.contains("viewer.containerMenu != menu"));
        assertTrue(code.contains("session.viewerConnection() != viewer.connection"));
        assertTrue(code.contains("DdsRemotePlayerOwnerResolver.owner(menu) == owner"));
        assertTrue(code.contains("org && orgControls"));
        assertTrue(code.contains("constructor.newInstance(id, owner, candidate)"));
        assertTrue(code.contains("menu.stillValid(candidate)"));
        assertTrue(code.contains("candidate.connection != connection"));
    }

    private static Path findRoot() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main")))
                return p;
        throw new IllegalStateException("Repository root not found");
    }
}
