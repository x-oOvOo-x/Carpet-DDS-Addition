/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayDeque;
import java.util.Deque;

public final class DirectDropContext {
    private static final ThreadLocal<Deque<ServerPlayer>> PLAYERS = new ThreadLocal<>();
    private DirectDropContext() {}

    public static void push(ServerPlayer player) {
        if (!DirectBlockDrops.enabled() || player == null) return;
        Deque<ServerPlayer> stack = PLAYERS.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            PLAYERS.set(stack);
        }
        stack.push(player);
    }
    /** Closes precisely the stack created at entry, even if a rule reset happened. */
    public static Scope enter(ServerPlayer player) {
        if (!DirectBlockDrops.enabled() || player == null) return new Scope(null);
        push(player);
        DirectDropTrace.log("scope.open", "depth=" + PLAYERS.get().size());
        return new Scope(PLAYERS.get());
    }

    public static final class Scope implements AutoCloseable {
        private Deque<ServerPlayer> stack;
        private Scope(Deque<ServerPlayer> stack) { this.stack = stack; }

        @Override public void close() {
            Deque<ServerPlayer> opened = stack;
            stack = null;
            if (opened != null && PLAYERS.get() == opened) {
                pop();
                DirectDropTrace.log("scope.close", "balanced");
            }
        }
    }

    public static void pop() {
        Deque<ServerPlayer> stack = PLAYERS.get();
        if (stack == null || stack.isEmpty()) return;
        stack.pop();
        if (stack.isEmpty()) PLAYERS.remove();
    }
    public static ServerPlayer getPlayer() {
        if (!DirectBlockDrops.enabled()) return null;
        Deque<ServerPlayer> stack = PLAYERS.get();
        return stack == null || stack.isEmpty() ? null : stack.peek();
    }
    public static void clear() { PLAYERS.remove(); }
}
