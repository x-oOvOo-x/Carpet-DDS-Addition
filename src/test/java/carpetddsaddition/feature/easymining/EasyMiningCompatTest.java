/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.easymining;

import carpetddsaddition.feature.easymining.compat.EasyMiningCompat;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EasyMiningCompatTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void obsidianModesDoNotOverlap() {
        assertTrue(EasyMiningCompat.isObsidian(Blocks.OBSIDIAN.defaultBlockState()));
        assertFalse(EasyMiningCompat.isObsidian(Blocks.CRYING_OBSIDIAN.defaultBlockState()));
        assertTrue(EasyMiningCompat.isCryingObsidian(Blocks.CRYING_OBSIDIAN.defaultBlockState()));
        assertFalse(EasyMiningCompat.isCryingObsidian(Blocks.OBSIDIAN.defaultBlockState()));
    }

    @Test
    void deepslateIncludesDeepOresButExcludesUnrelatedAndBuildingBlocks() {
        assertTrue(EasyMiningCompat.isDeepslate(Blocks.DEEPSLATE.defaultBlockState()));
        for (Block block : new Block[]{Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE_COPPER_ORE}) {
            assertTrue(EasyMiningCompat.isDeepslate(block.defaultBlockState()));
            assertTrue(EasyMiningCompat.isOre(block.defaultBlockState()));
        }
        for (Block block : new Block[]{Blocks.STONE, Blocks.OAK_LOG, Blocks.DIAMOND_ORE,
                Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS}) {
            assertFalse(EasyMiningCompat.isDeepslate(block.defaultBlockState()), block.toString());
        }
    }

    @Test
    void oresUseBlockIdentityInsteadOfSubstringMatching() {
        for (Block block : new Block[]{Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE,
                Blocks.GOLD_ORE, Blocks.REDSTONE_ORE, Blocks.EMERALD_ORE, Blocks.LAPIS_ORE,
                Blocks.DIAMOND_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE}) {
            assertTrue(EasyMiningCompat.isOre(block.defaultBlockState()), block.toString());
        }
        for (Block block : new Block[]{Blocks.SPORE_BLOSSOM, Blocks.ANCIENT_DEBRIS,
                Blocks.RAW_IRON_BLOCK, Blocks.IRON_BLOCK, Blocks.DEEPSLATE, Blocks.BEDROCK}) {
            assertFalse(EasyMiningCompat.isOre(block.defaultBlockState()), block.toString());
        }
    }
}
