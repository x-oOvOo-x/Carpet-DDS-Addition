/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.sneaktoggleirontrapdoor;

import carpetddsaddition.feature.sneaktoggleirontrapdoor.compat.SneakToggleIronTrapdoorCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
//#if MC < 12005
import net.minecraft.world.InteractionHand;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixin {
    @Shadow protected abstract void playSound(Player player, Level level, BlockPos pos, boolean opening);

    //#if MC >= 12005
    //$$ @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$sneakToggleIronTrapdoor(BlockState state, Level level, BlockPos pos, Player player,
    //$$         BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     if (carpetDDSAddition$tryToggle(state, level, pos, player)) cir.setReturnValue(InteractionResult.SUCCESS);
    //$$ }
    //#else
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$sneakToggleIronTrapdoor(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (hand == InteractionHand.MAIN_HAND && carpetDDSAddition$tryToggle(state, level, pos, player))
            cir.setReturnValue(InteractionResult.SUCCESS);
    }
    //#endif

    private boolean carpetDDSAddition$tryToggle(BlockState state, Level level, BlockPos pos, Player player) {
        Boolean opening = SneakToggleIronTrapdoorCompat.tryToggle(this, state, level, pos, player);
        if (opening == null) return false;
        playSound(player, level, pos, opening);
        return true;
    }
}
