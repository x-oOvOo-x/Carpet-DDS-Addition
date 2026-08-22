/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.norespawnblockexplosion;

import carpetddsaddition.feature.norespawnblockexplosion.NoRespawnBlockExplosion;
import carpetddsaddition.feature.norespawnblockexplosion.compat.NoRespawnBlockExplosionInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
//#if MC < 12005
import net.minecraft.world.InteractionHand;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {
    //#if MC >= 12005
    //$$ @Inject(
    //$$         method = "useWithoutItem",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", ordinal = 0),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$preventBedExplosion(BlockState state, Level level, BlockPos pos, Player player,
    //$$         BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     carpetDDSAddition$tryPreventExplosion(level, player, cir);
    //$$ }
    //#else
    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", ordinal = 0),
            cancellable = true
    )
    private void carpetDDSAddition$preventBedExplosionLegacy(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        carpetDDSAddition$tryPreventExplosion(level, player, cir);
    }
    //#endif

    private static void carpetDDSAddition$tryPreventExplosion(Level level, Player player,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (NoRespawnBlockExplosion.enabled())
            cir.setReturnValue(NoRespawnBlockExplosionInteraction.blocked(level, player));
    }
}
