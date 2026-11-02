/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/** Legacy scheduler adapter (modern 1.21.9+ ticks carry owner on the accepted object). */
public final class PendingDirectDropTicks {
    private static final long CLEANUP_INTERVAL_TICKS = 20L;
    // Owner is a strong session reference: worlds must be released explicitly at server close.
    private static final Map<ServerLevel, WorldEntries> PENDING = new IdentityHashMap<>();

    private PendingDirectDropTicks() {}

    public static void record(ServerLevel level, BlockPos pos, Block block, int delay, ServerPlayer player) {
        if (level == null || pos == null || block == null || player == null) return;
        DirectDropAttribution.Owner owner = DirectDropAttribution.ownerForSchedule(pos, block);
        if (owner == null || owner.player() != player
                || !DirectDropAttribution.valid(player, level, owner.startTick(), owner.generation())) return;
        // On older schedulers HEAD is before deduplication: never claim an existing natural tick.
        if (level.getBlockTicks().hasScheduledTick(pos, block)) return;
        WorldEntries world = PENDING.get(level);
        if (world == null) {
            world = new WorldEntries();
            PENDING.put(level, world);
        }
        long now = level.getGameTime();
        world.cleanupIfDue(now);
        Key key = new Key(pos, block);
        if (world.entries.containsKey(key)) return;
        long expiry = Math.min(now + Math.max(delay, 0) + 20L,
                owner.startTick() + DirectDropAttribution.MAX_CHAIN_TICKS);
        world.entries.put(key, new Entry(player, owner.startTick(), expiry, owner.generation()));
    }

    public static DirectDropAttribution.Owner consumeOwner(ServerLevel level, BlockPos pos, Block block) {
        if (!DirectBlockDrops.enabled() || level == null || pos == null || block == null) return null;
        WorldEntries world = PENDING.get(level);
        if (world == null) return null;
        long now = level.getGameTime();
        world.cleanupIfDue(now);
        Entry entry = world.entries.remove(new Key(pos, block));
        if (world.entries.isEmpty()) PENDING.remove(level);
        if (entry == null || entry.expiresAt < now
                || !DirectDropAttribution.valid(entry.player, level, entry.startTick, entry.generation)) return null;
        return new DirectDropAttribution.Owner(entry.player, entry.startTick, entry.generation);
    }

    public static void cleanup(MinecraftServer server) {
        for (Iterator<Map.Entry<ServerLevel, WorldEntries>> it = PENDING.entrySet().iterator(); it.hasNext();) {
            Map.Entry<ServerLevel, WorldEntries> entry = it.next();
            if (entry.getKey().getServer() != server) continue;
            entry.getValue().cleanupIfDue(entry.getKey().getGameTime());
            if (entry.getValue().entries.isEmpty()) it.remove();
        }
    }

    public static void clearAll() { PENDING.clear(); }

    private static final class WorldEntries {
        private final Map<Key, Entry> entries = new HashMap<>();
        private long nextCleanupTick = Long.MIN_VALUE;

        private void cleanupIfDue(long now) {
            if (now < nextCleanupTick) return;
            nextCleanupTick = now + CLEANUP_INTERVAL_TICKS;
            Iterator<Map.Entry<Key, Entry>> it = entries.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = it.next().getValue();
                if (entry.expiresAt < now
                        || !DirectDropAttribution.valid(entry.player, null, entry.startTick, entry.generation)) it.remove();
            }
        }
    }

    private static final class Entry {
        final ServerPlayer player;
        final long startTick;
        final long expiresAt;
        final long generation;

        Entry(ServerPlayer player, long startTick, long expiresAt, long generation) {
            this.player = player;
            this.startTick = startTick;
            this.expiresAt = expiresAt;
            this.generation = generation;
        }
    }

    private static final class Key {
        private final BlockPos pos;
        private final Block block;
        Key(BlockPos pos, Block block) {
            this.pos = pos.immutable();
            this.block = block;
        }
        @Override public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return block == other.block && pos.equals(other.pos);
        }
        @Override public int hashCode() {
            return 31 * pos.hashCode() + System.identityHashCode(block);
        }
    }
}
