package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessExtensionEnderAuthorityContractTest {
    private static final Path ROOT = findRoot();

    @Test void modernOrgCapacityRequiresStrictExtensionSession() throws IOException {
        String s = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");

        assertTrue(s.contains("if (providerSize == 0 && strictOrgWholeEnderAuthorized(viewer, menu, owner))"));
        assertTrue(s.contains("if (!PlayerInventoryExtension.enabled()"));
        assertTrue(s.contains("viewer.containerMenu != menu"));
        assertTrue(s.contains("DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C"));
        assertTrue(s.contains("DdsFakePlayerPackets.STRUCTURE_RESULT_S2C"));
        assertTrue(s.contains("DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID())"));
        assertTrue(s.contains("\"org\".equals(session.providerId())"));
        assertTrue(s.contains("session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.INVENTORY"));
        assertTrue(s.contains("session.liveTarget() == owner"));
        assertTrue(s.contains("return actualRemoteEnderSize(owner)"));
        assertTrue(s.contains("name.equals(\"boat.carpetorgaddition.wheel.screen.PlayerInventoryScreenHandler\")"));
        assertTrue(s.contains("name.equals(\"boat.carpetorgaddition.wheel.screen.WithButtonPlayerInventoryScreenHandler\")"));
    }

    @Test void strictPermissionReplacesLegacyRemoteRuleOnAllVersions() throws IOException {
        String session=source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String compat=source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessCompat.java");
        String access=source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/QuickContainerAccess.java");
        assertTrue(session.contains("if (providerSize <= 0) return providerSize"));
        assertTrue(session.contains("providerSize == 27 && strictGcaWholeEnderAuthorized(viewer, menu, owner)"));
        assertTrue(session.contains("strictOrgWholeEnderAuthorized(viewer, menu, owner)"));
        assertTrue(session.contains("PlayerInventoryExtension.enabled()"));
        assertFalse(session.contains("remoteFakePlayerEnderChestEnabled()"));
        assertFalse(compat.contains("legacyRemoteEnabled()"));
        assertFalse(access.contains("remoteFakePlayerEnderChestEnabled()"));
        assertFalse(source("src/main/rules/rules.json").contains("remoteFakePlayerEnderChest"));
    }

    @Test void extensionOwnsCanonicalOpenAndQcaKeepsNestedNavigation() throws IOException {
        String canonical = source("src/main/java/carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        String qca = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");

        assertTrue(canonical.contains("player -> PlayerInventoryExtension.enabled()"));
        assertTrue(canonical.contains("canonicalEnderCapacityMatches"));
        assertTrue(canonical.contains("captureProviderSlotPolicy"));
        assertTrue(canonical.contains("session.sessionGeneration()")
                || canonical.contains("request.sessionGeneration"));
        assertFalse(canonical.contains("QuickContainerAccess.remoteFakePlayerEnderChestEnabled()"));
        assertTrue(qca.contains("QuickContainerAccessNavigation") || qca.contains("sendNavigationState"));
        assertTrue(qca.contains("authorizedEnderSize(owner, authorizedEnderSize)"));
    }


    @Test void remoteEnderItemOpenMustFailClosedWithoutAuthorizedSize() throws IOException {
        String menus = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        String session = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        assertTrue(menus.contains("owner != player")
                && menus.contains("authorizedEnderSize <= 0 || authorizedEnderSize > sourceRef.authorizedEnderSize"));
        assertTrue(menus.contains("sourceRef.inventoryOwner != player && sourceRef.authorizedEnderSize <= 0"));
        assertTrue(session.contains("sourceRef.authorizedEnderSize >= openedSize"));
        assertTrue(menus.contains("resolveOpenedSize(owner, kind)"), "Own Ender remains usable");
    }

    @Test void shortcutEnderStorageBoundsEveryTraversalAndCarriedOwner() throws IOException {
        String storage = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        String session = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        assertTrue(storage.contains("authorizedStorageEnderSize(viewer, menu, owner)"));
        assertTrue(storage.contains("QuickContainerAccessCarriedEnderOrigins.resolve(player, menu, carried)"));
        assertTrue(storage.contains("if (carriedOwner == null) return true;"));
        assertTrue(storage.contains("storageFor(player, carriedOwner, menu, carried)"));
        assertFalse(storage.contains("StorageHandle carriedStorage = storageFor(player, player, menu, carried)"));
        assertTrue(storage.contains("storageFor(player, owner, menu, target)"));
        assertEquals(2, storage.split(java.util.regex.Pattern.quote("i < storage.size && !source.isEmpty()"), -1).length - 1);
        assertTrue(storage.contains("i < source.size; i++"));
        assertTrue(storage.contains("i = storage.size - 1; i >= 0; i--"));
        assertTrue(session.contains("active.sourceRef.authorizedEnderSize"));
    }

    @Test void extensionElevationMustBeRevokedThroughExistingSessionValidation() throws IOException {
        String session = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String extension = source("src/main/java/carpetddsaddition/feature/playerinventoryextension/PlayerInventoryExtension.java");
        assertTrue(session.contains("if (extensionBackedEnder && !PlayerInventoryExtension.enabled()) enderGrantRevoked = true;"));
        assertTrue(session.contains("if (remoteEnderSource() && !enderAuthorityValid()) return false;"));
        assertTrue(session.contains("authorizedEnderSize, true);"));
        assertTrue(session.contains("return !enderGrantRevoked && (parent == null || parent.enderAuthorityValid())"));
        assertTrue(session.contains("session.sourceRef.enderAuthorityValid();"));
        assertTrue(session.contains("active.sourceRef.extensionBackedEnder"));
        assertTrue(session.contains("getPlayer(inventoryOwner.getUUID()) != inventoryOwner"));
        assertTrue(extension.contains("QuickContainerAccess.onRuleChanged()"));
        assertTrue(session.contains("closeInvalidMenus()"));
    }
    @Test void turningOffExtensionKeepsOrdinaryQcaShulkerButRevokesEnderAuthority() throws IOException {
        String session = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java");
        String menus = source("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenus.java");
        int sourceValid = session.indexOf("boolean valid() {", session.indexOf("static final class SourceRef"));
        int sourceValidEnd = session.indexOf("void set(ItemStack replacement)", sourceValid);
        assertTrue(sourceValid > 0 && sourceValidEnd > sourceValid);
        String sourceGuard=session.substring(sourceValid, sourceValidEnd);
        assertTrue(sourceGuard.contains("if (remoteEnderSource() && !enderAuthorityValid()) return false;"));
        assertFalse(sourceGuard.contains("if (!PlayerInventoryExtension.enabled()) return false;"),
                "An ordinary Shulker source must not depend on Extension's Ender capacity");
        assertTrue(session.contains("if (!sourceRef.enderAuthorityValid()) return false;"),
                "An actual opened remote Ender path must be invalidated");
        assertTrue(session.contains("if (active != null && !active.sourceRef.enderAuthorityValid()) return 0;"),
                "Stored Ender capacity must not outlive the strict Extension grant");
        assertTrue(menus.contains("!sourceRef.enderAuthorityValid()"));
        assertTrue(menus.contains("!destination.sourceRef.enderAuthorityValid()"));
    }

    private static String source(String relative) throws IOException {
        return Files.readString(ROOT.resolve(relative));
    }

    private static Path findRoot() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent()) {
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main")))
                return p;
        }
        throw new IllegalStateException("Repository root not found");
    }
}
