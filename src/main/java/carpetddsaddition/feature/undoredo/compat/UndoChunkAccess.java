/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

public interface UndoChunkAccess {
    //#if MC >= 12109
    //$$ BlockState dds$setBlockStateDirect(
    //$$         BlockPos pos,
    //$$         BlockState state
    //$$ );
    //#endif
}
