/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoRedo;

/** Propagates Undo/Redo causal record IDs through deferred work. */
public final class UndoAsyncOrigin {
    private UndoAsyncOrigin() {}

    public static long captureOriginId() {
        //#if MC >= 12109
        //$$ if (!UndoRedo.enabled() || UndoRuntimeState.isRestoring()) return 0L;
        //$$ UndoRecord record = UndoContext.current();
        //$$ if (record == null) return 0L;
        //$$ UndoRuntimeState.markAsyncOrigin(record.id());
        //$$ return record.id();
        //#else
        return 0L;
        //#endif
    }

    public static UndoScope enterRecord(long recordId) {
        //#if MC >= 12109
        //$$ if (recordId == 0L || !UndoRedo.enabled() || UndoRuntimeState.isRestoring()) return UndoScope.noop();
        //$$ UndoRecord record = UndoRuntimeState.record(recordId);
        //$$ if (record == null) return UndoScope.noop();
        //$$ UndoContext.push(record);
        //$$ return UndoScope.active(record);
        //#else
        return UndoScope.noop();
        //#endif
    }

    public static boolean shouldSuppressAsyncOrigin(long recordId) {
        //#if MC >= 12109
        //$$ return UndoRuntimeState.isAsyncOriginSuppressed(recordId);
        //#else
        return false;
        //#endif
    }

    static void onScopeClosed(UndoRecord record) {
        //#if MC >= 12109
        //$$ if (!UndoRuntimeState.isRegistered(record)) return;
        //$$ PlayerUndoHistory history = UndoRuntimeState.findHistory(record.owner());
        //$$ if (history != null) history.trimAfterAsync();
        //#endif
    }
}
