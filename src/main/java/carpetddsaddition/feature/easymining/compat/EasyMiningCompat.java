/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.easymining.compat;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class EasyMiningCompat {
    private EasyMiningCompat() {}

    public static boolean isObsidian(BlockState state) {
        return state.getBlock() == Blocks.OBSIDIAN;
    }

    public static boolean isCryingObsidian(BlockState state) {
        //#if MC >= 11600
        //$$ return state.getBlock() == Blocks.CRYING_OBSIDIAN;
        //#else
        return false;
        //#endif
    }

    public static boolean isDeepslate(BlockState state) {
        //#if MC >= 11700
        //$$ return state.getBlock() == Blocks.DEEPSLATE || isDeepslateOre(state);
        //#else
        return false;
        //#endif
    }

    public static boolean isOre(BlockState state) {
        Block block = state.getBlock();
        //#if MC >= 11700
        //$$ if (block == Blocks.COPPER_ORE || isDeepslateOre(state)) return true;
        //#endif
        //#if MC >= 11600
        //$$ if (block == Blocks.NETHER_GOLD_ORE) return true;
        //#endif
        return block == Blocks.COAL_ORE || block == Blocks.IRON_ORE || block == Blocks.GOLD_ORE
                || block == Blocks.REDSTONE_ORE || block == Blocks.EMERALD_ORE
                || block == Blocks.LAPIS_ORE || block == Blocks.DIAMOND_ORE
                || block == Blocks.NETHER_QUARTZ_ORE;
    }

    //#if MC >= 11700
    //$$ private static boolean isDeepslateOre(BlockState state) {
    //$$     Block block = state.getBlock();
    //$$     return block == Blocks.DEEPSLATE_COAL_ORE || block == Blocks.DEEPSLATE_IRON_ORE
    //$$             || block == Blocks.DEEPSLATE_COPPER_ORE || block == Blocks.DEEPSLATE_GOLD_ORE
    //$$             || block == Blocks.DEEPSLATE_REDSTONE_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE
    //$$             || block == Blocks.DEEPSLATE_LAPIS_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE;
    //$$ }
    //#endif
}
