/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoPlayerActionCompat;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.level.ServerPlayerGameMode;
//$$ import net.minecraft.world.InteractionHand;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.phys.BlockHitResult;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ServerPlayerGameMode.class)
//$$ public abstract class ServerPlayerGameModeMixin {
//$$     @Shadow @Final protected ServerPlayer player;
//$$     @Inject(method = "destroyBlock", at = @At("HEAD"))
//$$     private void dds$beforeDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//$$         UndoPlayerActionCompat.beforeDestroyBlock(player, pos);
//$$     }
//$$     @Inject(method = "destroyBlock", at = @At("RETURN"))
//$$     private void dds$afterDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
//$$         UndoPlayerActionCompat.afterDestroyBlock(player);
//$$     }
//$$     @Inject(method = "useItemOn", at = @At("HEAD"))
//$$     private void dds$beforeUseItemOn(ServerPlayer serverPlayer, Level level, ItemStack stack, InteractionHand hand,
//$$                                      BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
//$$         UndoPlayerActionCompat.beforeUseItemOn(player, level, hit);
//$$     }
//$$     @Inject(method = "useItemOn", at = @At("RETURN"))
//$$     private void dds$afterUseItemOn(ServerPlayer serverPlayer, Level level, ItemStack stack, InteractionHand hand,
//$$                                     BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
//$$         UndoPlayerActionCompat.afterUseItemOn(player, level, stack, hit);
//$$     }
//$$     @Inject(method = "useItem", at = @At("HEAD"))
//$$     private void dds$beforeUseItem(ServerPlayer serverPlayer, Level level, ItemStack stack, InteractionHand hand,
//$$                                    CallbackInfoReturnable<InteractionResult> cir) {
//$$         UndoPlayerActionCompat.beforeUseItem(player);
//$$     }
//$$     @Inject(method = "useItem", at = @At("RETURN"))
//$$     private void dds$afterUseItem(ServerPlayer serverPlayer, Level level, ItemStack stack, InteractionHand hand,
//$$                                   CallbackInfoReturnable<InteractionResult> cir) {
//$$         UndoPlayerActionCompat.afterUseItem(player);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoPlayerGameModeTarget")
public abstract class ServerPlayerGameModeMixin {}
//#endif