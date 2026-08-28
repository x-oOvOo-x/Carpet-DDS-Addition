/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
//#if MC >= 11502
import net.minecraft.world.inventory.CartographyTableMenu;
//#endif
//#if MC >= 11601
//$$ import net.minecraft.world.inventory.SmithingMenu;
//#endif

/** Keeps QCA out of workstation-owned input/output slots while leaving player inventory slots untouched. */
public final class QuickContainerAccessWorkstations {
    private QuickContainerAccessWorkstations() {}

    public static boolean isOwnSlot(AbstractContainerMenu menu, int slotId, Player player) {
        return menu != null && slotId >= 0 && slotId < menu.slots.size() && isOwnSlot(menu, menu.getSlot(slotId), player);
    }
    public static boolean isOwnSlot(AbstractContainerMenu menu, Slot slot, Player player) {
        return menu != null && slot != null && player != null && isMenu(menu)
                && slot.container != QuickContainerAccessSession.inventory(player);
    }
    private static boolean isMenu(AbstractContainerMenu menu) {
        if (menu instanceof CraftingMenu || menu instanceof StonecutterMenu || menu instanceof AnvilMenu
                || menu instanceof LoomMenu || menu instanceof GrindstoneMenu || menu instanceof EnchantmentMenu) return true;
        //#if MC >= 11502
        if (menu instanceof CartographyTableMenu) return true;
        //#endif
        //#if MC >= 11601
        //$$ if (menu instanceof SmithingMenu) return true;
        //#endif
        return false;
    }
}
