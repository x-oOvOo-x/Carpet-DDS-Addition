/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.server.level.ServerPlayer;
//$$
//$$ import java.util.Collection;
//$$ import java.util.Collections;
//$$ import java.util.Map;
//$$ import java.util.Set;
//$$ import java.util.UUID;
//$$ import java.util.WeakHashMap;
//$$ import java.util.concurrent.ConcurrentHashMap;
//$$ import java.util.concurrent.atomic.AtomicLong;
//#endif

final class UndoRuntimeState {
    private UndoRuntimeState() {}

    //#if MC >= 12109

    //$$ private static final AtomicLong NEXT_ID = new AtomicLong(1L);
    //$$ private static final Map<UUID, PlayerUndoHistory> HISTORIES = new ConcurrentHashMap<>();
    //$$ private static final Map<Long, UndoRecord> RECORDS_BY_ID = new ConcurrentHashMap<>();
    //$$ private static final Set<Long> RECORDS_WITH_ASYNC_ORIGIN = ConcurrentHashMap.newKeySet();
    //$$ private static final Set<Long> SUPPRESSED_ASYNC_ORIGINS = ConcurrentHashMap.newKeySet();
    //$$ private static final Map<UndoRecord, PlayerInventorySnapshot> CONTAINER_BEFORE =
    //$$         Collections.synchronizedMap(new WeakHashMap<>());
    //$$ private static final Map<UndoRecord, PlayerInventorySnapshot> CONTAINER_AFTER =
    //$$         Collections.synchronizedMap(new WeakHashMap<>());
    //$$ private static final ThreadLocal<Boolean> RESTORING =
    //$$         ThreadLocal.withInitial(() -> false);
    //$$
    //$$ static long nextRecordId() {
    //$$     return NEXT_ID.getAndIncrement();
    //$$ }
    //$$
    //$$ static PlayerUndoHistory findHistory(UUID owner) {
    //$$     return HISTORIES.get(owner);
    //$$ }
    //$$
    //$$ static PlayerUndoHistory history(ServerPlayer player) {
    //$$     return HISTORIES.computeIfAbsent(player.getUUID(), ignored -> new PlayerUndoHistory());
    //$$ }
    //$$
    //$$ static PlayerUndoHistory removeHistory(ServerPlayer player) {
    //$$     return HISTORIES.remove(player.getUUID());
    //$$ }
    //$$
    //$$ static Collection<PlayerUndoHistory> histories() {
    //$$     return HISTORIES.values();
    //$$ }
    //$$
    //$$ static void registerRecord(UndoRecord record) {
    //$$     RECORDS_BY_ID.put(record.id(), record);
    //$$ }
    //$$
    //$$ /*
    //$$  * Temporary unregistering is used while a record sits in the redo
    //$$  * stack. Suppression/freeze state must survive that transition.
    //$$  */
    //$$ static void unregisterRecord(UndoRecord record) {
    //$$     if (record == null) return;
    //$$     RECORDS_BY_ID.remove(record.id(), record);
    //$$     RECORDS_WITH_ASYNC_ORIGIN.remove(record.id());
    //$$ }
    //$$
    //$$ /*
    //$$  * Permanent disposal owns every piece of runtime state attached to
    //$$  * the record. Unlike unregisterRecord(), nothing may survive here.
    //$$  */
    //$$ static void disposeRecord(UndoRecord record) {
    //$$     if (record == null) return;
    //$$     long recordId = record.id();
    //$$     UndoAsyncFreeze.discard(recordId);
    //$$     SUPPRESSED_ASYNC_ORIGINS.remove(recordId);
    //$$     unregisterRecord(record);
    //$$     clearContainerSnapshots(record);
    //$$ }
    //$$
    //$$ static UndoRecord record(long recordId) {
    //$$     return RECORDS_BY_ID.get(recordId);
    //$$ }
    //$$
    //$$ static boolean isRegistered(UndoRecord record) {
    //$$     return record != null && RECORDS_BY_ID.get(record.id()) == record;
    //$$ }
    //$$
    //$$ static void markAsyncOrigin(long recordId) {
    //$$     if (recordId != 0L) RECORDS_WITH_ASYNC_ORIGIN.add(recordId);
    //$$ }
    //$$
    //$$ static boolean hasAsyncOrigin(long recordId) {
    //$$     return recordId != 0L && RECORDS_WITH_ASYNC_ORIGIN.contains(recordId);
    //$$ }
    //$$
    //$$ static void suppressAsyncOrigin(long recordId) {
    //$$     if (recordId != 0L) SUPPRESSED_ASYNC_ORIGINS.add(recordId);
    //$$ }
    //$$
    //$$ static boolean isAsyncOriginSuppressed(long recordId) {
    //$$     return recordId != 0L && SUPPRESSED_ASYNC_ORIGINS.contains(recordId);
    //$$ }
    //$$
    //$$ static void restoreSuppressedAsyncOrigin(long recordId) {
    //$$     if (recordId == 0L || !SUPPRESSED_ASYNC_ORIGINS.remove(recordId)) return;
    //$$     RECORDS_WITH_ASYNC_ORIGIN.add(recordId);
    //$$ }
    //$$
    //$$ static void captureContainerBefore(UndoRecord record, ServerPlayer player) {
    //$$     CONTAINER_BEFORE.put(record, PlayerInventorySnapshot.capture(player));
    //$$ }
    //$$
    //$$ static void captureContainerAfter(UndoRecord record, ServerPlayer player) {
    //$$     CONTAINER_AFTER.put(record, PlayerInventorySnapshot.capture(player));
    //$$ }
    //$$
    //$$ static PlayerInventorySnapshot containerBefore(UndoRecord record) {
    //$$     return CONTAINER_BEFORE.get(record);
    //$$ }
    //$$
    //$$ static PlayerInventorySnapshot containerAfter(UndoRecord record) {
    //$$     return CONTAINER_AFTER.get(record);
    //$$ }
    //$$
    //$$ static boolean hasContainerBefore(UndoRecord record) {
    //$$     return CONTAINER_BEFORE.containsKey(record);
    //$$ }
    //$$
    //$$ static void clearContainerSnapshots(UndoRecord record) {
    //$$     CONTAINER_BEFORE.remove(record);
    //$$     CONTAINER_AFTER.remove(record);
    //$$ }
    //$$
    //$$ static long auxiliaryEstimatedBytes(UndoRecord record) {
    //$$     long result = 0L;
    //$$     PlayerInventorySnapshot before = containerBefore(record);
    //$$     PlayerInventorySnapshot after = containerAfter(record);
    //$$     if (before != null) result += before.estimatedBytes();
    //$$     if (after != null) result += after.estimatedBytes();
    //$$     return result;
    //$$ }
    //$$
    //$$ static boolean isRestoring() {
    //$$     return RESTORING.get();
    //$$ }
    //$$
    //$$ static void withRestoring(Runnable action) {
    //$$     boolean previous = RESTORING.get();
    //$$     RESTORING.set(true);
    //$$     try {
    //$$         action.run();
    //$$     }
    //$$     finally {
    //$$         if (previous) RESTORING.set(true);
    //$$         else RESTORING.remove();
    //$$     }
    //$$ }
    //$$
    //$$ static void clearAll() {
    //$$     for (PlayerUndoHistory history : HISTORIES.values()) history.clear();
    //$$     HISTORIES.clear();
    //$$     RECORDS_BY_ID.clear();
    //$$     RECORDS_WITH_ASYNC_ORIGIN.clear();
    //$$     SUPPRESSED_ASYNC_ORIGINS.clear();
    //$$     CONTAINER_BEFORE.clear();
    //$$     CONTAINER_AFTER.clear();
    //$$     UndoContext.clear();
    //$$     RESTORING.remove();
    //$$ }

    //#endif
}