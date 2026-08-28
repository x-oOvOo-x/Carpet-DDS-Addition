/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessClientNetwork;
//#if MC < 11903
import carpetddsaddition.mixin.feature.quickcontaineraccess.CreativeModeInventoryScreenAccessor;
//#endif

//#if MC >= 11404 && MC <= 260200
import carpetddsaddition.core.inventory.DdsDirectSlotAccess;
import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
import carpetddsaddition.mixin.feature.quickcontaineraccess.CreativeSlotWrapperAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
//#if MC >= 11900
//$$ import net.minecraft.network.chat.contents.TranslatableContents;
//#else
import net.minecraft.network.chat.TranslatableComponent;
//#endif
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//#if MC < 11903
import net.minecraft.world.item.CreativeModeTab;
//#endif
//#endif

/** Client-side QCA intent classification; server remains authoritative. */
public final class QuickContainerAccessClient {
    private QuickContainerAccessClient() {}
    public enum StorageDragMode { ABSORB, EXTRACT }

    //#if MC >= 11404 && MC <= 260200
    public static boolean isCreativePickerScreen() {
        Screen screen = currentScreen();
        if (!(screen instanceof CreativeModeInventoryScreen)) return false;
        //#if MC >= 11903
        //$$ return !((CreativeModeInventoryScreen) screen).isInventoryOpen();
        //#else
        return CreativeModeInventoryScreenAccessor.carpetDdsAddition$getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId();
        //#endif
    }
    public static boolean isCreativeSurvivalScreen() { return isCreativeScreen() && !isCreativePickerScreen(); }

    public static boolean canUseDedicatedOpenSlot(Slot slot, Player player) {
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseOpenSlot() || isCreativeScreen()
                || !safeSourceSlot(slot, player)) return false;
        ItemStack target = slot.getItem();
        return !target.isEmpty() && target.getCount() == 1 && QuickContainerAccessItems.kindOf(target) != null;
    }

    public static int creativeInventoryOpenSlot(Slot slot, Player player) {
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseOpenInventorySlot() || !isCreativeSurvivalScreen()) return -1;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        int index = creativeInventorySlot(slot, player);
        if (index < 0 || index >= inventory.getContainerSize()) return -1;
        ItemStack target = inventory.getItem(index);
        return !target.isEmpty() && target.getCount() == 1 && QuickContainerAccessItems.kindOf(target) != null ? index : -1;
    }

    public static int creativeInventoryStorageSlot(Slot slot, Player player, ItemStack carried) {
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseCreativeStorage()
                || !isCreativeSurvivalScreen() || carried == null || carried.isEmpty()) return -1;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        int index = creativeInventorySlot(slot, player); if (index < 0 || index >= inventory.getContainerSize()) return -1;
        ItemStack target = inventory.getItem(index);
        if (QuickContainerAccessItems.isStorageHostCandidate(carried))
            return !QuickContainerAccessItems.isShulker(carried) || !QuickContainerAccessItems.isShulker(target) || target.getCount() == 1 ? index : -1;
        return QuickContainerAccessItems.isStorageHostCandidate(target) ? index : -1;
    }

    public static void applyCreativeCarried(AbstractContainerMenu menu, Player player, ItemStack stack) {
        if (menu != null && player != null && isCreativeSurvivalScreen())
            QuickContainerAccessSession.setCarried(menu, player, stack == null ? ItemStack.EMPTY : stack);
    }

    public static boolean shouldSuppressPrediction(AbstractContainerMenu menu, int slotNum, int buttonNum,
                                                   Object clickType, Player player) {
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseStorageClick() || isCreativeScreen()
                || !"PICKUP".equals(QuickContainerAccessSession.enumName(clickType)) || buttonNum != 1
                || slotNum < 0 || slotNum >= menu.slots.size()
                || QuickContainerAccessWorkstations.isOwnSlot(menu, slotNum, player)) return false;
        Slot slot = menu.getSlot(slotNum);
        ItemStack target = slot.getItem(), carried = QuickContainerAccessSession.carried(menu, player);
        if (carried.isEmpty()) {
            if (target.isEmpty() || target.getCount() != 1) return false;
            return safeSourceSlot(slot, player) && QuickContainerAccessItems.kindOf(target) != null;
        }
        if (!canUseDedicatedStorageSlot(slot, player)) return false;
        return QuickContainerAccessItems.isStorageHostCandidate(carried)
                ? canUseDedicatedStorageInteraction(slot, player, carried)
                : QuickContainerAccessItems.isStorageHostCandidate(target);
    }

    public static boolean canUseDedicatedStorageInteraction(Slot slot, Player player, ItemStack carried) {
        if (!canUseDedicatedStorageSlot(slot, player) || !QuickContainerAccessItems.isStorageHostCandidate(carried)) return false;
        ItemStack target = slot.getItem();
        if (!QuickContainerAccessItems.isStorageHost(target)) return true;
        return !QuickContainerAccessItems.isShulker(carried) || !QuickContainerAccessItems.isShulker(target) || target.getCount() == 1;
    }

    public static boolean canUseDedicatedStorageSlot(Slot slot, Player player) {
        if (slot == null || player == null || QuickContainerAccessWorkstations.isOwnSlot(player.containerMenu, slot, player)) return false;
        if (slot.container == QuickContainerAccessSession.inventory(player)) return true;
        if (slot.container instanceof DdsDirectSlotAccess)
            return ((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(QuickContainerAccessSession.containerSlot(slot));
        if (slot.container instanceof DdsInventoryOwnerView) {
            DdsInventoryOwnerView view = (DdsInventoryOwnerView) slot.container;
            if (view.ddsInventoryOwner() != null && view.ddsCanonicalContainer() != null) return true;
        }
        return isEnderChestScreen();
    }

    private static int creativeInventorySlot(Slot slot, Player player) {
        if (slot == null || player == null) return -1;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        Slot backing = creativeBackingSlot(slot); if (backing == null) backing = slot;
        if (backing.container != inventory || !backing.mayPickup(player)) return -1;
        return QuickContainerAccessSession.containerSlot(backing);
    }
    private static Slot creativeBackingSlot(Slot slot) {
        return (Object) slot instanceof CreativeSlotWrapperAccessor
                ? ((CreativeSlotWrapperAccessor) (Object) slot).carpetDdsAddition$getTarget() : null;
    }
    private static boolean safeSourceSlot(Slot slot, Player player) {
        if (slot == null || player == null || !slot.mayPickup(player)
                || QuickContainerAccessWorkstations.isOwnSlot(player.containerMenu, slot, player)) return false;
        if (slot.container instanceof DdsDirectSlotAccess
                && !((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(QuickContainerAccessSession.containerSlot(slot))) return false;
        ItemStack target = slot.getItem(); return target.isEmpty() || slot.mayPlace(target);
    }
    private static boolean isEnderChestScreen() {
        Screen screen = currentScreen(); if (screen == null) return false;
        //#if MC >= 11900
        //$$ if (!(screen.getTitle().getContents() instanceof TranslatableContents)) return false;
        //$$ return "container.enderchest".equals(((TranslatableContents) screen.getTitle().getContents()).getKey());
        //#else
        if (!(screen.getTitle() instanceof TranslatableComponent)) return false;
        return "container.enderchest".equals(((TranslatableComponent) screen.getTitle()).getKey());
        //#endif
    }
    private static boolean isCreativeScreen() { return currentScreen() instanceof CreativeModeInventoryScreen; }
    private static Screen currentScreen() {
        //#if MC >= 260200
        //$$ return Minecraft.getInstance().gui.screen();
        //#else
        return Minecraft.getInstance().screen;
        //#endif
    }
    //#endif
}
