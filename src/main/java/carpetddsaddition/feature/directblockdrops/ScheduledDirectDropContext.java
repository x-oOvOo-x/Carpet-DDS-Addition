/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import java.util.ArrayDeque;
import java.util.Deque;

public final class ScheduledDirectDropContext {
    /* Contexts exist only while attributed scheduled ticks/destroyBlock calls execute. */
    private static final ThreadLocal<Deque<Entry>> TICKS = new ThreadLocal<>();
    /* TRUE means the nested destroy invocation opened a DirectDropContext. */
    private static final ThreadLocal<Deque<Boolean>> DESTROYS = new ThreadLocal<>();
    private ScheduledDirectDropContext() {}

    public static void beginTick(ServerLevel level, BlockPos pos, Block block, ServerPlayer player) {
        if (level == null || pos == null || block == null || player == null) return;
        Deque<Entry> stack = TICKS.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            TICKS.set(stack);
        }
        stack.push(new Entry(level, pos.immutable(), block, player));
    }

    public static void endTick(ServerLevel level, BlockPos pos, Block block) {
        Deque<Entry> stack = TICKS.get();
        if (stack == null || stack.isEmpty()) return;
        Entry entry = stack.peek();
        if (entry.level != level || entry.block != block || !entry.pos.equals(pos)) return;
        stack.pop();
        if (stack.isEmpty()) TICKS.remove();
    }

    /** Only an attributed scheduled tick destroying its own position becomes a direct-drop operation. */
    public static void beginDestroy(Level level, BlockPos pos) {
        Deque<Entry> ticks = TICKS.get();
        if (ticks == null || ticks.isEmpty()) return;
        Entry entry = ticks.peek();
        boolean matched = entry.level == level && entry.pos.equals(pos);
        if (matched) DirectDropContext.push(entry.player);
        Deque<Boolean> destroys = DESTROYS.get();
        if (destroys == null) {
            destroys = new ArrayDeque<>();
            DESTROYS.set(destroys);
        }
        destroys.push(Boolean.valueOf(matched));
    }

    public static void endDestroy() {
        Deque<Boolean> destroys = DESTROYS.get();
        if (destroys == null || destroys.isEmpty()) return;
        if (destroys.pop().booleanValue()) DirectDropContext.pop();
        if (destroys.isEmpty()) DESTROYS.remove();
    }

    public static void clear() {
        TICKS.remove();
        DESTROYS.remove();
    }

    private static final class Entry {
        private final ServerLevel level;
        private final BlockPos pos;
        private final Block block;
        private final ServerPlayer player;
        private Entry(ServerLevel level, BlockPos pos, Block block, ServerPlayer player) {
            this.level = level;
            this.pos = pos;
            this.block = block;
            this.player = player;
        }
    }
}
