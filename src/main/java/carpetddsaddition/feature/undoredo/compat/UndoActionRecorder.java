/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoCause;
import carpetddsaddition.feature.undoredo.UndoRedo;

//#if MC >= 12109
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

/** Owns the lifecycle of player-originated Undo/Redo recording scopes. */
public final class UndoActionRecorder {
    private UndoActionRecorder() {}

    //#if MC >= 12109
    //$$ public static void beginPlayerAction(ServerPlayer player, UndoCause cause) {
    //$$     PlayerUndoHistory existing = UndoRuntimeState.findHistory(player.getUUID());
    //$$     if (existing != null && existing.pendingContainerRecord != null) finishPendingContainerAction(player);
    //$$     if (!canRecord(player) || UndoRuntimeState.isRestoring()) return;
    //$$     PlayerUndoHistory history = UndoRuntimeState.history(player);
    //$$     if (history.activeRecord != null) {
    //$$         if (isActiveRecordConsistent(history)) {
    //$$             history.activeDepth++;
    //$$             return;
    //$$         }
    //$$         discardDanglingActiveRecord(history);
    //$$     }
    //$$     beginRecord(history, new UndoRecord(UndoRuntimeState.nextRecordId(), player.getUUID(), cause));
    //$$ }
    //$$ public static void endPlayerAction(ServerPlayer player) {
    //$$     PlayerUndoHistory history = UndoRuntimeState.findHistory(player.getUUID());
    //$$     if (history == null || history.activeRecord == null) return;
    //$$     if (!isActiveRecordConsistent(history)) {
    //$$         discardDanglingActiveRecord(history);
    //$$         return;
    //$$     }
    //$$     if (--history.activeDepth > 0) return;
    //$$     UndoRecord record = detachActiveRecord(history);
    //$$     finalizeRecord(player, history, record);
    //$$ }
    //$$ public static void beginContainerAction(ServerPlayer player) {
    //$$     if (!canRecord(player) || UndoRuntimeState.isRestoring()) return;
    //$$     PlayerUndoHistory history = UndoRuntimeState.history(player);
    //$$     if (history.activeRecord != null) {
    //$$         if (isActiveRecordConsistent(history)) {
    //$$             history.activeDepth++;
    //$$             return;
    //$$         }
    //$$         discardDanglingActiveRecord(history);
    //$$     }
    //$$     UndoRecord record = history.pendingContainerRecord;
    //$$     if (record != null) history.pendingContainerRecord = null;
    //$$     else {
    //$$         record = new UndoRecord(UndoRuntimeState.nextRecordId(), player.getUUID(), UndoCause.CONTAINER_MENU);
    //$$         UndoRuntimeState.captureContainerBefore(record, player);
    //$$     }
    //$$     beginRecord(history, record);
    //$$ }
    //$$ public static void endContainerAction(ServerPlayer player) {
    //$$     PlayerUndoHistory history = UndoRuntimeState.findHistory(player.getUUID());
    //$$     if (history == null || history.activeRecord == null) return;
    //$$     if (!isActiveRecordConsistent(history)) {
    //$$         discardDanglingActiveRecord(history);
    //$$         return;
    //$$     }
    //$$     if (--history.activeDepth > 0) return;
    //$$     UndoRecord record = detachActiveRecord(history);
    //$$     if (!player.containerMenu.getCarried().isEmpty()) {
    //$$         history.pendingContainerRecord = record;
    //$$         return;
    //$$     }
    //$$     finalizeRecord(player, history, record);
    //$$ }
    //$$ public static void finishPendingContainerAction(ServerPlayer player) {
    //$$     PlayerUndoHistory history = UndoRuntimeState.findHistory(player.getUUID());
    //$$     if (history == null || history.pendingContainerRecord == null) return;
    //$$     UndoRecord record = history.pendingContainerRecord;
    //$$     history.pendingContainerRecord = null;
    //$$     finalizeRecord(player, history, record);
    //$$ }
    //$$ private static void beginRecord(PlayerUndoHistory history, UndoRecord record) {
    //$$     history.activeRecord = record;
    //$$     history.activeDepth = 1;
    //$$     UndoRuntimeState.registerRecord(record);
    //$$     UndoContext.push(record);
    //$$ }
    //$$ private static UndoRecord detachActiveRecord(PlayerUndoHistory history) {
    //$$     UndoRecord record = history.activeRecord;
    //$$     history.activeRecord = null;
    //$$     history.activeDepth = 0;
    //$$     UndoContext.pop(record);
    //$$     return record;
    //$$ }
    //$$ private static boolean isActiveRecordConsistent(PlayerUndoHistory history) {
    //$$     return history.activeRecord != null && history.activeDepth > 0 && UndoContext.current() == history.activeRecord;
    //$$ }
    //$$ private static void discardDanglingActiveRecord(PlayerUndoHistory history) {
    //$$     UndoRecord active = history.activeRecord;
    //$$     history.activeRecord = null;
    //$$     history.activeDepth = 0;
    //$$     if (active == null) return;
    //$$     if (UndoContext.current() == active) UndoContext.pop(active);
    //$$     UndoRuntimeState.disposeRecord(active);
    //$$ }
    //$$ private static void finalizeRecord(ServerPlayer player, PlayerUndoHistory history, UndoRecord record) {
    //$$     MinecraftServer server = player.level().getServer();
    //$$     record.discardTentativeBlocks();
    //$$     record.discardUnchangedBlocks(server);
    //$$     record.discardUnchangedEntities(server);
    //$$     PlayerInventorySnapshot beforeInventory = UndoRuntimeState.containerBefore(record);
    //$$     boolean inventoryChanged = beforeInventory != null && !beforeInventory.matchesCurrent(player);
    //$$     if (!inventoryChanged) UndoRuntimeState.clearContainerSnapshots(record);
    //$$     if (!record.isEmpty() || inventoryChanged || UndoRuntimeState.hasAsyncOrigin(record.id())) {
    //$$         record.captureAfter(server);
    //$$         if (inventoryChanged) UndoRuntimeState.captureContainerAfter(record, player);
    //$$         history.commit(record);
    //$$     } else UndoRuntimeState.unregisterRecord(record);
    //$$ }
    //$$ private static boolean canRecord(ServerPlayer player) { return UndoRedo.enabled() && player.isCreative(); }
    //#endif

    public static void abortDanglingActions() {
        //#if MC >= 12109
        //$$ for (PlayerUndoHistory history : UndoRuntimeState.histories())
        //$$     if (history.activeRecord != null) discardDanglingActiveRecord(history);
        //$$ UndoContext.clear();
        //#endif
    }
}
