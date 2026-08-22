/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

final class PlayerUndoHistory {
    //#if MC >= 12109
    //$$ private final UndoHistoryStack history = new UndoHistoryStack();
    //$$ UndoRecord activeRecord;
    //$$ int activeDepth;
    //$$ UndoRecord pendingContainerRecord;
    //$$
    //$$ void commit(UndoRecord record) { history.commit(record); }
    //$$ UndoRecord pollUndo() { return history.pollUndo(); }
    //$$ UndoRecord pollRedo() { return history.pollRedo(); }
    //$$ void pushUndo(UndoRecord record) { history.pushUndo(record); }
    //$$ void pushRedo(UndoRecord record) { history.pushRedo(record); }
    //$$ void trimAfterAsync() { history.trimAfterAsync(); }
    //$$ void clear() {
    //$$     clearActiveContext();
    //$$     history.clear();
    //$$     disposeTransientRecord(activeRecord);
    //$$     disposeTransientRecord(pendingContainerRecord);
    //$$     activeRecord = null;
    //$$     activeDepth = 0;
    //$$     pendingContainerRecord = null;
    //$$ }
    //$$ private void clearActiveContext() {
    //$$     if (activeRecord != null && UndoContext.current() == activeRecord) UndoContext.pop(activeRecord);
    //$$ }
    //$$ private static void disposeTransientRecord(UndoRecord record) {
    //$$     if (record != null) UndoRuntimeState.disposeRecord(record);
    //$$ }
    //#endif
}
