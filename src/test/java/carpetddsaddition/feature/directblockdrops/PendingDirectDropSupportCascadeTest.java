/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class PendingDirectDropSupportCascadeTest {
    @BeforeAll static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test void stackedPlantSupportsOnlyCaneCactusAndBothBambooForms() {
        assertTrue(PendingDirectDropSupportCascade.isStackedPlant(Blocks.SUGAR_CANE));
        assertTrue(PendingDirectDropSupportCascade.isStackedPlant(Blocks.CACTUS));
        assertTrue(PendingDirectDropSupportCascade.isStackedPlant(Blocks.BAMBOO));
        assertTrue(PendingDirectDropSupportCascade.isStackedPlant(Blocks.BAMBOO_SAPLING));
        assertFalse(PendingDirectDropSupportCascade.isStackedPlant(Blocks.STONE));
    }

    @Test void scheduledPlantCompensationNeverCrossesSpecies() {
        assertTrue(PendingDirectDropSupportCascade.samePlant(Blocks.SUGAR_CANE, Blocks.SUGAR_CANE));
        assertTrue(PendingDirectDropSupportCascade.samePlant(Blocks.CACTUS, Blocks.CACTUS));
        assertTrue(PendingDirectDropSupportCascade.samePlant(Blocks.BAMBOO, Blocks.BAMBOO_SAPLING));
        assertTrue(PendingDirectDropSupportCascade.samePlant(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO));
        assertFalse(PendingDirectDropSupportCascade.samePlant(Blocks.SUGAR_CANE, Blocks.BAMBOO));
        assertFalse(PendingDirectDropSupportCascade.samePlant(Blocks.STONE, Blocks.STONE));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.SCAFFOLDING, Blocks.SCAFFOLDING));
        assertFalse(PendingDirectDropSupportCascade.sameChainType(
                Blocks.SCAFFOLDING, Blocks.BAMBOO));
        assertFalse(PendingDirectDropSupportCascade.sameChainType(
                Blocks.STONE, Blocks.STONE));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.KELP_PLANT, Blocks.KELP));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT));
        assertTrue(PendingDirectDropSupportCascade.sameChainType(
                Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT));
        assertEquals(-1, PendingDirectDropSupportCascade.verticalDirection(Blocks.WEEPING_VINES));
        assertEquals(-1, PendingDirectDropSupportCascade.verticalDirection(Blocks.WEEPING_VINES_PLANT));
        assertEquals(1, PendingDirectDropSupportCascade.verticalDirection(Blocks.TWISTING_VINES));
        assertEquals(1, PendingDirectDropSupportCascade.verticalDirection(Blocks.KELP_PLANT));
        assertEquals(0, PendingDirectDropSupportCascade.verticalDirection(Blocks.STONE));
        assertFalse(PendingDirectDropSupportCascade.sameChainType(
                Blocks.CHORUS_PLANT, Blocks.SCAFFOLDING));
        assertFalse(PendingDirectDropSupportCascade.sameChainType(
                Blocks.BIG_DRIPLEAF, Blocks.SUGAR_CANE));
    }
}
