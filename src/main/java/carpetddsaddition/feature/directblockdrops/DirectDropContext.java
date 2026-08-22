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
