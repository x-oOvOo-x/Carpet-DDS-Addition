/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import java.util.ArrayDeque;
//$$ import java.util.Deque;
//#endif

final class UndoHistoryStack {

    //#if MC >= 12109

    //$$ static final long MAX_HISTORY_BYTES = 32L * 1024L * 1024L;
    //$$ private static final int ASYNC_TRIM_INTERVAL = 8;
    //$$
    //$$ private final Deque<UndoRecord> undo = new ArrayDeque<>();
    //$$ private final Deque<UndoRecord> redo = new ArrayDeque<>();
    //$$ private int asyncScopesUntilTrim = 1;
    //$$
    //$$ void commit(UndoRecord record) {
    //$$     clearRedo();
    //$$     undo.addLast(record);
    //$$     trim();
    //$$     resetAsyncTrimCounter();
    //$$ }
    //$$
    //$$ UndoRecord pollUndo() {
    //$$     return undo.pollLast();
    //$$ }
    //$$
    //$$ UndoRecord pollRedo() {
    //$$     UndoRecord record = redo.pollLast();
    //$$     if (record != null) UndoAsyncFreeze.beginRedoAttempt(record.id());
    //$$     return record;
    //$$ }
    //$$
    //$$ void pushUndo(UndoRecord record) {
    //$$     UndoRuntimeState.registerRecord(record);
    //$$     UndoAsyncFreeze.resume(record.id());
    //$$     undo.addLast(record);
    //$$     trim();
    //$$     resetAsyncTrimCounter();
    //$$ }
    //$$
    //$$ void pushRedo(UndoRecord record) {
    //$$     UndoAsyncFreeze.freeze(record.id());
    //$$     redo.addLast(record);
    //$$     trim();
    //$$ }
    //$$
    //$$ void clear() {
    //$$     disposeAll(undo);
    //$$     disposeAll(redo);
    //$$     undo.clear();
    //$$     redo.clear();
    //$$     resetAsyncTrimCounter();
    //$$ }
    //$$
    //$$ private void clearRedo() {
    //$$     while (!redo.isEmpty()) disposeHistoricalRecord(redo.removeFirst());
    //$$ }
    //$$
    //$$ private static void disposeAll(Deque<UndoRecord> records) {
    //$$     for (UndoRecord record : records) disposeHistoricalRecord(record);
    //$$ }
    //$$
    //$$ private static void disposeHistoricalRecord(UndoRecord record) {
    //$$     UndoRuntimeState.disposeRecord(record);
    //$$ }
    //$$
    //$$ void trimAfterAsync() {
    //$$     if (--asyncScopesUntilTrim > 0) return;
    //$$     trim();
    //$$     asyncScopesUntilTrim = ASYNC_TRIM_INTERVAL;
    //$$ }
    //$$
    //$$ void trim() {
    //$$     long bytes = estimatedBytes();
    //$$     while (bytes > MAX_HISTORY_BYTES) {
    //$$         UndoRecord removed = removeOldest();
    //$$         if (removed == null) break;
    //$$         long removedBytes = estimatedRecordBytes(removed);
    //$$         disposeHistoricalRecord(removed);
    //$$         bytes -= removedBytes;
    //$$     }
    //$$ }
    //$$
    //$$ private UndoRecord removeOldest() {
    //$$     if (!undo.isEmpty()) return undo.removeFirst();
    //$$     if (!redo.isEmpty()) return redo.removeFirst();
    //$$     return null;
    //$$ }
    //$$
    //$$ private long estimatedBytes() {
    //$$     long result = 0L;
    //$$     for (UndoRecord record : undo) result += estimatedRecordBytes(record);
    //$$     for (UndoRecord record : redo) result += estimatedRecordBytes(record);
    //$$     return result;
    //$$ }
    //$$
    //$$ private static long estimatedRecordBytes(UndoRecord record) {
    //$$     return record.estimatedBytes()
    //$$             + UndoRuntimeState.auxiliaryEstimatedBytes(record);
    //$$ }
    //$$
    //$$ private void resetAsyncTrimCounter() {
    //$$     asyncScopesUntilTrim = 1;
    //$$ }

    //#endif
}