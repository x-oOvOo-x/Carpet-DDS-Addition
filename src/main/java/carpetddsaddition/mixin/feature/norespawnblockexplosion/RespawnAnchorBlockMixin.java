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
//#if MC >= 11601
//$$ import net.minecraft.world.level.block.RespawnAnchorBlock;
//#endif
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11601
//$$ @Mixin(RespawnAnchorBlock.class)
//#else
@Pseudo
@Mixin(targets = "net.minecraft.world.level.block.RespawnAnchorBlock")
//#endif
public abstract class RespawnAnchorBlockMixin {
    //#if MC >= 12111
    //$$ @Inject(
    //$$         method = "useWithoutItem",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$preventRespawnAnchorExplosion12111(BlockState state, Level level, BlockPos pos,
    //$$         Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     carpetDDSAddition$tryPreventExplosion(level, player, cir);
    //$$ }
    //#elseif MC >= 12005
    //$$ @Inject(
    //$$         method = "useWithoutItem",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$preventRespawnAnchorExplosionModern(BlockState state, Level level, BlockPos pos,
    //$$         Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     carpetDDSAddition$tryPreventExplosion(level, player, cir);
    //$$ }
    //#else
    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;explode(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"),
            cancellable = true
    )
    private void carpetDDSAddition$preventRespawnAnchorExplosionLegacy(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        carpetDDSAddition$tryPreventExplosion(level, player, cir);
    }
    //#endif

    private static void carpetDDSAddition$tryPreventExplosion(Level level, Player player,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (NoRespawnBlockExplosion.enabled())
            cir.setReturnValue(NoRespawnBlockExplosionInteraction.blocked(level, player));
    }
}
