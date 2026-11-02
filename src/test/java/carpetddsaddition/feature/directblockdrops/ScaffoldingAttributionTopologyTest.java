/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.core.BlockPos;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class ScaffoldingAttributionTopologyTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test void permitsOnlySameOrFaceAdjacentSourcePositions() {
        BlockPos origin = new BlockPos(10, 70, 10);
        assertTrue(ScheduledDirectDropContext.near(origin, origin));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.north()));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.south()));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.east()));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.west()));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.near(origin, origin.below()));
        assertFalse(ScheduledDirectDropContext.near(origin, origin.offset(2, 0, 0)));
        assertFalse(ScheduledDirectDropContext.near(origin, origin.offset(1, 1, 0)));
        assertFalse(ScheduledDirectDropContext.near(origin, new BlockPos(-100, 70, 10)));
        assertFalse(ScheduledDirectDropContext.near(null, origin));
    }

    @Test void stackedPlantChainOnlyAdvancesOneBlockUp() {
        BlockPos origin = new BlockPos(10, 70, 10);
        assertTrue(ScheduledDirectDropContext.directlyAbove(origin, origin.above()));
        assertFalse(ScheduledDirectDropContext.directlyAbove(origin, origin));
        assertFalse(ScheduledDirectDropContext.directlyAbove(origin, origin.below()));
        assertFalse(ScheduledDirectDropContext.directlyAbove(origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.directlyAbove(origin, origin.above(2)));
        assertFalse(ScheduledDirectDropContext.directlyAbove(origin, origin.offset(1, 1, 0)));
        assertFalse(ScheduledDirectDropContext.directlyAbove(null, origin));
    }

    @Test void sugarCaneBambooAndCactusPropagateOnlyStraightUp() {
        BlockPos origin = new BlockPos(10, 70, 10);
        for (net.minecraft.world.level.block.Block plant : new net.minecraft.world.level.block.Block[]{
                Blocks.SUGAR_CANE, Blocks.BAMBOO, Blocks.CACTUS}) {
            assertTrue(ScheduledDirectDropContext.sameChain(plant, plant, origin, origin.above()));
            assertFalse(ScheduledDirectDropContext.sameChain(plant, plant, origin, origin.above(2)));
            assertFalse(ScheduledDirectDropContext.sameChain(plant, plant, origin, origin.east()));
            assertFalse(ScheduledDirectDropContext.sameChain(plant, plant, origin, origin.below()));
        }
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.BAMBOO_SAPLING, Blocks.BAMBOO, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.BAMBOO, Blocks.BAMBOO_SAPLING, origin, origin.above()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.SUGAR_CANE, Blocks.BAMBOO, origin, origin.above()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.STONE, Blocks.STONE, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER, origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER, origin, origin.offset(2, 0, 0)));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.TWISTING_VINES_PLANT, Blocks.TWISTING_VINES, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.KELP_PLANT, Blocks.KELP, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES, origin, origin.below()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, origin, origin.above()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.KELP, Blocks.KELP_PLANT, origin, origin.below()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF, origin, origin.east()));
        assertTrue(ScheduledDirectDropContext.sameChain(
                Blocks.SCAFFOLDING, Blocks.SCAFFOLDING, origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.sameChain(
                Blocks.SCAFFOLDING, Blocks.STONE, origin, origin.east()));
    }

    @Test void oneCropTickCanDropMultipleUpperSegmentsButNotNearbyFarms() {
        BlockPos origin = new BlockPos(10, 70, 10);
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(Blocks.BAMBOO, origin, origin.above(10)));
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(Blocks.SUGAR_CANE, origin, origin.above(2)));
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(Blocks.BAMBOO_SAPLING, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.WEEPING_VINES_PLANT, origin, origin.below(10)));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.WEEPING_VINES_PLANT, origin, origin.above()));
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.KELP_PLANT, origin, origin.above(10)));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.KELP_PLANT, origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(Blocks.BAMBOO, origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(Blocks.BAMBOO, origin, origin.below()));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(Blocks.BAMBOO, origin, origin.above(33)));
        assertTrue(ScheduledDirectDropContext.matchingDropPosition(Blocks.SCAFFOLDING, origin, origin.east()));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(Blocks.SCAFFOLDING, origin, origin.above(2)));
    }

    @Test void scheduledDropMatchesOnlyItsOriginBlock() {
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.SUGAR_CANE, Items.SUGAR_CANE));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.BAMBOO, Items.BAMBOO));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.BAMBOO_SAPLING, Items.BAMBOO));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.CACTUS, Items.CACTUS));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.SCAFFOLDING, Items.SCAFFOLDING));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.WEEPING_VINES_PLANT, Items.WEEPING_VINES));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.TWISTING_VINES_PLANT, Items.TWISTING_VINES));
        assertTrue(ScheduledDirectDropContext.matchingDrop(Blocks.KELP_PLANT, Items.KELP));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.KELP_PLANT, Items.WEEPING_VINES));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.WEEPING_VINES_PLANT, Items.KELP));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.CHORUS_PLANT, Items.SCAFFOLDING));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.BIG_DRIPLEAF, Items.CHORUS_FRUIT));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.STONE, Items.STONE));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.WEEPING_VINES_PLANT, new BlockPos(10, 70, 10), new BlockPos(10, 37, 10)));
        assertFalse(ScheduledDirectDropContext.matchingDropPosition(
                Blocks.WEEPING_VINES_PLANT, new BlockPos(10, 70, 10), new BlockPos(11, 69, 10)));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.BAMBOO, Items.SUGAR_CANE));
        assertFalse(ScheduledDirectDropContext.matchingDrop(Blocks.STONE, Items.BAMBOO));
    }
}
