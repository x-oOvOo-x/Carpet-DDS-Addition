/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11904 && MC <= 260300
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import carpet.helpers.EntityPlayerActionPack.Action;
//$$ import carpet.helpers.EntityPlayerActionPack.ActionType;
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySession;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionAccessor;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionPackAccessor;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.item.ItemStack;
//#if MC >= 260300
//$$ import net.minecraft.util.Prediction;
//#endif
//$$ import java.util.UUID;
//$$
//$$ public final class DdsFakePlayerActions {
//$$     public static final int MODE_NONE = 0, MODE_INTERVAL = 1, MODE_CONTINUOUS = 2;
//$$     public static final int DEFAULT_INTERVAL = 12, MIN_INTERVAL = 1, MAX_INTERVAL = Integer.MAX_VALUE;
//$$     private DdsFakePlayerActions() {}
//$$
//$$     public static void handleRequest(ServerPlayer viewer, CompoundTag data) {
//$$         AbstractContainerMenu current = viewer.containerMenu;
//$$         if (current == null || !current.stillValid(viewer)) return;
//#if MC == 12111 || MC >= 260102 && MC <= 260300
//#if MC >= 12105
//$$         String actionName = data.getStringOr("action", ""); int value = data.getIntOr("value", 0);
//#else
//$$         String actionName = data.getString("action"); int value = data.contains("value") ? data.getInt("value") : 0;
//#endif
//$$         DdsFakePlayerAction action = DdsFakePlayerAction.fromWireName(actionName);
//$$         if (action == null || !handleStrictSessionRequest(viewer, current, action, value, data)) return;
//$$         if (viewer.containerMenu == current) current.broadcastChanges();
//#endif
//$$     }
//$$
//$$     /** The old menu-only ACTION_C2S requests deliberately fail closed without a strict session. */
//$$
//$$     public static void handleProviderRequest(ServerPlayer viewer, CompoundTag data) {
//$$         AbstractContainerMenu current = viewer.containerMenu;
//$$         if (current == null || !current.stillValid(viewer)) return;
//#if MC >= 12105
//$$         int containerId = data.getIntOr("container_id", -1); String targetUuid = data.getStringOr("target_uuid", "");
//$$         String actionName = data.getStringOr("action", ""); int value = data.getIntOr("value", 0);
//#else
//$$         int containerId = data.contains("container_id") ? data.getInt("container_id") : -1; String targetUuid = data.getString("target_uuid");
//$$         String actionName = data.getString("action"); int value = data.contains("value") ? data.getInt("value") : 0;
//#endif
//$$         if (containerId != current.containerId || targetUuid.isEmpty()) return;
//$$         DdsFakePlayerAction action = DdsFakePlayerAction.fromWireName(actionName);
//$$         if (action == null || !isProviderAction(action)) return;
//$$         UUID expectedTarget;
//$$         try { expectedTarget = UUID.fromString(targetUuid); }
//$$         catch (IllegalArgumentException ignored) { return; }
//$$
//$$         DdsPlayerInventorySession session = DdsPlayerInventorySessions.current(viewer, containerId, expectedTarget);
//$$         EntityPlayerMPFake target = null;
//$$         if (session != null
//$$                 && session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_FAKE
//$$                 && session.inventoryKind() != DdsPlayerInventorySession.InventoryKind.ENDER_CHEST
//$$                 && session.liveTarget() instanceof EntityPlayerMPFake)
//$$             target = (EntityPlayerMPFake) session.liveTarget();
//$$         if (target == null) {
//$$             ServerPlayer legacyTarget = DdsRemotePlayerOwnerResolver.remoteInventoryOwnerForMenu(viewer, current);
//$$             MinecraftServer server =
//#if MC >= 12001
//$$                     viewer.serverLevel().getServer();
//#else
//$$                     viewer.level.getServer();
//#endif
//$$             boolean uuidMatches = legacyTarget != null && expectedTarget.equals(legacyTarget.getUUID());
//$$             boolean liveInstanceMatches = server != null && server.getPlayerList().getPlayer(expectedTarget) == legacyTarget;
//$$             if (!DdsProviderStopGate.allowsLegacy(action, legacyTarget != null, uuidMatches, liveInstanceMatches,
//$$                     legacyTarget instanceof EntityPlayerMPFake, legacyTarget != null && legacyTarget.isAlive())) return;
//$$             target = (EntityPlayerMPFake) legacyTarget;
//$$         }
//$$         if (!target.isAlive()) return;
//$$         handleInventoryRequest(viewer, target, action, value);
//$$         if (viewer.containerMenu == current) current.broadcastChanges();
//$$     }
//$$
//#if MC == 12111 || MC >= 260102 && MC <= 260300
//$$     private static boolean handleStrictSessionRequest(ServerPlayer viewer, AbstractContainerMenu current,
//$$                                                       DdsFakePlayerAction action, int value, CompoundTag data) {
//$$         int containerId = data.getIntOr("container_id", -1);
//$$         String targetUuid = data.getStringOr("target_uuid", "");
//$$         long generation = data.getLongOr("session_generation", -1L);
//$$         if (containerId != current.containerId || targetUuid.isEmpty() || generation <= 0L) return false;
//$$         UUID expectedTarget;
//$$         try { expectedTarget = UUID.fromString(targetUuid); }
//$$         catch (IllegalArgumentException ignored) { return false; }
//$$         DdsPlayerInventorySession session = DdsPlayerInventorySessions.current(
//$$                 viewer, containerId, expectedTarget, generation);
//$$         if (session == null || session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_FAKE
//$$                 || !(session.liveTarget() instanceof EntityPlayerMPFake)) return false;
//$$         EntityPlayerMPFake target = (EntityPlayerMPFake) session.liveTarget();
//$$         if (!target.isAlive()) return false;
//$$         if (session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.ENDER_CHEST) {
//$$             switch (action) {
//$$                 case TOGGLE_SNEAK: DdsFakePlayerCarpetCompat.actionPack(target).setSneaking(!target.isShiftKeyDown()); return true;
//$$                 case DISCONNECT: viewer.closeContainer(); DdsFakePlayerCarpetCompat.disconnectFake(target); return true;
//$$                 default: return false;
//$$             }
//$$         }
//$$         if (!isStrictInventoryAction(action)) return false;
//$$         handleInventoryRequest(viewer, target, action, value);
//$$         return true;
//$$     }
//$$
//$$     private static boolean isStrictInventoryAction(DdsFakePlayerAction action) {
//$$         switch (action) {
//$$             case ATTACK_INTERVAL: case ATTACK_CONTINUOUS: case STOP_ATTACK:
//$$             case USE_INTERVAL: case USE_CONTINUOUS: case STOP_USE:
//$$             case STOP_ALL: case DROP_ALL: case DISCONNECT: case SELECT_HOTBAR: return true;
//$$             default: return false;
//$$         }
//$$     }
//#endif
//$$
//$$     private static boolean isProviderAction(DdsFakePlayerAction action) {
//$$         return DdsProviderStopGate.isExactStop(action);
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
//$$             case DROP_ALL: dropInventory(target); break;
//$$             case DISCONNECT: viewer.closeContainer(); DdsFakePlayerCarpetCompat.disconnectFake(target); break;
//$$             case SELECT_HOTBAR: if (value >= 1 && value <= 9) pack.setSlot(value); break;
//$$             default: break;
//$$         }
//$$     }
//$$
//$$     private static void dropInventory(EntityPlayerMPFake target) {
//$$         Inventory inventory = target.getInventory();
//$$         for (int i = inventory.getContainerSize() - 1; i >= 0; i--) {
//$$             ItemStack stack = inventory.getItem(i); if (stack.isEmpty()) continue;
//$$             ItemStack dropped = inventory.removeItem(i, stack.getCount());
//#if MC >= 260300
//$$             if (!dropped.isEmpty()) target.drop(dropped, false, Prediction.SERVER_ONLY);
//#else
//$$             if (!dropped.isEmpty()) target.drop(dropped, false, true);
//#endif
//$$         }
//$$         inventory.setChanged();
//$$     }
//$$
//$$     private static void setInterval(EntityPlayerActionPack pack, ActionType type, int requested) { pack.start(type, Action.interval(clampInterval(requested))); }
//$$     private static void setContinuous(EntityPlayerActionPack pack, ActionType type) { pack.start(type, Action.continuous()); }
//$$     private static void stop(EntityPlayerActionPack pack, ActionType type) { pack.start(type, null); }
//$$     public static int selectedHotbarSlot(EntityPlayerMPFake target) { return DdsFakePlayerCarpetCompat.selectedHotbarSlot(target); }
//$$     public static int mode(EntityPlayerMPFake target, ActionType type) { return mode(DdsFakePlayerCarpetCompat.actionPack(target), type); }
//$$     public static int interval(EntityPlayerMPFake target, ActionType type) { return interval(DdsFakePlayerCarpetCompat.actionPack(target), type); }
//$$     public static int mode(EntityPlayerActionPack pack, ActionType type) {
//$$         Action action = current(pack, type); if (action == null || action.done || action.limit == 1) return MODE_NONE;
//$$         return ((EntityPlayerActionAccessor) (Object) action).dds$isContinuous() ? MODE_CONTINUOUS : MODE_INTERVAL;
//$$     }
//$$     public static int interval(EntityPlayerActionPack pack, ActionType type) { Action action = current(pack, type); return action == null ? DEFAULT_INTERVAL : clampInterval(action.interval); }
//$$     private static Action current(EntityPlayerActionPack pack, ActionType type) { return ((EntityPlayerActionPackAccessor) (Object) pack).dds$getActions().get(type); }
//$$     public static int clampInterval(int value) { return Math.max(MIN_INTERVAL, value); }
//$$ }
//#else
public final class DdsFakePlayerActions { private DdsFakePlayerActions() {} }
//#endif
