package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessNativeProviderRoleContractTest {
    @Test void serverProviderValidationPersistsWithoutCdaLogicalClient() throws Exception {
        Path root = Path.of("").toAbsolutePath();
        while (root != null && !Files.isRegularFile(root.resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessProviderSlots.java")))
            root = root.getParent();
        assertNotNull(root, "CDA source root missing");
        String slots = Files.readString(root.resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessProviderSlots.java"));
        String session = Files.readString(root.resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java"));
        assertTrue(slots.contains("static boolean allows(AbstractContainerMenu menu, Slot slot, ServerPlayer viewer)"));
        assertTrue(slots.contains("if (Boolean.FALSE.equals(role)) return false;"));
        assertTrue(slots.contains("DdsRemotePlayerOwnerResolver.owner(menu)"));
        assertTrue(session.contains("QuickContainerAccessProviderSlots.allows("));
        assertFalse(Files.exists(root.resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessClient.java")));
    }
}
