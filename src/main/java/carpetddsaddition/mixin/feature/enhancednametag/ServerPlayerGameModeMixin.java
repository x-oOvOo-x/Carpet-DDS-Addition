/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.enhancednametag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//#if MC >= 11904
//$$ import carpetddsaddition.feature.enhancednametag.compat.EnhancedNameTagInteractionCompat;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.level.ServerPlayerGameMode;
//$$ import net.minecraft.world.InteractionHand;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.phys.BlockHitResult;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

/** Block annotations remain readable/cleanable while rule-off only disables new edits. */
//#if MC >= 11904
//$$ @Mixin(ServerPlayerGameMode.class)
//#else
@Pseudo
@Mixin(targets = "net.minecraft.world.entity.Display")
//#endif
public abstract class ServerPlayerGameModeMixin {
    //#if MC >= 11904
    //$$ @Shadow protected ServerLevel level;
    //$$
    //$$ @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$enhancedNameTagBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     if (EnhancedNameTagInteractionCompat.tryUseOnBlock(player, level, stack, hit.getBlockPos()))
    //$$         cir.setReturnValue(InteractionResult.SUCCESS);
    //$$ }
    //$$
    //$$ @Inject(method = "destroyBlock", at = @At("RETURN"))
    //$$ private void carpetDDSAddition$removeDestroyedBlockAnnotation(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    //$$     EnhancedNameTagInteractionCompat.onBlockDestroyed(level, pos, Boolean.TRUE.equals(cir.getReturnValue()));
    //$$ }
    //#endif
}
