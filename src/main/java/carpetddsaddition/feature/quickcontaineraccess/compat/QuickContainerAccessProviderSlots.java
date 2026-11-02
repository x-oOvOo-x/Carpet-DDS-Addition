/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;

/**
 * Narrow server-side slot guard for known native GCA/Org menus.
 * This is independent of PlayerInventoryExtension: menu identity and the
 * server's actual slots, not any client-reported role, determine eligibility.
 *
 * Unknown menus keep QCA's existing general Slot validation. Known Provider
 * layouts with unexpected dimensions fail closed.
 */
final class QuickContainerAccessProviderSlots {
    private static final String GCA_INVENTORY = "dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu";
    private static final String GCA_ENDER_CONTAINER = "dev.dubhe.gugle.carpet.tools.player.PlayerEnderChestContainer";
    private static final String ORG_PACKAGE = ".carpetorgaddition.";

    private QuickContainerAccessProviderSlots() {}

    /** Client-visible slot shape only; never a grant to mutate server inventory. */
    static Boolean storageRole(AbstractContainerMenu menu, Slot slot) {
        if (menu == null || slot == null) return false;
        int id = menu.slots.indexOf(slot);
        if (id < 0) return false;

        String name = menu.getClass().getName();
        int total = menu.slots.size();
        if (GCA_INVENTORY.equals(name)) return storageOrOffhand(total, id, 54, 18, 54, 7);
        if (name.contains(ORG_PACKAGE)) {
            if (name.endsWith(".WithButtonPlayerInventoryScreenHandler"))
                return storageOrOffhand(total, id, 54, 18, 54, 7);
            if (name.endsWith(".PlayerInventoryScreenHandler"))
                return storageOrOffhand(total, id, 54, 0, 36, 40);
            if (name.endsWith(".PlayerEnderChestScreenHandler"))
                return storageSlot(total, id, 27, 0, 27);
        }

        // GCA can expose its controls in a vanilla ChestMenu.
        if (hasGcaEnderBacking(menu)) {
            if (menu.getClass() != ChestMenu.class) return false;
            return total == 63 ? storageSlot(total, id, 27, 0, 0)
                    : storageSlot(total, id, 54, 27, 54);
        }
        return null; // Ordinary/unknown menus retain QCA's existing slot handling.
    }

    /** Server-only requirement in addition to the untrusted GUI slot shape. */
    static boolean allows(AbstractContainerMenu menu, Slot slot, ServerPlayer viewer) {
        Boolean role = storageRole(menu, slot);
        if (Boolean.FALSE.equals(role)) return false;
        if (role != null && isOrgEnderMenu(menu)) {
            // Org's owner field and concrete slot backing attest this menu.
            // GCA may supply the Ender backing container, but its separate
            // owner-view interface is not a requirement for Org's own menu.
            ServerPlayer target = DdsRemotePlayerOwnerResolver.owner(menu);
            return target != null && viewer != null
                    && orgEnderSlotMatches(menu.slots.indexOf(slot), slot.container,
                            target.getEnderChestInventory(), QuickContainerAccessSession.inventory(viewer));
        }
        // Only native GCA-backed menus need this extra GCA owner-view check.
        return role == null || !hasGcaEnderBacking(menu) || hasVerifiedGcaEnderOwner(menu);
    }

    private static boolean isOrgEnderMenu(AbstractContainerMenu menu) {
        String name = menu.getClass().getName();
        return name.contains(ORG_PACKAGE) && name.endsWith(".PlayerEnderChestScreenHandler");
    }

    /** Structural owner check: 27 genuine Ender slots followed by 36 viewer slots. */
    static boolean orgEnderSlotMatches(int id, Container backing, Container targetEnder, Container viewerInventory) {
        return id >= 0 && id < 63 && backing != null
                && (id < 27 ? backing == targetEnder : backing == viewerInventory);
    }

    static boolean storageSlot(int total, int id, int top, int start, int end) {
        return total == top + 36 && id >= 0 && id < total
                && (id >= start && id < end || id >= top);
    }

    /** Offhand is ordinary item storage; armor and Provider controls stay excluded. */
    static boolean storageOrOffhand(int total, int id, int top, int start, int end, int offhand) {
        return storageSlot(total, id, top, start, end)
                || total == top + 36 && id == offhand;
    }

    /** Exact Provider class identity: vanilla uses the same simple class name. */
    static boolean isGcaEnderContainerName(String name) {
        return GCA_ENDER_CONTAINER.equals(name);
    }

    private static boolean hasGcaEnderBacking(AbstractContainerMenu menu) {
        for (Slot slot : menu.slots) {
            if (slot.container != null
                    && isGcaEnderContainerName(slot.container.getClass().getName())) return true;
        }
        return false;
    }

    private static boolean hasVerifiedGcaEnderOwner(AbstractContainerMenu menu) {
        for (Slot slot : menu.slots) {
            if (slot.container == null
                    || !isGcaEnderContainerName(slot.container.getClass().getName())) continue;
            if (!(slot.container instanceof DdsInventoryOwnerView)) return false;
            DdsInventoryOwnerView view = (DdsInventoryOwnerView) slot.container;
            return view.ddsInventoryOwner() != null
                    && view.ddsCanonicalContainer() == view.ddsInventoryOwner().getEnderChestInventory();
        }
        return false;
    }
}
