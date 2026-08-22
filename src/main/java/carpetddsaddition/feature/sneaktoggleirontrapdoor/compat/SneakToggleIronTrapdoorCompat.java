/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.sneaktoggleirontrapdoor.compat;

import carpetddsaddition.feature.sneaktoggleirontrapdoor.SneakToggleIronTrapdoor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/** Feature-local Minecraft boundary for iron trapdoor interaction. */
public final class SneakToggleIronTrapdoorCompat {
    private SneakToggleIronTrapdoorCompat() {}

    /** Returns the new open state, or null when DDS should not handle the interaction. */
    public static Boolean tryToggle(Object block, BlockState state, Level level, BlockPos pos, Player player) {
        if (!SneakToggleIronTrapdoor.enabled() || block != Blocks.IRON_TRAPDOOR
                || !player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()) return null;
        BlockState updated = state.cycle(TrapDoorBlock.OPEN);
        level.setBlock(pos, updated, 2);
        if (updated.getValue(TrapDoorBlock.WATERLOGGED)) {
            //#if MC >= 11800
            //$$ level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            //#else
            level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            //#endif
        }
        return updated.getValue(TrapDoorBlock.OPEN);
    }
}
