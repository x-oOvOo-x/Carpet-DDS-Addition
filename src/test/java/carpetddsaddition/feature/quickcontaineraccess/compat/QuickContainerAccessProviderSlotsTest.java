package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessProviderSlotsTest {
    @Test
    void gcaInventoryOnlyAllowsStorageAndViewerSlots() {
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(90, 0, 54, 18, 54));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(90, 17, 54, 18, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 18, 54, 18, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 53, 54, 18, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 54, 54, 18, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 89, 54, 18, 54));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(91, 18, 54, 18, 54));
    }

    @Test
    void orgInventoryExcludesEquipmentAndDisabledSlots() {
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 0, 54, 0, 36));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 35, 54, 0, 36));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(90, 36, 54, 0, 36));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(90, 53, 54, 0, 36));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 54, 54, 0, 36));
    }

    @Test
    void providerOffhandIsEligibleButArmorAndControlsAreNot() {
        assertTrue(QuickContainerAccessProviderSlots.storageOrOffhand(90, 7, 54, 18, 54, 7));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(90, 4, 54, 18, 54, 7));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(90, 0, 54, 18, 54, 7));
        assertTrue(QuickContainerAccessProviderSlots.storageOrOffhand(90, 40, 54, 0, 36, 40));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(90, 39, 54, 0, 36, 40));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(91, 7, 54, 18, 54, 7));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(91, 40, 54, 0, 36, 40));
        // After P7 client retirement, server-side slot eligibility stays authoritative.
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(90, 6, 54, 18, 54, 7));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(90, 8, 54, 18, 54, 7));
        assertFalse(QuickContainerAccessProviderSlots.storageOrOffhand(89, 7, 54, 18, 54, 7));
    }

    @Test
    void gcaEnderAndControllerNeverExposeControlSlots() {
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(90, 26, 54, 27, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 27, 54, 27, 54));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(90, 89, 54, 27, 54));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(63, 26, 27, 0, 0));
        assertTrue(QuickContainerAccessProviderSlots.storageSlot(63, 27, 27, 0, 0));
        assertFalse(QuickContainerAccessProviderSlots.storageSlot(63, 63, 27, 0, 0));
    }

    @Test
    void orgEnderRequiresActualTargetBackingAndViewerInventory() {
        Container targetEnder = new SimpleContainer(54);
        Container viewerInventory = new SimpleContainer(36);
        Container other = new SimpleContainer(54);
        assertTrue(QuickContainerAccessProviderSlots.orgEnderSlotMatches(0, targetEnder, targetEnder, viewerInventory));
        assertTrue(QuickContainerAccessProviderSlots.orgEnderSlotMatches(26, targetEnder, targetEnder, viewerInventory));
        assertTrue(QuickContainerAccessProviderSlots.orgEnderSlotMatches(27, viewerInventory, targetEnder, viewerInventory));
        assertTrue(QuickContainerAccessProviderSlots.orgEnderSlotMatches(62, viewerInventory, targetEnder, viewerInventory));
        assertFalse(QuickContainerAccessProviderSlots.orgEnderSlotMatches(0, other, targetEnder, viewerInventory));
        assertFalse(QuickContainerAccessProviderSlots.orgEnderSlotMatches(27, targetEnder, targetEnder, viewerInventory));
        assertFalse(QuickContainerAccessProviderSlots.orgEnderSlotMatches(63, viewerInventory, targetEnder, viewerInventory));
        assertFalse(QuickContainerAccessProviderSlots.orgEnderSlotMatches(-1, targetEnder, targetEnder, viewerInventory));
    }

    @Test
    void qcaServerChecksProvidersWithoutRequiringExtension() throws IOException {
        String session = Files.readString(root().resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java"));
        String policy = Files.readString(root().resolve(
                "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessProviderSlots.java"));
        assertTrue(session.contains("//$$ if (!QuickContainerAccessProviderSlots.allows(player.containerMenu, slot, player))"));
        assertTrue(policy.contains("static Boolean storageRole(AbstractContainerMenu menu, Slot slot)"));
        assertTrue(policy.contains("static boolean allows(AbstractContainerMenu menu, Slot slot, ServerPlayer viewer)"));
        assertFalse(policy.contains("PlayerInventoryExtension.enabled()"));
        assertTrue(policy.contains("hasVerifiedGcaEnderOwner"));
        assertTrue(policy.contains("DdsRemotePlayerOwnerResolver.owner(menu)"));
        assertTrue(policy.contains("orgEnderSlotMatches(menu.slots.indexOf(slot)"));
        assertTrue(policy.contains("menu.getClass() != ChestMenu.class"));
    }

    @Test
    void vanillaEnderContainerNeverInheritsGcaSlotRestrictions() {
        assertTrue(QuickContainerAccessProviderSlots.isGcaEnderContainerName(
                "dev.dubhe.gugle.carpet.tools.player.PlayerEnderChestContainer"));
        assertFalse(QuickContainerAccessProviderSlots.isGcaEnderContainerName(
                "net.minecraft.world.inventory.PlayerEnderChestContainer"));
        assertFalse(QuickContainerAccessProviderSlots.isGcaEnderContainerName(
                "other.provider.PlayerEnderChestContainer"));
        assertFalse(QuickContainerAccessProviderSlots.isGcaEnderContainerName(null));
    }

    private static Path root() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent()) {
            if (Files.exists(p.resolve("settings.json"))) return p;
        }
        throw new IllegalStateException("repository root not found");
    }
}
