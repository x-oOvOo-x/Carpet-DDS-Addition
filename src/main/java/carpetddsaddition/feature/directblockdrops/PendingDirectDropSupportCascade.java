/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.compat.DirectDropPlayerCompat;
import carpetddsaddition.feature.directblockdrops.compat.DirectDropVerticalPlantCompat;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compensates for a pre-existing scheduled plant tick that vanilla deduplicates
 * when a player removes its support. Exact positions only, no global tick owner.
 */
public final class PendingDirectDropSupportCascade {
    private static final int MAX_HEIGHT = 32;
    private static final long MAX_WAIT_TICKS = 20L;
    private static final Map<ServerLevel, Map<BlockPos, Entry>> PENDING = new IdentityHashMap<>();

    private PendingDirectDropSupportCascade() {}

    public static void recordBreak(ServerPlayer player, BlockPos brokenPos) {
        if (!DirectBlockDrops.enabled() || player == null || brokenPos == null) return;
        ServerLevel level = DirectDropPlayerCompat.level(player);
        if (!DirectDropAttribution.valid(player, level, -1L)) {
            DirectDropTrace.log("cascade.skip", "invalid direct-break actor at=" + brokenPos);
            return;
        }
        long now = level.getGameTime();
        long generation = DirectDropAttribution.generation();
        recordScaffoldingNeighbors(level, brokenPos, player, now, generation);
        recordUnsupportedChorusNeighbors(level, brokenPos, player, now, generation);
        recordUnsupportedVerticalPlants(level, brokenPos, player, now, generation);
    }

    /** Support-loss chains only: upward plants above, hanging vines below. */
    private static void recordUnsupportedVerticalPlants(ServerLevel level, BlockPos origin,
            ServerPlayer player, long now, long generation) {
        recordVerticalColumn(level, origin, 1, player, now, generation);
        recordVerticalColumn(level, origin, -1, player, now, generation);
    }

    private static void recordVerticalColumn(ServerLevel level, BlockPos origin, int direction,
            ServerPlayer player, long now, long generation) {
        BlockPos first = origin.offset(0, direction, 0);
        BlockState state = level.getBlockState(first);
        Block block = state.getBlock();
        if (verticalDirection(block) != direction) return;
        boolean unsupported = !state.canSurvive(level, first);
        DirectDropTrace.log("cascade.check", "base=" + origin
                + " first=" + block + " direction=" + direction + " unsupported=" + unsupported);
        if (!unsupported) return;
        Map<BlockPos, Entry> entries = PENDING.computeIfAbsent(level, unused -> new HashMap<>());
        int count = 0;
        for (int distance = 1; distance <= MAX_HEIGHT; distance++) {
            BlockPos pos = origin.offset(0, direction * distance, 0);
            if (!samePlant(block, level.getBlockState(pos).getBlock())) break;
            entries.putIfAbsent(pos.immutable(), new Entry(player, block, now, generation));
            count++;
        }
        DirectDropTrace.log("cascade.record", "base=" + origin + " block=" + block
                + " direction=" + direction + " count=" + count);
    }

    /** Recheck unsupported chorus branches after each attributed chorus update. */
    public static void recordAfterTick(ServerLevel level, BlockPos pos, Block block,
            ServerPlayer player, long startTick, long generation) {
        if (!DirectDropAttribution.valid(player, level, startTick, generation)) return;
        if (isChorus(block))
            recordUnsupportedChorusNeighbors(level, pos, player, startTick, generation);
        if (verticalDirection(block) != 0)
            recordUnsupportedVerticalPlants(level, pos, player, startTick, generation);
    }

    /** Only face-adjacent, currently unsupported chorus blocks can inherit the actor. */
    private static void recordUnsupportedChorusNeighbors(ServerLevel level, BlockPos origin,
            ServerPlayer player, long startTick, long generation) {
        BlockPos[] neighbors = {origin.above(), origin.below(), origin.north(),
                origin.south(), origin.east(), origin.west()};
        Map<BlockPos, Entry> entries = null;
        for (BlockPos pos : neighbors) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (!isChorus(block) || state.canSurvive(level, pos)) continue;
            if (entries == null) entries = PENDING.computeIfAbsent(level, unused -> new HashMap<>());
            entries.putIfAbsent(pos.immutable(), new Entry(player, block, startTick, generation));
            DirectDropTrace.log("chorus.seed", "pos=" + pos + " from=" + origin);
        }
    }

    /**
     * Seed only existing face-adjacent scaffolding from a direct break or an
     * already-attributed scaffolding tick. This also covers deduplicated ticks.
     */
    public static void recordScaffoldingNeighbors(ServerLevel level, BlockPos origin,
            ServerPlayer player, long startTick, long generation) {
        if (level == null || origin == null
                || !DirectDropAttribution.valid(player, level, startTick, generation)) return;
        BlockPos[] neighbors = {origin.above(), origin.below(), origin.north(),
                origin.south(), origin.east(), origin.west()};
        Map<BlockPos, Entry> entries = null;
        int recorded = 0;
        for (BlockPos neighbor : neighbors) {
            Block block = level.getBlockState(neighbor).getBlock();
            if (!(block instanceof ScaffoldingBlock)) continue;
            if (entries == null) entries = PENDING.computeIfAbsent(level, unused -> new HashMap<>());
            entries.putIfAbsent(neighbor.immutable(), new Entry(player, block, startTick, generation));
            recorded++;
        }
        if (recorded != 0)
            DirectDropTrace.log("scaffolding.seed", "source=" + origin + " neighbors=" + recorded);
    }

    public static DirectDropAttribution.Owner consume(ServerLevel level, BlockPos pos, Block block) {
        Map<BlockPos, Entry> entries = PENDING.get(level);
        if (entries == null || pos == null) return null;
        Entry entry = entries.remove(pos);
        if (entries.isEmpty()) PENDING.remove(level);
        if (entry == null) return null;
        boolean accepted = sameChainType(entry.block, block)
                && level.getGameTime() >= entry.startTick
                && level.getGameTime() - entry.startTick <= MAX_WAIT_TICKS
                && DirectDropAttribution.valid(entry.player, level, entry.startTick, entry.generation);
        DirectDropTrace.log("cascade.consume", "pos=" + pos + " block=" + block + " owner=" + accepted);
        return accepted ? new DirectDropAttribution.Owner(
                entry.player, entry.startTick, entry.generation) : null;
    }

    static boolean isStackedPlant(Block block) {
        return block == Blocks.SUGAR_CANE || block == Blocks.CACTUS || isBamboo(block);
    }

    static boolean samePlant(Block first, Block next) {
        return first == next && isStackedPlant(first)
                || isBamboo(first) && isBamboo(next)
                || DirectDropVerticalPlantCompat.sameFamily(first, next);
    }

    static int verticalDirection(Block block) {
        if (DirectDropVerticalPlantCompat.isWeeping(block)) return -1;
        return isStackedPlant(block) || DirectDropVerticalPlantCompat.growsUp(block) ? 1 : 0;
    }

    static boolean sameChainType(Block from, Block to) {
        return from instanceof ScaffoldingBlock && to instanceof ScaffoldingBlock
                || isChorus(from) && isChorus(to)
                || samePlant(from, to);
    }

    private static boolean isChorus(Block block) {
        return block == Blocks.CHORUS_PLANT || block == Blocks.CHORUS_FLOWER;
    }

    private static boolean isBamboo(Block block) {
        return block == Blocks.BAMBOO || block == Blocks.BAMBOO_SAPLING;
    }

    public static void cleanup(MinecraftServer server) {
        for (Iterator<Map.Entry<ServerLevel, Map<BlockPos, Entry>>> it =
                     PENDING.entrySet().iterator(); it.hasNext();) {
            Map.Entry<ServerLevel, Map<BlockPos, Entry>> world = it.next();
            ServerLevel level = world.getKey();
            if (level.getServer() != server) continue;
            long now = level.getGameTime();
            world.getValue().values().removeIf(entry ->
                    now < entry.startTick || now - entry.startTick > MAX_WAIT_TICKS
                            || !DirectDropAttribution.valid(entry.player, level,
                                    entry.startTick, entry.generation));
            if (world.getValue().isEmpty()) it.remove();
        }
    }

    public static void clearAll() { PENDING.clear(); }

    private static final class Entry {
        final ServerPlayer player;
        final Block block;
        final long startTick;
        final long generation;
        Entry(ServerPlayer player, Block block, long startTick, long generation) {
            this.player = player;
            this.block = block;
            this.startTick = startTick;
            this.generation = generation;
        }
    }
}
