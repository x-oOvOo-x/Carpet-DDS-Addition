/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.compat.DirectDropVerticalPlantCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.ArrayDeque;
import java.util.Deque;

public final class ScheduledDirectDropContext {
    private static final ThreadLocal<Deque<Entry>> TICKS = new ThreadLocal<>();
    private static final ThreadLocal<Deque<Boolean>> DESTROYS = new ThreadLocal<>();

    private ScheduledDirectDropContext() {}

    public static void beginTick(ServerLevel level, BlockPos pos, Block block, ServerPlayer player, long startTick, long generation) {
        if (level == null || pos == null || block == null) return;
        Deque<Entry> stack = TICKS.get();
        if (stack == null) {
            stack = new ArrayDeque<>();
            TICKS.set(stack);
        }
        stack.push(new Entry(level, pos.immutable(), block, player, startTick, generation));
    }

    public static TickScope openTick(ServerLevel level, BlockPos pos, Block block,
                                      ServerPlayer player, long startTick, long generation) {
        if (level == null || pos == null || block == null) return new TickScope(null);
        // Even an unattributed nested tick must mask its attributed parent.
        boolean accepted = DirectDropAttribution.valid(player, level, startTick, generation);
        DirectDropTrace.log("tick.execute", "block=" + block + " pos=" + pos
                + " candidate=" + (player != null) + " accepted=" + accepted);
        if (!accepted) player = null;
        if (accepted && block instanceof ScaffoldingBlock)
            PendingDirectDropSupportCascade.recordScaffoldingNeighbors(
                    level, pos, player, startTick, generation);
        beginTick(level, pos, block, player, startTick, generation);
        return new TickScope(TICKS.get());
    }

    public static final class TickScope implements AutoCloseable {
        private Deque<Entry> opened;
        private TickScope(Deque<Entry> opened) { this.opened = opened; }

        @Override public void close() {
            Deque<Entry> stack = opened;
            opened = null;
            if (stack == null || stack != TICKS.get() || stack.isEmpty()) return;
            stack.pop();
            if (stack.isEmpty()) TICKS.remove();
        }
    }

    public static boolean hasTick() {
        Deque<Entry> stack = TICKS.get();
        return stack != null && !stack.isEmpty();
    }

    /**
     * Preserve vertical support-loss cascades of the original crops while
     * keeping scaffolding propagation restricted to face-adjacent scaffolding.
     * Never let a scheduled tick inherit authority over an unrelated block.
     */
    public static DirectDropAttribution.Owner ownerForNextTick(BlockPos pos, Block block) {
        Entry entry = current();
        if (entry == null || !sameChain(entry.block, block, entry.pos, pos)
                || !DirectDropAttribution.valid(entry.player, entry.level, entry.startTick, entry.generation)) return null;
        return new DirectDropAttribution.Owner(entry.player, entry.startTick, entry.generation);
    }

    /** Only items matching the attributed tick's original block may be collected. */
    public static ServerPlayer ownerForChainItem(ServerLevel level, BlockPos pos, Item item) {
        Entry entry = current();
        if (entry == null || entry.level != level || !matchingDropPosition(entry.block, entry.pos, pos)
                || !DirectDropAttribution.valid(entry.player, level, entry.startTick, entry.generation)) return null;
        return matchingDrop(entry.block, item) ? entry.player : null;
    }

    static boolean matchingDrop(Block block, Item item) {
        return block instanceof ScaffoldingBlock && item == Items.SCAFFOLDING
                || block == Blocks.SUGAR_CANE && item == Items.SUGAR_CANE
                || isBamboo(block) && item == Items.BAMBOO
                || block == Blocks.CACTUS && item == Items.CACTUS
                || block == Blocks.CHORUS_PLANT && item == Items.CHORUS_FRUIT
                || block == Blocks.CHORUS_FLOWER && item == Items.CHORUS_FLOWER
                || DirectDropVerticalPlantCompat.matchesDrop(block, item);
    }

    /**
     * A single support-loss tick can emit the full vertical crop column.
     * Bound this to the same column; scaffolding keeps stricter adjacency.
     */
    static boolean matchingDropPosition(Block block, BlockPos origin, BlockPos drop) {
        int direction = PendingDirectDropSupportCascade.verticalDirection(block);
        if (direction != 0)
            return origin != null && drop != null && origin.getX() == drop.getX()
                    && origin.getZ() == drop.getZ()
                    && (drop.getY() - origin.getY()) * direction >= 0
                    && (drop.getY() - origin.getY()) * direction <= 32;
        return near(origin, drop);
    }

    static boolean sameChain(Block from, Block to, BlockPos origin, BlockPos next) {
        if (from instanceof ScaffoldingBlock)
            return to instanceof ScaffoldingBlock && near(origin, next);
        if (isChorus(from)) return isChorus(to) && near(origin, next);
        int direction = PendingDirectDropSupportCascade.verticalDirection(from);
        return direction != 0 && PendingDirectDropSupportCascade.samePlant(from, to)
                && origin != null && next != null && origin.getX() == next.getX()
                && origin.getZ() == next.getZ()
                && next.getY() == origin.getY() + direction;
    }

    private static boolean isStackedPlant(Block block) {
        return block == Blocks.SUGAR_CANE || block == Blocks.CACTUS;
    }

    private static boolean isChorus(Block block) {
        return block == Blocks.CHORUS_PLANT || block == Blocks.CHORUS_FLOWER;
    }

    private static boolean isBamboo(Block block) {
        return block == Blocks.BAMBOO || block == Blocks.BAMBOO_SAPLING;
    }

    static boolean directlyAbove(BlockPos origin, BlockPos next) {
        return origin != null && next != null && origin.getX() == next.getX()
                && origin.getZ() == next.getZ() && next.getY() == origin.getY() + 1;
    }

    /** Attribute only a falling scaffolding entity created by this update. */
    public static DirectDropAttribution.Owner ownerForFalling(ServerLevel level, BlockPos pos) {
        Entry entry = current();
        if (entry == null || entry.level != level || !(entry.block instanceof ScaffoldingBlock)
                || !near(entry.pos, pos)
                || !DirectDropAttribution.valid(entry.player, level, entry.startTick, entry.generation)) return null;
        return new DirectDropAttribution.Owner(entry.player, entry.startTick, entry.generation);
    }

    private static Entry current() {
        Deque<Entry> stack = TICKS.get();
        return stack == null || stack.isEmpty() ? null : stack.peek();
    }

    static boolean near(BlockPos a, BlockPos b) {
        return a != null && b != null
                && Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY())
                + Math.abs(a.getZ() - b.getZ()) <= 1;
    }

    public static void endTick(ServerLevel level, BlockPos pos, Block block) {
        Deque<Entry> stack = TICKS.get();
        if (stack == null || stack.isEmpty()) return;
        Entry entry = stack.peek();
        if (entry.level != level || entry.block != block || !entry.pos.equals(pos)) return;
        stack.pop();
        if (stack.isEmpty()) TICKS.remove();
    }

    public static void beginDestroy(Level level, BlockPos pos) {
        Entry entry = current();
        if (entry == null) return;
        boolean matched = entry.level == level && entry.pos.equals(pos)
                && DirectDropAttribution.valid(entry.player, entry.level, entry.startTick, entry.generation);
        if (matched) DirectDropContext.push(entry.player);
        Deque<Boolean> destroys = DESTROYS.get();
        if (destroys == null) {
            destroys = new ArrayDeque<>();
            DESTROYS.set(destroys);
        }
        destroys.push(matched);
    }

    public static DestroyScope openDestroy(Level level, BlockPos pos) {
        if (!hasTick()) return new DestroyScope(null);
        beginDestroy(level, pos);
        return new DestroyScope(DESTROYS.get());
    }

    public static final class DestroyScope implements AutoCloseable {
        private Deque<Boolean> opened;
        private DestroyScope(Deque<Boolean> opened) { this.opened = opened; }

        @Override public void close() {
            Deque<Boolean> stack = opened;
            opened = null;
            if (stack != null && stack == DESTROYS.get()) endDestroy();
        }
    }

    public static void endDestroy() {
        Deque<Boolean> destroys = DESTROYS.get();
        if (destroys == null || destroys.isEmpty()) return;
        if (destroys.pop()) DirectDropContext.pop();
        if (destroys.isEmpty()) DESTROYS.remove();
    }

    public static void clear() {
        TICKS.remove();
        DESTROYS.remove();
    }

    private static final class Entry {
        final ServerLevel level;
        final BlockPos pos;
        final Block block;
        final ServerPlayer player;
        final long startTick;
        final long generation;
        Entry(ServerLevel level, BlockPos pos, Block block, ServerPlayer player, long startTick, long generation) {
            this.level = level;
            this.pos = pos;
            this.block = block;
            this.player = player;
            this.startTick = startTick;
            this.generation = generation;
        }
    }
}
