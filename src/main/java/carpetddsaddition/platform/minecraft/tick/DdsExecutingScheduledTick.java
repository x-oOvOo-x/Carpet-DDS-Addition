/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.tick;

import java.util.ArrayDeque;
import java.util.Deque;

/** Shares the exact vanilla tick object between the scheduler and feature hooks. */
public final class DdsExecutingScheduledTick {
    private static final ThreadLocal<Deque<Object>> CURRENT = new ThreadLocal<>();

    private DdsExecutingScheduledTick() {}

    public static Scope enter(Object tick) {
        if (tick == null) return new Scope(false);
        Deque<Object> stack = CURRENT.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            CURRENT.set(stack);
        }
        stack.push(tick);
        return new Scope(true);
    }

    public static Object current() {
        Deque<Object> stack = CURRENT.get();
        return stack == null || stack.isEmpty() ? null : stack.peek();
    }

    public static final class Scope implements AutoCloseable {
        private boolean active;

        private Scope(boolean active) { this.active = active; }

        @Override public void close() {
            if (!active) return;
            active = false;
            Deque<Object> stack = CURRENT.get();
            if (stack == null || stack.isEmpty()) return;
            stack.pop();
            if (stack.isEmpty()) CURRENT.remove();
        }
    }
}
