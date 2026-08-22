/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import carpet.helpers.EntityPlayerActionPack.Action;
//$$ import carpet.helpers.EntityPlayerActionPack.ActionType;
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionAccessor;
//$$ import carpetddsaddition.mixin.integration.carpet.EntityPlayerActionPackAccessor;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$
//$$ public final class DdsFakePlayerActions {
//$$     public static final int MODE_NONE = 0, MODE_INTERVAL = 1, MODE_CONTINUOUS = 2;
//$$     public static final int DEFAULT_INTERVAL = 12, MIN_INTERVAL = 1, MAX_INTERVAL = 72000;
//$$     private DdsFakePlayerActions() {}
//$$
//$$     public static void handleRequest(ServerPlayer viewer, CompoundTag data) {
//$$         if (!(viewer.containerMenu instanceof DdsFakePlayerMenu)) return;
//$$         DdsFakePlayerMenu menu = (DdsFakePlayerMenu) viewer.containerMenu;
//$$         EntityPlayerMPFake target = menu.target();
//$$         if (target == null || !menu.stillValid(viewer)) return;
//#if MC >= 12105
//$$         String actionName = data.getStringOr("action", "");
//$$         int value = data.getIntOr("value", 0);
//#else
//$$         String actionName = data.getString("action");
//$$         int value = data.contains("value") ? data.getInt("value") : 0;
//#endif
//$$         DdsFakePlayerAction action = DdsFakePlayerAction.fromWireName(actionName);
//$$         if (action == null) return;
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
//$$         if (viewer.containerMenu instanceof DdsFakePlayerMenu) viewer.containerMenu.broadcastChanges();
//$$     }
//$$
//$$     private static void setInterval(EntityPlayerActionPack pack, ActionType type, int requested) { pack.start(type, Action.interval(clampInterval(requested))); }
//$$     private static void setContinuous(EntityPlayerActionPack pack, ActionType type) { pack.start(type, Action.continuous()); }
//$$     private static void stop(EntityPlayerActionPack pack, ActionType type) { pack.start(type, null); }
//$$
//$$     public static int mode(EntityPlayerActionPack pack, ActionType type) {
//$$         Action action = current(pack, type);
//$$         if (action == null || action.done || action.limit == 1) return MODE_NONE;
//$$         return ((EntityPlayerActionAccessor) (Object) action).dds$isContinuous() ? MODE_CONTINUOUS : MODE_INTERVAL;
//$$     }
//$$
//$$     public static int interval(EntityPlayerActionPack pack, ActionType type) {
//$$         Action action = current(pack, type);
//$$         return action == null ? DEFAULT_INTERVAL : clampInterval(action.interval);
//$$     }
//$$
//$$     private static Action current(EntityPlayerActionPack pack, ActionType type) {
//$$         return ((EntityPlayerActionPackAccessor) (Object) pack).dds$getActions().get(type);
//$$     }
//$$
//$$     public static int clampInterval(int value) { return Math.max(MIN_INTERVAL, Math.min(MAX_INTERVAL, value)); }
//$$ }
//#else
public final class DdsFakePlayerActions { private DdsFakePlayerActions() {} }
//#endif
