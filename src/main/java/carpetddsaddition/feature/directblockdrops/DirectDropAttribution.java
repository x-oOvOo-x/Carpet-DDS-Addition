/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.compat.DirectDropPlayerCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

/** Session-bound, non-persistent provenance for block update chains. */
public final class DirectDropAttribution {
    // A bounded cascade may span game ticks, but must not survive indefinitely.
    public static final long MAX_CHAIN_TICKS = 1200L;
    private static long generation = 1L;

    private DirectDropAttribution() {}

    public static long generation() { return generation; }
    public static void revokeAll() { generation++; }

    public static Owner ownerForSchedule(BlockPos pos, Block block) {
        Owner scheduled = ScheduledDirectDropContext.ownerForNextTick(pos, block);
        if (ScheduledDirectDropContext.hasTick()) {
            DirectDropTrace.log("tick.schedule", "block=" + block + " pos=" + pos
                    + " nested=true owner=" + (scheduled != null));
            return scheduled;
        }
        ServerPlayer player = DirectDropContext.getPlayer();
        boolean accepted = valid(player, null, -1L);
        if (player != null)
            DirectDropTrace.log("tick.schedule", "block=" + block + " pos=" + pos
                    + " direct=true owner=" + accepted);
        return accepted ? new Owner(player, gameTime(player)) : null;
    }

    public static boolean valid(ServerPlayer player, ServerLevel level, long startTick, long savedGeneration) {
        if (savedGeneration != generation) return false;
        return valid(player, level, startTick);
    }

    public static boolean valid(ServerPlayer player, ServerLevel level, long startTick) {
        if (!DirectBlockDrops.enabled() || player == null) return false;
        if (playerLevel(player).getServer().getPlayerList().getPlayer(player.getUUID()) != player) return false;
        if (level != null && level != playerLevel(player)) return false;
        long now = gameTime(player);
        return startTick < 0L || now >= startTick && now - startTick <= MAX_CHAIN_TICKS;
    }

    public static long gameTime(ServerPlayer player) {
        return playerLevel(player).getGameTime();
    }

    public static ServerLevel playerLevel(ServerPlayer player) {
        return DirectDropPlayerCompat.level(player);
    }

    public static final class Owner {
        private final ServerPlayer player;
        private final long startTick;
        private final long generation;

        public Owner(ServerPlayer player, long startTick) {
            this(player, startTick, DirectDropAttribution.generation());
        }
        public Owner(ServerPlayer player, long startTick, long generation) {
            this.player = player;
            this.startTick = startTick;
            this.generation = generation;
        }
        public ServerPlayer player() { return player; }
        public long startTick() { return startTick; }
        public long generation() { return generation; }
    }
}
