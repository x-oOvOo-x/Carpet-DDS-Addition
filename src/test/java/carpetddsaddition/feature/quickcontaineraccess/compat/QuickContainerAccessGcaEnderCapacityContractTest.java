package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessGcaEnderCapacityContractTest {
    private static final Path ROOT=findRepositoryRoot(), JAVA=ROOT.resolve("src/main/java");

    @Test void strictGcaWholeEnderUsesActualAmsCapacityButLegacyFallbackStaysThreeRows() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");

        assertTrue(session.contains("providerAuthorizedEnderSize"));
        assertTrue(session.contains("PlayerInventoryContainer"));
        assertTrue(session.contains("shouldOpenGcaEnderChest(viewer)"));
        assertTrue(session.contains("strictGcaWholeEnderAuthorized(viewer, menu, owner)"));
        assertTrue(session.contains("return actualRemoteEnderSize(owner)"));
        assertTrue(session.contains("return 27"));
        assertFalse(session.contains("DdsGcaFakePlayerBridge.shouldOpenGcaEnderChest(viewer) ? 27 : -1"));
        assertTrue(session.contains("DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C"));
        assertTrue(session.contains("DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID())"));
        assertTrue(session.contains("DdsPlayerInventorySession.InventoryKind.INVENTORY"));
        assertTrue(session.contains("DdsPlayerInventorySession.TargetType.LIVE_FAKE"));
        assertTrue(session.contains("DdsPlayerInventorySession.TargetType.LIVE_PLAYER"));
        assertTrue(session.contains("session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.ENDER_CHEST"));
        assertTrue(session.contains("DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()"));
        assertTrue(session.contains("providerSize == 27 && strictGcaWholeEnderAuthorized(viewer, menu, owner)"));
        assertTrue(menus.contains("sourceRef.authorizedEnderSize < 0"));
        assertTrue(menus.contains("? sourceRef.authorizedEnderSize : 0"));
        assertTrue(menus.contains("openInternal(player, sourceRef, kind, false, authorized)"));
        assertTrue(menus.contains("authorizedEnderSize(owner, authorizedEnderSize)"));
    }

    @Test void legacyOrgFakeEnderIsOnlyTwentySevenSlotsWithoutExtension() throws IOException {
        String session = read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        assertTrue(session.contains("!PlayerInventoryExtension.enabled()"));
        assertTrue(session.contains("orgLegacyQcaEnderAuthorized(viewer, menu, owner)) providerSize = 27"));
        assertTrue(session.contains("!(owner instanceof EntityPlayerMPFake)"));
        assertTrue(session.contains("DdsRemotePlayerOwnerResolver.owner(menu) == owner"));
        assertTrue(session.contains("viewer.containerMenu != menu"));
        assertTrue(session.contains("orgLegacyQcaEnderAuthorized("));
        assertTrue(read("carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java")
                .contains("NAVIGATE_BACK_C2S"));
    }

    @Test void providerEnderCapacityNeverUsesRetiredRemoteRulePromotion() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String menus=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        assertFalse(session.contains("extensionAuthorizedEnderSize"));
        assertFalse(session.contains("remoteFakePlayerEnderChestEnabled()"));
        assertTrue(session.contains("providerSize == 27 && strictGcaWholeEnderAuthorized(viewer, menu, owner)"));
        assertTrue(session.contains("if (providerSize <= 0) return providerSize"));
        assertTrue(menus.contains("providerAuthorizedEnderSize(viewer, currentMenu, owner)"));
        assertTrue(menus.contains("boolean remoteSource=sourceRef.remoteEnderSource()"));
        assertTrue(menus.contains("remoteEnder=remoteSource && PlayerInventoryExtension.enabled()"));
        assertTrue(menus.contains("remoteSource && !remoteEnder"));
        assertFalse(menus.contains("remoteFakePlayerEnderChestEnabled()"));
    }

    @Test void providerAuthorizedSizeIsCarriedThroughNestedSourceRefs() throws IOException {
        String session=read("carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");

        assertTrue(session.contains("final int authorizedEnderSize"));
        assertTrue(session.contains("active.sourceRef.authorizedEnderSize"));
        assertTrue(session.contains("SourceRef.guard(player, owner"));
        assertTrue(session.contains("factory, authorizedEnderSize, extensionBacked)"));
        assertTrue(session.contains("active.sourceRef.extensionBackedEnder"));
        assertTrue(session.contains("this.authorizedEnderSize = authorizedEnderSize"));
    }

    private static String read(String path) throws IOException {
        return Files.readString(JAVA.resolve(path));
    }

    private static Path findRepositoryRoot() {
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
