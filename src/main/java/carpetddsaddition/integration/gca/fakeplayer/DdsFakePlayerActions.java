/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11904 && MC <= 260200
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import carpet.helpers.EntityPlayerActionPack.Action;
//$$ import carpet.helpers.EntityPlayerActionPack.ActionType;
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
//$$ import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessLease;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionAccessor;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionPackAccessor;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.item.ItemStack;
//$$
//$$ public final class DdsFakePlayerActions {
//$$     public static final int MODE_NONE = 0, MODE_INTERVAL = 1, MODE_CONTINUOUS = 2;
//$$     public static final int DEFAULT_INTERVAL = 12, MIN_INTERVAL = 1, MAX_INTERVAL = 12000;
//$$     private DdsFakePlayerActions() {}
//$$
//$$     public static void handleRequest(ServerPlayer viewer, CompoundTag data) {
//$$         AbstractContainerMenu current = viewer.containerMenu;
//$$         DdsFakePlayerMenu inventoryMenu = current instanceof DdsFakePlayerMenu ? (DdsFakePlayerMenu) current : null;
//$$         DdsFakePlayerEnderChestMenu enderMenu = current instanceof DdsFakePlayerEnderChestMenu ? (DdsFakePlayerEnderChestMenu) current : null;
//$$         if (inventoryMenu == null && enderMenu == null) return;
//$$         EntityPlayerMPFake target = inventoryMenu != null ? inventoryMenu.target() : enderMenu.target();
//$$         if (target == null || !current.stillValid(viewer)) return;
//#if MC >= 12105
//$$         String actionName = data.getStringOr("action", ""); int value = data.getIntOr("value", 0);
//#else
//$$         String actionName = data.getString("action"); int value = data.contains("value") ? data.getInt("value") : 0;
//#endif
//$$         DdsFakePlayerAction action = DdsFakePlayerAction.fromWireName(actionName); if (action == null) return;
//$$         if (enderMenu != null) handleEnderRequest(viewer, target, enderMenu, action); else handleInventoryRequest(viewer, target, action, value);
//$$         if (viewer.containerMenu == current) current.broadcastChanges();
//$$     }
//$$
//$$     private static void handleInventoryRequest(ServerPlayer viewer, EntityPlayerMPFake target, DdsFakePlayerAction action, int value) {
//$$         EntityPlayerActionPack pack = DdsFakePlayerCarpetCompat.actionPack(target);
//$$         switch (action) {
//$$             case ATTACK_INTERVAL: setInterval(pack, ActionType.ATTACK, value); break;
//$$             case ATTACK_CONTINUOUS: setContinuous(pack, ActionType.ATTACK); break;
//$$             case STOP_ATTACK: stop(pack, ActionType.ATTACK); break;
//$$             case USE_INTERVAL: setInterval(pack, ActionType.USE, value); break;
//$$             case USE_CONTINUOUS: setContinuous(pack, ActionType.USE); break;
//$$             case STOP_USE: stop(pack, ActionType.USE); break;
//$$             case STOP_ALL: pack.stopAll(); break;
//$$             case DROP_ALL: pack.drop(-2, true); break;
//$$             case DISCONNECT: viewer.closeContainer(); DdsFakePlayerCarpetCompat.disconnectFake(target); break;
//$$             case SELECT_HOTBAR: if (value >= 1 && value <= 9) pack.setSlot(value); break;
//$$             default: break;
//$$         }
//$$     }
//$$
//$$     private static void handleEnderRequest(ServerPlayer viewer, EntityPlayerMPFake target, DdsFakePlayerEnderChestMenu menu, DdsFakePlayerAction action) {
//$$         Container ender = target.getEnderChestInventory();
//$$         if (ender != menu.ddsCanonicalContainer() || ender.getContainerSize() != menu.rows() * 9) return;
//$$         switch (action) {
//$$             case ENDER_DROP_ALL: dropAll(viewer, target, ender); break;
//$$             case ENDER_STORE_ALL: moveAll(viewer, viewer.getInventory(), 36, ender, ender.getContainerSize()); break;
//$$             case ENDER_TAKE_ALL: moveAll(viewer, ender, ender.getContainerSize(), viewer.getInventory(), 36); break;
//$$             case TOGGLE_SNEAK: DdsFakePlayerCarpetCompat.actionPack(target).setSneaking(!target.isShiftKeyDown()); break;
//$$             case DISCONNECT: viewer.closeContainer(); DdsFakePlayerCarpetCompat.disconnectFake(target); break;
//$$             default: break;
//$$         }
//$$     }
//$$
//$$     private static void dropAll(ServerPlayer viewer, EntityPlayerMPFake target, Container container) {
//$$         for (int i = container.getContainerSize() - 1; i >= 0; i--) {
//$$             ItemStack stack = container.getItem(i); if (stack.isEmpty() || QuickContainerAccessLease.isHeldByOther(viewer, stack)) continue;
//$$             ItemStack dropped = container.removeItem(i, stack.getCount()); if (!dropped.isEmpty()) target.drop(dropped, false, true);
//$$         }
//$$         container.setChanged();
//$$     }
//$$
//$$     private static void moveAll(ServerPlayer viewer, Container from, int fromSize, Container to, int toSize) {
//$$         int sources = Math.min(fromSize, from.getContainerSize()), targets = Math.min(toSize, to.getContainerSize());
//$$         for (int i = 0; i < sources; i++) {
//$$             ItemStack source = from.getItem(i); if (source.isEmpty() || QuickContainerAccessLease.isHeldByOther(viewer, source)) continue;
//$$             mergeInto(viewer, source, to, targets); fillEmpty(source, to, targets); if (source.isEmpty()) from.setItem(i, ItemStack.EMPTY);
//$$         }
//$$         from.setChanged(); to.setChanged();
//$$     }
//$$
//$$     private static void mergeInto(ServerPlayer viewer, ItemStack source, Container target, int size) {
//$$         for (int i = 0; i < size && !source.isEmpty(); i++) {
//$$             ItemStack existing = target.getItem(i);
//$$             if (existing.isEmpty() || QuickContainerAccessLease.isHeldByOther(viewer, existing) || !QuickContainerAccessItems.sameItemAndData(source, existing) || !target.canPlaceItem(i, source)) continue;
//$$             int moved = Math.min(source.getCount(), maxStack(target, existing) - existing.getCount()); if (moved > 0) { existing.grow(moved); source.shrink(moved); }
//$$         }
//$$     }
//$$
//$$     private static void fillEmpty(ItemStack source, Container target, int size) {
//$$         for (int i = 0; i < size && !source.isEmpty(); i++) {
//$$             if (!target.getItem(i).isEmpty() || !target.canPlaceItem(i, source)) continue;
//$$             int moved = Math.min(source.getCount(), maxStack(target, source)); ItemStack placed = source.copy(); placed.setCount(moved); target.setItem(i, placed); source.shrink(moved);
//$$         }
//$$     }
//$$
//$$     private static int maxStack(Container container, ItemStack stack) {
//#if MC >= 12006
//$$         return Math.min(stack.getMaxStackSize(), container.getMaxStackSize(stack));
//#else
//$$         return Math.min(stack.getMaxStackSize(), container.getMaxStackSize());
//#endif
//$$     }
//$$
//$$     private static void setInterval(EntityPlayerActionPack pack, ActionType type, int requested) { pack.start(type, Action.interval(clampInterval(requested))); }
//$$     private static void setContinuous(EntityPlayerActionPack pack, ActionType type) { pack.start(type, Action.continuous()); }
//$$     private static void stop(EntityPlayerActionPack pack, ActionType type) { pack.start(type, null); }
//$$     public static int mode(EntityPlayerActionPack pack, ActionType type) {
//$$         Action action = current(pack, type); if (action == null || action.done || action.limit == 1) return MODE_NONE;
//$$         return ((EntityPlayerActionAccessor) (Object) action).dds$isContinuous() ? MODE_CONTINUOUS : MODE_INTERVAL;
//$$     }
//$$     public static int interval(EntityPlayerActionPack pack, ActionType type) { Action action = current(pack, type); return action == null ? DEFAULT_INTERVAL : clampInterval(action.interval); }
//$$     private static Action current(EntityPlayerActionPack pack, ActionType type) { return ((EntityPlayerActionPackAccessor) (Object) pack).dds$getActions().get(type); }
//$$     public static int clampInterval(int value) { return Math.max(MIN_INTERVAL, Math.min(MAX_INTERVAL, value)); }
//$$ }
//#else
public final class DdsFakePlayerActions { private DdsFakePlayerActions() {} }
//#endif
