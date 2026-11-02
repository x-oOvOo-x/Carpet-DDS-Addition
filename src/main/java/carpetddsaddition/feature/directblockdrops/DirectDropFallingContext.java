/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.compat.DirectDropEntityCompat;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

/** Restricts delayed falling-block collection to that entity's scaffolding drop. */
public final class DirectDropFallingContext {
    private static final ThreadLocal<Deque<Entry>> CURRENT = new ThreadLocal<>();

    private DirectDropFallingContext() {}

    public static boolean begin(FallingBlockEntity entity, ServerPlayer player, ServerLevel level, long startTick, long generation) {
        if (!DirectDropAttribution.valid(player, level, startTick, generation)) return false;
        Deque<Entry> stack = CURRENT.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            CURRENT.set(stack);
        }
        stack.push(new Entry(DirectDropEntityCompat.position(entity), player, level, startTick, generation));
        return true;
    }

    public static Scope open(FallingBlockEntity entity, ServerPlayer player, ServerLevel level,
                             long startTick, long generation) {
        return begin(entity, player, level, startTick, generation)
                ? new Scope(CURRENT.get()) : new Scope(null);
    }

    public static final class Scope implements AutoCloseable {
        private Deque<Entry> opened;
        private Scope(Deque<Entry> opened) { this.opened = opened; }

        @Override public void close() {
            Deque<Entry> stack = opened;
            opened = null;
            if (stack != null && stack == CURRENT.get()) end();
        }
    }

    public static ServerPlayer ownerForItem(ServerLevel level, ItemEntity item) {
        Deque<Entry> stack = CURRENT.get();
        if (stack == null || stack.isEmpty() || item.getItem().getItem() != Items.SCAFFOLDING) return null;
        Entry entry = stack.peek();
        BlockPos at = DirectDropEntityCompat.position(item), from = entry.pos;
        int distance = Math.abs(from.getX() - at.getX()) + Math.abs(from.getY() - at.getY())
                + Math.abs(from.getZ() - at.getZ());
        return entry.level == level && distance <= 2
                && DirectDropAttribution.valid(entry.player, level, entry.startTick, entry.generation)
                ? entry.player : null;
    }

    public static void end() {
        Deque<Entry> stack = CURRENT.get();
        if (stack == null || stack.isEmpty()) return;
        stack.pop();
        if (stack.isEmpty()) CURRENT.remove();
    }

    public static void clear() { CURRENT.remove(); }

    private static final class Entry {
        final BlockPos pos;
        final ServerPlayer player;
        final ServerLevel level;
        final long startTick;
        final long generation;
        Entry(BlockPos pos, ServerPlayer player, ServerLevel level, long startTick, long generation) {
            this.pos = pos.immutable();
            this.player = player;
            this.level = level;
            this.startTick = startTick;
            this.generation = generation;
        }
    }
}
