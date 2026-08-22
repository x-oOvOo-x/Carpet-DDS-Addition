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
import net.minecraft.world.level.block.Block;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class PendingDirectDropTicks {
    private static final long CLEANUP_INTERVAL_TICKS = 20L;
    /* Weak world keys never keep a closed integrated-server world alive; entries retain only player UUIDs. */
    private static final Map<ServerLevel, WorldEntries> PENDING = new WeakHashMap<>();
    private PendingDirectDropTicks() {}

    public static void record(ServerLevel level, BlockPos pos, Block block, int delay, ServerPlayer player) {
        if (!DirectBlockDrops.enabled() || level == null || pos == null || block == null || player == null) return;
        WorldEntries world = PENDING.get(level);
        if (world == null) {
            world = new WorldEntries();
            PENDING.put(level, world);
        }
        long now = level.getGameTime();
        world.cleanupIfDue(now);
        // Vanilla deduplicates equal scheduled block ticks, so the first attribution must win as well.
        Key key = new Key(pos, block);
        if (world.entries.containsKey(key)) return;
        world.entries.put(key, new Entry(player.getUUID(), now + Math.max(delay, 0) + 20L));
    }

    public static ServerPlayer consume(ServerLevel level, BlockPos pos, Block block) {
        if (!DirectBlockDrops.enabled() || level == null || pos == null || block == null) return null;
        WorldEntries world = PENDING.get(level);
        if (world == null) return null;
        long now = level.getGameTime();
        world.cleanupIfDue(now);
        Entry entry = world.entries.remove(new Key(pos, block));
        if (world.entries.isEmpty()) PENDING.remove(level);
        return entry == null || entry.expiresAt < now ? null : level.getServer().getPlayerList().getPlayer(entry.playerId);
    }

    public static void clearAll() { PENDING.clear(); }

    private static final class WorldEntries {
        private final Map<Key, Entry> entries = new HashMap<>();
        private long nextCleanupTick = Long.MIN_VALUE;
        private void cleanupIfDue(long now) {
            if (now < nextCleanupTick) return;
            nextCleanupTick = now + CLEANUP_INTERVAL_TICKS;
            Iterator<Map.Entry<Key, Entry>> iterator = entries.entrySet().iterator();
            while (iterator.hasNext()) if (iterator.next().getValue().expiresAt < now) iterator.remove();
        }
    }

    private static final class Entry {
        private final UUID playerId;
        private final long expiresAt;
        private Entry(UUID playerId, long expiresAt) {
            this.playerId = playerId;
            this.expiresAt = expiresAt;
        }
    }

    private static final class Key {
        private final BlockPos pos;
        private final Block block;
        private Key(BlockPos pos, Block block) {
            this.pos = pos.immutable();
            this.block = block;
        }
        @Override public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return block == other.block && pos.equals(other.pos);
        }
        @Override public int hashCode() { return 31 * pos.hashCode() + System.identityHashCode(block); }
    }
}
