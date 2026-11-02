/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import static org.junit.jupiter.api.Assertions.*;

final class DirectDropExceptionScopeTest {
    @SuppressWarnings("unchecked")
    private static ThreadLocal<Deque<Object>> local(Class<?> owner, String fieldName) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (ThreadLocal<Deque<Object>>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends AutoCloseable> T scope(Class<T> type, Deque<?> opened) throws Exception {
        Constructor<T> constructor = type.getDeclaredConstructor(Deque.class);
        constructor.setAccessible(true);
        return constructor.newInstance(opened);
    }

    @Test void playerScopeClosesOnException() throws Exception {
        ThreadLocal<Deque<Object>> local = local(DirectDropContext.class, "PLAYERS");
        Deque<Object> opened = new ArrayDeque<>();
        opened.push(new Object());
        local.set(opened);
        try {
            assertThrows(IllegalStateException.class, () -> {
                try (DirectDropContext.Scope ignored = scope(DirectDropContext.Scope.class, opened)) {
                    throw new IllegalStateException("CCE update suppression");
                }
            });
            assertNull(local.get());
        } finally {
            local.remove();
        }
    }

    @Test void stalePlayerScopeDoesNotPopNewScopeAfterReset() throws Exception {
        ThreadLocal<Deque<Object>> local = local(DirectDropContext.class, "PLAYERS");
        Deque<Object> previous = new ArrayDeque<>();
        previous.push(new Object());
        local.set(previous);
        try (DirectDropContext.Scope ignored = scope(DirectDropContext.Scope.class, previous)) {
            local.remove();
            Deque<Object> replacement = new ArrayDeque<>();
            replacement.push(new Object());
            local.set(replacement);
        } finally {
            assertNotNull(local.get());
            assertEquals(1, local.get().size());
            local.remove();
        }
    }

    @Test void nestedPlayerScopesUnwindInOrderAfterInnerFailure() throws Exception {
        ThreadLocal<Deque<Object>> local = local(DirectDropContext.class, "PLAYERS");
        Deque<Object> stack = new ArrayDeque<>();
        Object outer = new Object(), inner = new Object();
        stack.push(outer);
        local.set(stack);
        try (DirectDropContext.Scope first = scope(DirectDropContext.Scope.class, stack)) {
            stack.push(inner);
            assertThrows(IllegalStateException.class, () -> {
                try (DirectDropContext.Scope second = scope(DirectDropContext.Scope.class, stack)) {
                    throw new IllegalStateException("nested block update");
                }
            });
            assertEquals(1, stack.size());
            assertSame(outer, stack.peek());
            assertSame(stack, local.get());
        } finally {
            assertNull(local.get());
            local.remove();
        }
    }

    @Test void scheduledTickScopePopsOnlyItsOwnFrame() throws Exception {
        ThreadLocal<Deque<Object>> local = local(ScheduledDirectDropContext.class, "TICKS");
        Deque<Object> opened = new ArrayDeque<>();
        opened.push(new Object());
        local.set(opened);
        try {
            assertThrows(IllegalStateException.class, () -> {
                try (ScheduledDirectDropContext.TickScope ignored =
                             scope(ScheduledDirectDropContext.TickScope.class, opened)) {
                    throw new IllegalStateException("scheduled tick aborted");
                }
            });
            assertNull(local.get());
        } finally {
            local.remove();
        }
    }

    @Test void nestedDestroyScopeClearsFalseFrameOnException() throws Exception {
        ThreadLocal<Deque<Object>> local = local(ScheduledDirectDropContext.class, "DESTROYS");
        Deque<Object> opened = new ArrayDeque<>();
        opened.push(Boolean.FALSE);
        local.set(opened);
        try {
            assertThrows(IllegalStateException.class, () -> {
                try (ScheduledDirectDropContext.DestroyScope ignored =
                             scope(ScheduledDirectDropContext.DestroyScope.class, opened)) {
                    throw new IllegalStateException("nested destroy aborted");
                }
            });
            assertNull(local.get());
        } finally {
            local.remove();
        }
    }
}
