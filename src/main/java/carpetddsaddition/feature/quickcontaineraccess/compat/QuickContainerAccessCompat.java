/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
//#if MC >= 11404 && MC <= 260300
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
//#endif

public final class QuickContainerAccessCompat {
    private QuickContainerAccessCompat() {}

    /** Identity of the fake player whose Ender Chest this active QCA menu actually owns. */
    public static ServerPlayer fakeEnderOwner(ServerPlayer viewer) {
        //#if MC >= 11404 && MC <= 260300
        if (viewer == null) return null;
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(viewer.containerMenu);
        return session != null && session.player == viewer && session.valid()
                && session.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && session.inventoryOwner instanceof EntityPlayerMPFake ? session.inventoryOwner : null;
        //#else
        //$$ return null;
        //#endif
    }

    /** Keep Org's offline inventories entirely under Org ownership. */
    public static boolean isOrgOfflineMenu(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260300
        if (menu == null) return false;
        for (Slot slot : menu.slots) {
            if (slot.container == null) continue;
            String type = slot.container.getClass().getName();
            if (type.endsWith(".OfflinePlayerInventory")
                    || type.endsWith(".OfflinePlayerEnderChestInventory")) return true;
        }
        return menu.getClass().getName().endsWith(".OfflinePlayerInventoryScreenHandler");
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean shouldInspectContainerClickPackets() { return QuickContainerAccess.enabled(); }

    public static boolean tryOpenInHand(Player player, ItemStack stack, InteractionHand hand) {
        //#if MC >= 11404 && MC <= 260300
        if (!QuickContainerAccess.enabled() || !(player instanceof ServerPlayer) || player instanceof EntityPlayerMPFake || player.isSpectator()) return false;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(stack);
        if (kind == null || !QuickContainerAccessItems.canOpenInHand(kind, stack)) return false;
        int sourceSlot = hand == InteractionHand.MAIN_HAND ? QuickContainerAccessSession.selectedSlot(inventory) : QuickContainerAccessSession.offhandSlot();
        return QuickContainerAccessMenus.open(serverPlayer, QuickContainerAccessSession.rootRef(serverPlayer, inventory, sourceSlot, stack), kind);
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean openPlayerInventorySlot(ServerPlayer player, int inventorySlot) {
        //#if MC >= 11404 && MC <= 260300
        if (!validCreativeInventorySlot(player, inventorySlot)) return false;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        ItemStack target = inventory.getItem(inventorySlot);
        QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(target);
        if (target.isEmpty() || target.getCount() != 1 || kind == null) return false;
        QuickContainerAccessSession.SourceRef ref = QuickContainerAccessSession.rootRef(player, inventory, inventorySlot, target);
        return QuickContainerAccessMenus.open(player, ref, kind);
        //#else
        //$$ return false;
        //#endif
    }

    /** Runs one Creative survival-inventory storage transaction without changing normal Survival cursor state. */
    public static ItemStack handleCreativeStorageClick(ServerPlayer player, int inventorySlot, ItemStack creativeCarried) {
        //#if MC >= 11404 && MC <= 260300
        if (!validCreativeInventorySlot(player, inventorySlot) || creativeCarried == null || creativeCarried.isEmpty()) return null;
        Inventory inventory = QuickContainerAccessSession.inventory(player);
        AbstractContainerMenu menu = player.inventoryMenu;
        int menuSlot = inventoryMenuSlot(menu, inventory, inventorySlot);
        if (menuSlot < 0) return null;
        ItemStack previous = QuickContainerAccessSession.carried(menu, player);
        QuickContainerAccessSession.setCarried(menu, player, creativeCarried);
        try {
            if (!QuickContainerAccessStorage.handleStorageInteraction(player, player, menu, menuSlot, 1, "PICKUP")) return null;
            ItemStack result = QuickContainerAccessSession.carried(menu, player);
            return result.isEmpty() ? ItemStack.EMPTY : result.copy();
        } finally {
            QuickContainerAccessSession.setCarried(menu, player, previous);
            menu.broadcastChanges();
        }
        //#else
        //$$ return null;
        //#endif
    }

    public static boolean handleInventoryClick(AbstractContainerMenu menu, int slotId, int button, Object clickType, Player player) {
        //#if MC >= 11404 && MC <= 260300
        if (!(player instanceof ServerPlayer) || player instanceof EntityPlayerMPFake || menu == null
                || isOrgOfflineMenu(menu) || !menu.stillValid(player)) return false;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        QuickContainerAccessSession.Session active = QuickContainerAccessSession.get(menu);
        if (active != null && !active.valid()) { serverPlayer.closeContainer(); return true; }
        if (!QuickContainerAccess.enabled()) return false;
        boolean rightPickup = isRightPickup(clickType, button), validSlot = validSlot(menu, slotId);
        if (validSlot && QuickContainerAccessWorkstations.isOwnSlot(menu, slotId, player)) return false;
        ItemStack carried = QuickContainerAccessSession.carried(menu, serverPlayer);

        if (active != null) {
            if (QuickContainerAccessStorage.handleOpenShulkerMerge(serverPlayer, menu, active, slotId, button, clickType)) return true;
            if (rightPickup && carried.isEmpty()) {
                QuickContainerAccessSession.PathNode sourceNode = active.path.findSourceMenuSlot(menu, slotId);
                if (sourceNode != null) {
                    if (sourceNode == active.path) {
                        if (QuickContainerAccessCloseDebounce.suppress(menu)) return true;
                        serverPlayer.closeContainer();
                        return true;
                    }
                    if (sourceNode.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
                        QuickContainerAccessSession.syncMenu(serverPlayer, menu);
                        return QuickContainerAccessMenus.navigateBack(serverPlayer, menu, sourceNode);
                    }
                }
            }
            if (active.sourceRef.isProtectedMenuSlot(menu, slotId) || active.sourceRef.isProtectedPlayerSwap(button, clickType)
                    || QuickContainerAccessSession.isPickupAllSourceCollect(menu, clickType, active)) return true;

            if (rightPickup && carried.isEmpty() && validSlot) {
                Slot clicked = menu.getSlot(slotId);
                ItemStack target = clicked.getItem();
                if (!target.isEmpty() && target.getCount() == 1 && QuickContainerAccessSession.safeDirectSlot(serverPlayer, clicked)) {
                    QuickContainerAccessItems.MenuKind nextKind = QuickContainerAccessItems.kindOf(target);
                    QuickContainerAccessSession.SourceRef nextRef = QuickContainerAccessSession.sourceRefForActiveClick(active, clicked, target, nextKind);
                    if (nextRef != null) {
                        if (!QuickContainerAccess.enabled() && !nextRef.remoteEnderSource()) return false;
                        if (nextKind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
                            QuickContainerAccessSession.PathNode destination = active.path.findOpenedContainer(nextRef.inventoryOwner.getEnderChestInventory());
                            if (destination != null) { QuickContainerAccessSession.syncMenu(serverPlayer, menu); return QuickContainerAccessMenus.navigateBack(serverPlayer, menu, destination); }
                        }
                        QuickContainerAccessSession.syncMenu(serverPlayer, menu);
                        return QuickContainerAccessMenus.switchOpen(serverPlayer, menu, nextRef, target, nextKind);
                    }
                }
            }
            if (validSlot) {
                Slot clicked = menu.getSlot(slotId);
                ServerPlayer owner = clicked.container == active.playerInventory ? serverPlayer : active.inventoryOwner;
                if (QuickContainerAccessStorage.handleStorageInteraction(serverPlayer, owner, menu, slotId, button, clickType)) return true;
            }
            return false;
        }

        if (validSlot) {
            Slot slot = menu.getSlot(slotId);
            ServerPlayer owner = QuickContainerAccessSession.ownerFor(serverPlayer, menu, slot.container);
            if (QuickContainerAccessStorage.handleStorageInteraction(serverPlayer, owner, menu, slotId, button, clickType)) return true;
        }
        if (!rightPickup || !validSlot || !carried.isEmpty()) return false;
        Slot slot = menu.getSlot(slotId);
        if (!QuickContainerAccessSession.safeDirectSlot(serverPlayer, slot)) return false;
        ItemStack target = slot.getItem();
        if (target.isEmpty() || target.getCount() != 1) return false;
        QuickContainerAccessItems.MenuKind kind = QuickContainerAccessItems.kindOf(target);
        if (kind == null) return false;
        QuickContainerAccessSession.SourceRef ref = QuickContainerAccessSession.rootRefForMenu(serverPlayer, menu, slot, target);
        QuickContainerAccessNavigation.attachSupportedRoot(ref, menu, slot, serverPlayer, kind);
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST && ref.parentPath != null
                && ref.parentPath.findOpenedContainer(ref.inventoryOwner.getEnderChestInventory()) != null) return true;
        QuickContainerAccessSession.syncMenu(serverPlayer, menu);
        return QuickContainerAccessMenus.switchOpen(serverPlayer, menu, ref, target, kind);
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean openRemoteOwnerEnder(ServerPlayer viewer, int containerId) {
        //#if MC >= 11404 && MC <= 260300
        if (!PlayerInventoryExtension.enabled()
                || viewer == null || viewer.isSpectator() || viewer instanceof EntityPlayerMPFake) return false;
        AbstractContainerMenu menu = viewer.containerMenu;
        if (menu == null || menu.containerId != containerId || !menu.stillValid(viewer)) return false;
        ServerPlayer owner = QuickContainerAccessSession.remoteOwnerForMenu(viewer, menu);
        if (!(owner instanceof EntityPlayerMPFake) || !QuickContainerAccessSession.isRemoteEnderMenu(viewer, menu, owner)) return false;
        return QuickContainerAccessMenus.openRemoteEnder(viewer, menu, owner);
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean canUseDedicatedOpenClick(AbstractContainerMenu menu, int slotId, Player player) {
        return dedicatedOpenRejection(menu, slotId, player) == null;
    }

    /** Null means accepted; rejection text is optional diagnostics, not authority. */
    public static String dedicatedOpenRejection(AbstractContainerMenu menu, int slotId, Player player) {
        //#if MC >= 11404 && MC <= 260300
        String reason = dedicatedStorageRejection(menu, slotId, player);
        if (reason != null) return reason;
        if (!QuickContainerAccessSession.carried(menu, player).isEmpty()) return "carried_not_empty";
        ItemStack target = menu.getSlot(slotId).getItem();
        if (target.isEmpty()) return "empty_slot";
        if (target.getCount() != 1) return "stacked_source";
        return QuickContainerAccessItems.kindOf(target) == null ? "not_storage_host" : null;
        //#else
        //$$ return "unsupported_version";
        //#endif
    }

    public static boolean canUseDedicatedStorageClick(AbstractContainerMenu menu, int slotId, Player player) {
        return dedicatedStorageRejection(menu, slotId, player) == null;
    }

    public static String dedicatedStorageRejection(AbstractContainerMenu menu, int slotId, Player player) {
        //#if MC >= 11404 && MC <= 260300
        if (!(player instanceof ServerPlayer) || player instanceof EntityPlayerMPFake) return "invalid_viewer";
        if (!QuickContainerAccess.enabled()) return "rule_disabled";
        if (menu == null || !menu.stillValid(player)) return "invalid_menu";
        if (!validSlot(menu, slotId)) return "invalid_slot";
        if (QuickContainerAccessWorkstations.isOwnSlot(menu, slotId, player)) return "workstation_slot";
        return QuickContainerAccessSession.directSlotRejection((ServerPlayer) player, menu.getSlot(slotId));
        //#else
        //$$ return "unsupported_version";
        //#endif
    }

    public static boolean canNavigateBack(AbstractContainerMenu menu, Player player) {
        //#if MC >= 11404 && MC <= 260300
        return player instanceof ServerPlayer && !(player instanceof EntityPlayerMPFake)
                && QuickContainerAccessNavigation.canNavigateBack(QuickContainerAccessSession.get(menu));
        //#else
        //$$ return false;
        //#endif
    }
    public static boolean navigateBack(ServerPlayer player, int containerId) {
        //#if MC >= 11404 && MC <= 260300
        if (player == null || player.isSpectator() || player.containerMenu.containerId != containerId) return false;
        return QuickContainerAccessNavigation.navigateBack(player, player.containerMenu);
        //#else
        //$$ return false;
        //#endif
    }

    /** Observe an actual vanilla server click; dedicated QCA storage clicks never create provenance. */
    public static void beforeVanillaClick(AbstractContainerMenu menu, int slotId, int button,
                                          Object clickType, ServerPlayer player) {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessCarriedEnderOrigins.beforeVanillaClick(player, menu, slotId, button, clickType);
        //#endif
    }
    public static void afterVanillaClick(ServerPlayer player) {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessCarriedEnderOrigins.afterVanillaClick(player);
        //#endif
    }
    public static void revokeCarriedOrigins() {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessCarriedEnderOrigins.clearAll();
        //#endif
    }
    public static void validateMenu(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (session != null && !session.valid()) session.player.closeContainer();
        //#endif
    }
    public static void onMenuRemoved(AbstractContainerMenu menu, Player player) {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessCloseDebounce.forget(menu);
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessCarriedEnderOrigins.forgetMenu(menu, player);
        //#endif
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.finish(menu, player);
        if (session != null) QuickContainerAccessMenus.sendCloseSound(session);
        //#endif
    }
    public static void closeInvalidMenus() {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessSession.closeInvalidMenus();
        //#endif
    }
    public static void closeActiveMenus() {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessSession.closeAllActiveMenus();
        //#endif
    }
    public static void resetRuntimeState() {
        //#if MC >= 11404 && MC <= 260300
        QuickContainerAccessSession.clearAll(); revokeCarriedOrigins(); QuickContainerAccessNavigation.clear(); QuickContainerAccessCloseDebounce.clear(); QuickContainerAccessAmsCompat.reset();
        //#endif
    }
    public static boolean handlePortableAnvilUse(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260300
        return QuickContainerAccessMenus.handlePortableAnvilUse(menu);
        //#else
        //$$ return false;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260300
    /** Uses the existing version-aware carried accessor; never duplicate preprocessor branches in the network layer. */
    public static boolean carriedStorageHostCandidate(AbstractContainerMenu menu, Player player) {
        return menu != null && player != null
                && QuickContainerAccessItems.isStorageHostCandidate(QuickContainerAccessSession.carried(menu, player));
    }
    private static boolean validCreativeInventorySlot(ServerPlayer player, int slot) {
        return player != null && !(player instanceof EntityPlayerMPFake) && !player.isSpectator() && player.isCreative()
                && player.containerMenu == player.inventoryMenu && QuickContainerAccess.enabled()
                && slot >= 0 && slot < QuickContainerAccessSession.inventory(player).getContainerSize();
    }
    private static int inventoryMenuSlot(AbstractContainerMenu menu, Inventory inventory, int inventorySlot) {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (slot.container == inventory && QuickContainerAccessSession.containerSlot(slot) == inventorySlot) return i;
        }
        return -1;
    }
    private static boolean isRightPickup(Object clickType, int button) { return button == 1 && "PICKUP".equals(QuickContainerAccessSession.enumName(clickType)); }
    private static boolean validSlot(AbstractContainerMenu menu, int slotId) { return slotId >= 0 && slotId < menu.slots.size(); }
    //#endif
}
