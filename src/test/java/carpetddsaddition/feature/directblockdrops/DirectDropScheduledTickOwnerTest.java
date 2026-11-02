/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class DirectDropScheduledTickOwnerTest {
    private static final class Tick implements DirectDropScheduledTickOwner {
        private ServerPlayer player;
        private long startTick;
        private long generation;

        @Override public ServerPlayer dds$getDirectDropPlayer() { return player; }
        @Override public long dds$getDirectDropStartTick() { return startTick; }
        @Override public long dds$getDirectDropGeneration() { return generation; }
        @Override public void dds$setDirectDropOwner(ServerPlayer owner, long start, long gen) {
            player = owner;
            startTick = start;
            generation = gen;
        }
    }

    @Test void undoTickReplacementPreservesAttributionMetadata() {
        Tick original = new Tick();
        original.dds$setDirectDropOwner(null, 125L, 9L);
        Tick replacement = new Tick();

        DirectDropScheduledTickOwner.copy(original, replacement);

        assertEquals(125L, replacement.dds$getDirectDropStartTick());
        assertEquals(9L, replacement.dds$getDirectDropGeneration());
        assertEquals(125L, original.dds$getDirectDropStartTick());
        assertEquals(9L, original.dds$getDirectDropGeneration());
    }

    @Test void unrelatedObjectsDoNotOverwriteMetadata() {
        Tick replacement = new Tick();
        replacement.dds$setDirectDropOwner(null, 40L, 3L);

        DirectDropScheduledTickOwner.copy(new Object(), replacement);
        DirectDropScheduledTickOwner.copy(replacement, new Object());

        assertEquals(40L, replacement.dds$getDirectDropStartTick());
        assertEquals(3L, replacement.dds$getDirectDropGeneration());
    }
}
