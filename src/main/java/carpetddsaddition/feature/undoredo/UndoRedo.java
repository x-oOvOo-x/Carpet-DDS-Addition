/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo;

import carpetddsaddition.feature.undoredo.compat.UndoActionRecorder;
import carpetddsaddition.feature.undoredo.compat.UndoAsyncFreeze;
import carpetddsaddition.feature.undoredo.compat.UndoEntityLifecycle;
import carpetddsaddition.feature.undoredo.compat.UndoManager;
import carpetddsaddition.generated.DDSRules;

public final class UndoRedo {

    private UndoRedo() {
    }

    public static boolean enabled() {
        return DDSRules.undoRedo();
    }


    /*
     * ============================================================
     * Rule lifecycle
     * ============================================================
     */

    public static void onRuleChanged() {
        if (enabled()) {
            return;
        }

        resetRuntimeState();
    }


    /*
     * ============================================================
     * Server lifecycle
     * ============================================================
     */

    public static void onServerTickStart() {
        if (!enabled()) {
            return;
        }

        UndoAsyncFreeze.advanceServerTick();
        UndoActionRecorder.abortDanglingActions();
        UndoEntityLifecycle.clear();
    }

    public static void onServerTickEnd() {
        if (!enabled()) {
            return;
        }

        UndoActionRecorder.abortDanglingActions();
        UndoEntityLifecycle.clear();
        UndoAsyncFreeze.flushDeferred();
    }

    public static void onServerStop() {
        resetRuntimeState();
    }


    /*
     * ============================================================
     * Runtime state
     * ============================================================
     */

    public static void resetRuntimeState() {
        UndoManager.clearAll();
        UndoEntityLifecycle.clear();
        UndoAsyncFreeze.clear();
    }
}