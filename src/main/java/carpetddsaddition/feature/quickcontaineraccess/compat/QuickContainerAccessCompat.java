/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
//#if MC >= 11404 && MC <= 260200
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
//#endif

public final class QuickContainerAccessCompat {
    private QuickContainerAccessCompat() {}
    public static boolean shouldInspectContainerClickPackets() { return QuickContainerAccess.enabled(); }

    public static boolean tryOpenInHand(Player player, ItemStack stack, InteractionHand hand) {
        //#if MC >= 11404 && MC <= 260200
        if (!QuickContainerAccess.enabled() || !(player instanceof ServerPlayer) || player instanceof EntityPlayerMPFake) return false;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(stack);
        if (kind == null || !QuickContainerAccessItems.canOpenInHand(kind, stack)) return false;
        int sourceSlot = hand == InteractionHand.MAIN_HAND ? QuickContainerAccessSession.selectedSlot(inventory)
                : QuickContainerAccessSession.offhandSlot();
        QuickContainerAccessSession.SourceRef sourceRef = QuickContainerAccessSession.rootRef(serverPlayer, inventory, sourceSlot, stack);
        return QuickContainerAccessMenus.open(serverPlayer, sourceRef, kind);
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean handleInventoryClick(AbstractContainerMenu menu, int slotId, int button, Object clickType, Player player) {
        //#if MC >= 11404 && MC <= 260200
        if (!(player instanceof ServerPlayer) || player instanceof EntityPlayerMPFake) return false;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        // Resolve surviving sessions before the enabled fast-path so vanilla never mutates an invalid QCA menu.
        QuickContainerAccessSession.Session active = QuickContainerAccessSession.get(menu);
        if (active != null && !active.valid()) {
            serverPlayer.closeContainer();
            return true;
        }
        if (!QuickContainerAccess.enabled()) return false;
        Inventory playerInventory = QuickContainerAccessSession.inventory(serverPlayer);

        if (active != null) {
            if (active.sourceRef.isProtectedMenuSlot(menu, slotId)
                    || active.sourceRef.isProtectedPlayerSwap(button, clickType)
                    || QuickContainerAccessSession.isPickupAllSourceCollect(menu, clickType, active)) return true;

            if (isRightPickup(clickType, button) && QuickContainerAccessSession.carried(menu, serverPlayer).isEmpty()
                    && validSlot(menu, slotId)) {
                Slot clicked = menu.getSlot(slotId);
                ItemStack target = clicked.getItem();
                if (!target.isEmpty() && target.getCount() == 1) {
                    QuickContainerAccessItems.MenuKind nextKind = QuickContainerAccessItems.kindOf(target);
                    QuickContainerAccessSession.SourceRef nextRef =
                            QuickContainerAccessSession.sourceRefForActiveClick(active, clicked, target, nextKind);
                    if (nextKind != null && nextRef != null) {
                        QuickContainerAccessSession.syncMenu(serverPlayer, menu);
                        return QuickContainerAccessMenus.switchOpen(serverPlayer, menu, nextRef, target, nextKind);
                    }
                }
            }
            if (QuickContainerAccessStorage.handleStorageInteraction(serverPlayer, menu, slotId, button, clickType, playerInventory)) return true;
            return false;
        }

        if (QuickContainerAccessStorage.handleStorageInteraction(serverPlayer, menu, slotId, button, clickType, playerInventory)) return true;
        if (!isRightPickup(clickType, button) || !validSlot(menu, slotId)) return false;

        Slot slot = menu.getSlot(slotId);
        ItemStack target = slot.getItem();
        ItemStack carried = QuickContainerAccessSession.carried(menu, serverPlayer);
        if (slot.container == playerInventory) {
            if (carried.isEmpty() && !target.isEmpty() && target.getCount() == 1) {
                QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(target);
                if (kind != null) {
                    QuickContainerAccessSession.SourceRef ref = QuickContainerAccessSession.rootRef(serverPlayer, playerInventory,
                            QuickContainerAccessSession.containerSlot(slot), target);
                    QuickContainerAccessSession.syncMenu(serverPlayer, menu);
                    return QuickContainerAccessMenus.switchOpen(serverPlayer, menu, ref, target, kind);
                }
            }
            return false;
        }

        // Ender Chest nested exception: own Ender Chest -> single Shulker Box.
        if (slot.container == serverPlayer.getEnderChestInventory() && carried.isEmpty() && !target.isEmpty() && target.getCount() == 1
                && QuickContainerAccessItems.kindOf(target) == QuickContainerAccessItems.MenuKind.SHULKER) {
            QuickContainerAccessSession.SourceRef ref = QuickContainerAccessSession.rootRef(serverPlayer,
                    serverPlayer.getEnderChestInventory(), QuickContainerAccessSession.containerSlot(slot), target);
            QuickContainerAccessSession.syncMenu(serverPlayer, menu);
            return QuickContainerAccessMenus.switchOpen(serverPlayer, menu, ref, target, QuickContainerAccessItems.MenuKind.SHULKER);
        }
        return false;
        //#else
        //$$ return false;
        //#endif
    }

    public static void validateMenu(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260200
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (session != null && !session.valid()) session.player.closeContainer();
        //#endif
    }
    public static void onMenuRemoved(AbstractContainerMenu menu, Player player) {
        //#if MC >= 11404 && MC <= 260200
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.finish(menu, player);
        if (session != null) QuickContainerAccessMenus.sendCloseSound(session);
        //#endif
    }
    public static void closeActiveMenus() {
        //#if MC >= 11404 && MC <= 260200
        QuickContainerAccessSession.closeAllActiveMenus();
        //#endif
    }
    public static void resetRuntimeState() {
        //#if MC >= 11404 && MC <= 260200
        QuickContainerAccessSession.clearAll();
        QuickContainerAccessAmsCompat.reset();
        //#endif
    }
    public static boolean handlePortableAnvilUse(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260200
        return QuickContainerAccessMenus.handlePortableAnvilUse(menu);
        //#else
        //$$ return false;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260200
    private static boolean isRightPickup(Object clickType, int button) {
        return button == 1 && "PICKUP".equals(QuickContainerAccessSession.enumName(clickType));
    }
    private static boolean validSlot(AbstractContainerMenu menu, int slotId) { return slotId >= 0 && slotId < menu.slots.size(); }
    //#endif
}
