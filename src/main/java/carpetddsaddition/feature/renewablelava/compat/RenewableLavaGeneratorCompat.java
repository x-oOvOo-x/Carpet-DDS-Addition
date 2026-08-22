/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.renewablelava.compat;

import carpetddsaddition.feature.renewablecalcite.RenewableCalcite;
import carpetddsaddition.feature.renewabletuff.RenewableTuff;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
//#if MC >= 11700
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.tags.FluidTags;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.Blocks;
//#endif

public final class RenewableLavaGeneratorCompat {
    private RenewableLavaGeneratorCompat() {}
    public static boolean tryGenerate(Level level, BlockPos pos) {
        if (!RenewableCalcite.enabled() && !RenewableTuff.enabled()) return false;
        //#if MC >= 11700
        //$$ if (!level.getFluidState(pos).is(FluidTags.LAVA) || !level.getBlockState(pos.below()).is(Blocks.SMOOTH_BASALT)) return false;
        //$$ if (RenewableCalcite.enabled() && hasAdjacentBlock(level, pos, Blocks.AMETHYST_BLOCK)) return generate(level, pos, Blocks.CALCITE);
        //$$ if (RenewableTuff.enabled() && hasAdjacentBlock(level, pos, Blocks.MAGMA_BLOCK)) return generate(level, pos, Blocks.TUFF);
        //#endif
        return false;
    }
    //#if MC >= 11700
    //$$ private static boolean hasAdjacentBlock(Level level, BlockPos pos, Block block) {
    //$$     for (Direction direction : Direction.values())
    //$$         if (direction != Direction.DOWN && level.getBlockState(pos.relative(direction)).is(block)) return true;
    //$$     return false;
    //$$ }
    //$$ private static boolean generate(Level level, BlockPos pos, Block block) {
    //$$     level.setBlockAndUpdate(pos, block.defaultBlockState());
    //$$     level.levelEvent(1501, pos, 0);
    //$$     return true;
    //$$ }
    //#endif
}
