/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.easymining;

import carpetddsaddition.feature.easymining.EasyMining;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
//#if MC >= 11600
//$$ import net.minecraft.world.level.block.state.BlockBehaviour;
//#else
import net.minecraft.world.level.block.Block;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11600
//$$ @Mixin(BlockBehaviour.BlockStateBase.class)
//#else
@Mixin(Block.class)
//#endif
public abstract class BlockStateBaseMixin {
    //#if MC >= 11600
    //$$ @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    //$$ private void carpetDDSAddition$easyMining(Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
    //$$     cir.setReturnValue(EasyMining.adjustProgress((BlockState) (Object) this, cir.getReturnValue()));
    //$$ }
    //#else
    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void carpetDDSAddition$easyMining(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EasyMining.adjustProgress(state, cir.getReturnValue()));
    }
    //#endif
}
