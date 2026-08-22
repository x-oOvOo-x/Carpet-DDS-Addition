/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

public final class UndoScope implements AutoCloseable {

    //#if MC >= 12109

    //$$ private static final UndoScope NOOP =
    //$$         new UndoScope(
    //$$                 null
    //$$         );
    //$$
    //$$ private UndoRecord record;
    //$$
    //$$ private UndoScope(
    //$$         UndoRecord record
    //$$ ) {
    //$$     this.record =
    //$$             record;
    //$$ }
    //$$
    //$$ static UndoScope noop() {
    //$$     return NOOP;
    //$$ }
    //$$
    //$$ static UndoScope active(
    //$$         UndoRecord record
    //$$ ) {
    //$$     return new UndoScope(
    //$$             record
    //$$     );
    //$$ }
    //$$
    //$$ public boolean isActive() {
    //$$     return record != null;
    //$$ }
    //$$
    //$$ @Override
    //$$ public void close() {
    //$$     UndoRecord current =
    //$$             record;
    //$$
    //$$     if (current == null) {
    //$$         return;
    //$$     }
    //$$
    //$$     record =
    //$$             null;
    //$$
    //$$     UndoContext.pop(
    //$$             current
    //$$     );
    //$$
    //$$     UndoAsyncOrigin.onScopeClosed(
    //$$             current
    //$$     );
    //$$ }

    //#else

    private static final UndoScope NOOP =
            new UndoScope();

    private UndoScope() {
    }

    static UndoScope noop() {
        return NOOP;
    }

    static UndoScope active(
            UndoRecord record
    ) {
        return NOOP;
    }

    public boolean isActive() {
        return false;
    }

    @Override
    public void close() {
    }

    //#endif
}