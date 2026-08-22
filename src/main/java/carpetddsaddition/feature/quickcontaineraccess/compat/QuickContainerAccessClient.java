/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessClientNetwork;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
//#if MC >= 11900
//$$ import net.minecraft.network.chat.contents.TranslatableContents;
//#else
import net.minecraft.network.chat.TranslatableComponent;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//#endif

/** Optional prediction suppression, enabled only after negotiated QCA storage-click support. */
public final class QuickContainerAccessClient {
    private QuickContainerAccessClient() {}

    //#if MC >= 11404 && MC <= 260200
    public static boolean shouldSuppressPrediction(AbstractContainerMenu menu, int slotNum, int buttonNum,
                                                   Object clickType, Player player) {
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseStorageClick()
                || currentScreen() instanceof CreativeModeInventoryScreen) return false;
        String type = QuickContainerAccessSession.enumName(clickType);
        ItemStack carried = QuickContainerAccessSession.carried(menu, player);
        if (!"PICKUP".equals(type) || buttonNum != 1 || slotNum < 0 || slotNum >= menu.slots.size()) return false;
        Slot slot = menu.getSlot(slotNum);
        ItemStack target = slot.getItem();
        if (slot.container == QuickContainerAccessSession.inventory(player)) {
            if (carried.isEmpty()) {
                return !target.isEmpty() && target.getCount() == 1 && QuickContainerAccessItems.kindOf(target) != null;
            }
            return QuickContainerAccessItems.isStorageHostCandidate(target)
                    || QuickContainerAccessItems.isStorageHostCandidate(carried);
        }
        // World containers are not general nested sources; only own Ender Chest -> single Shulker Box is supported.
        if (!carried.isEmpty() || target.isEmpty() || target.getCount() != 1) return false;
        QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(target);
        return kind == QuickContainerAccessItems.MenuKind.SHULKER && isEnderChestScreen();
    }

    private static boolean isEnderChestScreen() {
        Screen screen = currentScreen();
        if (screen == null) return false;
        //#if MC >= 11900
        //$$ if (!(screen.getTitle().getContents() instanceof TranslatableContents)) return false;
        //$$ TranslatableContents contents = (TranslatableContents) screen.getTitle().getContents();
        //$$ return "container.enderchest".equals(contents.getKey());
        //#else
        if (!(screen.getTitle() instanceof TranslatableComponent)) return false;
        return "container.enderchest".equals(((TranslatableComponent) screen.getTitle()).getKey());
        //#endif
    }
    private static Screen currentScreen() {
        //#if MC >= 260200
        //$$ return Minecraft.getInstance().gui.screen();
        //#else
        return Minecraft.getInstance().screen;
        //#endif
    }
    //#endif
}
