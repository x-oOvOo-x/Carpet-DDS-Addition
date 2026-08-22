/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoRedo;

//#if MC >= 12109
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

final class UndoExecutor {
    private UndoExecutor() {}

    //#if MC >= 12109

    //$$ static int undo(ServerPlayer player) {
    //$$     if (!canUse(player)) return 0;
    //$$     UndoActionRecorder.finishPendingContainerAction(player);
    //$$     PlayerUndoHistory history = UndoRuntimeState.history(player);
    //$$     MinecraftServer server = player.level().getServer();
    //$$     UndoRecord selected = null;
    //$$     int conflicts = 0;
    //$$     while ((selected = history.pollUndo()) != null) {
    //$$         boolean coupledInventory = hasContainerInventory(selected);
    //$$         if (isEffectivelyEmpty(selected)) {
    //$$             suppressPendingAsyncIfNeeded(selected);
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         if (!selected.hasAfterSnapshot()) selected.captureAfter(server);
    //$$         if (coupledInventory && !containerInventoryMatches(selected, player, true)) {
    //$$             conflicts++;
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         int recordConflicts = selected.pruneUndoConflicts(server);
    //$$         conflicts += recordConflicts;
    //$$         if (coupledInventory && recordConflicts > 0) {
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         if (isEffectivelyEmpty(selected)) {
    //$$             suppressPendingAsyncIfNeeded(selected);
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         break;
    //$$     }
    //$$     if (selected == null) {
    //$$         message(player, conflicts == 0
    //$$                 ? "没有可撤销的操作 | Nothing to undo"
    //$$                 : "没有可安全撤销的操作，后续更改已保留 | Nothing safe to undo; later changes were preserved");
    //$$         return 0;
    //$$     }
    //$$     final UndoRecord record = selected;
    //$$     suppressPendingAsyncIfNeeded(record);
    //$$     UndoRuntimeState.unregisterRecord(record);
    //$$     UndoRuntimeState.withRestoring(() -> {
    //$$         record.restoreBefore(server);
    //$$         restoreContainerInventory(record, player, false);
    //$$     });
    //$$     history.pushRedo(record);
    //$$     messageCompletion(player, "Undo", conflicts);
    //$$     return 1;
    //$$ }

    //$$ static int redo(ServerPlayer player) {
    //$$     if (!canUse(player)) return 0;
    //$$     UndoActionRecorder.finishPendingContainerAction(player);
    //$$     PlayerUndoHistory history = UndoRuntimeState.history(player);
    //$$     MinecraftServer server = player.level().getServer();
    //$$     UndoRecord selected = null;
    //$$     int conflicts = 0;
    //$$     while ((selected = history.pollRedo()) != null) {
    //$$         boolean coupledInventory = hasContainerInventory(selected);
    //$$         if (!selected.hasAfterSnapshot()) {
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         if (coupledInventory && !containerInventoryMatches(selected, player, false)) {
    //$$             conflicts++;
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         int recordConflicts = selected.pruneRedoConflicts(server);
    //$$         conflicts += recordConflicts;
    //$$         if (coupledInventory && recordConflicts > 0) {
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         if (isEffectivelyEmpty(selected)) {
    //$$             UndoRuntimeState.disposeRecord(selected);
    //$$             continue;
    //$$         }
    //$$         break;
    //$$     }
    //$$     if (selected == null) {
    //$$         message(player, conflicts == 0
    //$$                 ? "没有可重做的操作 | Nothing to redo"
    //$$                 : "没有可安全重做的操作，后续更改已保留 | Nothing safe to redo; later changes were preserved");
    //$$         return 0;
    //$$     }
    //$$     final UndoRecord record = selected;
    //$$     UndoRuntimeState.restoreSuppressedAsyncOrigin(record.id());
    //$$     UndoRuntimeState.withRestoring(() -> {
    //$$         UndoAsyncFreeze.withForcedOrigin(record.id(), () -> record.restoreAfter(server));
    //$$         restoreContainerInventory(record, player, true);
    //$$     });
    //$$     history.pushUndo(record);
    //$$     messageCompletion(player, "Redo", conflicts);
    //$$     return 1;
    //$$ }

    //$$ private static boolean hasContainerInventory(UndoRecord record) {
    //$$     return UndoRuntimeState.hasContainerBefore(record);
    //$$ }

    //$$ private static boolean containerInventoryMatches(UndoRecord record, ServerPlayer player, boolean after) {
    //$$     PlayerInventorySnapshot snapshot = after ? UndoRuntimeState.containerAfter(record) : UndoRuntimeState.containerBefore(record);
    //$$     return snapshot != null && snapshot.matchesCurrent(player);
    //$$ }

    //$$ private static void restoreContainerInventory(UndoRecord record, ServerPlayer player, boolean after) {
    //$$     PlayerInventorySnapshot snapshot = after ? UndoRuntimeState.containerAfter(record) : UndoRuntimeState.containerBefore(record);
    //$$     if (snapshot != null) snapshot.restore(player);
    //$$ }

    //$$ private static boolean isEffectivelyEmpty(UndoRecord record) {
    //$$     return record.isEmpty() && !hasContainerInventory(record);
    //$$ }

    //$$ private static void suppressPendingAsyncIfNeeded(UndoRecord record) {
    //$$     if (record != null && UndoRuntimeState.hasAsyncOrigin(record.id())) UndoRuntimeState.suppressAsyncOrigin(record.id());
    //$$ }

    //$$ private static boolean canUse(ServerPlayer player) {
    //$$     if (!UndoRedo.enabled()) {
    //$$         message(player, "undoRedo 规则未开启 | Undo/Redo is disabled by the undoRedo rule");
    //$$         return false;
    //$$     }
    //$$     if (!player.isCreative()) {
    //$$         message(player, "仅创造模式可使用 Undo/Redo | Undo/Redo is only available in Creative Mode");
    //$$         return false;
    //$$     }
    //$$     return true;
    //$$ }

    //$$ private static void messageCompletion(ServerPlayer player, String action, int conflicts) {
    //$$     String zh = "Undo".equals(action) ? "撤销" : "重做";
    //$$     if (conflicts <= 0) {
    //$$         message(player, zh + "完成 | " + action + " complete");
    //$$         return;
    //$$     }
    //$$     message(player, zh + "完成；已保留 " + conflicts + " 项后续更改 | " + action + " complete; "
    //$$             + conflicts + (conflicts == 1 ? " later change was preserved." : " later changes were preserved."));
    //$$ }

    //$$ private static void message(ServerPlayer player, String text) {
    //#if MC <= 12111
    //$$ player.displayClientMessage(Component.literal(text), true);
    //#else
    //$$ player.sendSystemMessage(Component.literal(text), true);
    //#endif
    //$$ }

    //#endif
}