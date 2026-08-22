/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class UndoAsyncFreeze {
    private static final Map<Long, FreezeState> STATES = new ConcurrentHashMap<>();
    private static final Set<Long> REDO_ATTEMPTS = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<Long> FORCED_ORIGIN = new ThreadLocal<>();
    private static final ThreadLocal<List<Runnable>> DEFERRED_REQUEUES = new ThreadLocal<>();

    private static long serverTickEpoch;

    private UndoAsyncFreeze() {
    }

    private static final class FreezeState {
        long accumulatedPauseTicks;
        long frozenAtTick;
        boolean frozen;
        final List<Runnable> parked = new ArrayList<>();
    }

    public static void advanceServerTick() {
        serverTickEpoch++;
    }

    public static void freeze(long recordId) {
        if (recordId == 0L) return;

        FreezeState state = STATES.computeIfAbsent(recordId, ignored -> new FreezeState());
        if (state.frozen) return;

        state.frozen = true;
        state.frozenAtTick = serverTickEpoch;
    }

    public static void beginRedoAttempt(long recordId) {
        if (recordId != 0L && STATES.containsKey(recordId)) {
            REDO_ATTEMPTS.add(recordId);
        }
    }

    public static void resume(long recordId) {
        if (recordId == 0L) return;

        REDO_ATTEMPTS.remove(recordId);
        FreezeState state = STATES.get(recordId);
        if (state == null) return;

        if (state.frozen) {
            state.accumulatedPauseTicks += Math.max(0L, serverTickEpoch - state.frozenAtTick);
            state.frozen = false;
        }

        if (!state.parked.isEmpty()) {
            List<Runnable> parked = new ArrayList<>(state.parked);
            state.parked.clear();
            for (Runnable requeue : parked) {
                defer(requeue);
            }
        }
    }

    public static void discard(long recordId) {
        if (recordId != 0L) {
            REDO_ATTEMPTS.remove(recordId);
            STATES.remove(recordId);
        }
    }

    public static boolean isFrozen(long recordId) {
        FreezeState state = STATES.get(recordId);
        return state != null && state.frozen;
    }

    public static long pauseTicks(long recordId) {
        FreezeState state = STATES.get(recordId);
        if (state == null) return 0L;

        long result = state.accumulatedPauseTicks;
        if (state.frozen) {
            result += Math.max(0L, serverTickEpoch - state.frozenAtTick);
        }
        return result;
    }

    public static boolean park(long recordId, Runnable requeueAfterRedo) {
        FreezeState state = STATES.get(recordId);
        if (state == null || !state.frozen) return false;

        state.parked.add(requeueAfterRedo);
        return true;
    }

    public static long forcedOriginId() {
        Long recordId = FORCED_ORIGIN.get();
        return recordId == null ? 0L : recordId;
    }

    public static void withForcedOrigin(long recordId, Runnable action) {
        Long previous = FORCED_ORIGIN.get();
        if (recordId == 0L) {
            FORCED_ORIGIN.remove();
        }
        else {
            FORCED_ORIGIN.set(recordId);
        }

        try {
            action.run();
        }
        finally {
            if (previous == null) {
                FORCED_ORIGIN.remove();
            }
            else {
                FORCED_ORIGIN.set(previous);
            }
        }
    }

    public static void defer(Runnable requeue) {
        List<Runnable> pending = DEFERRED_REQUEUES.get();
        if (pending == null) {
            pending = new ArrayList<>();
            DEFERRED_REQUEUES.set(pending);
        }
        pending.add(requeue);
    }

    public static void flushDeferred() {
        if (!REDO_ATTEMPTS.isEmpty()) {
            for (long recordId : REDO_ATTEMPTS) {
                STATES.remove(recordId);
            }
            REDO_ATTEMPTS.clear();
        }

        List<Runnable> pending = DEFERRED_REQUEUES.get();
        if (pending == null || pending.isEmpty()) {
            DEFERRED_REQUEUES.remove();
            return;
        }

        DEFERRED_REQUEUES.remove();
        for (Runnable requeue : pending) {
            requeue.run();
        }
    }

    public static void clear() {
        STATES.clear();
        REDO_ATTEMPTS.clear();
        FORCED_ORIGIN.remove();
        DEFERRED_REQUEUES.remove();
        serverTickEpoch = 0L;
    }
}
