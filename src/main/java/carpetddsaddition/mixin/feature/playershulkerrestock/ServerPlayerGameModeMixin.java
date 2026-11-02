/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.playershulkerrestock;

import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#if MC >= 11404 && MC <= 260300
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
//#endif

/** Brackets air-use and block-use separately; drop and inventory clicks never enter. */
@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    //#if MC >= 11404 && MC <= 260300
    @Unique private PlayerShulkerRestock.Snapshot dds$pendingAir, dds$pendingBlock;

    @Inject(method = "useItem", at = @At("HEAD"))
    private void dds$beginAir(
    //#if MC >= 11601
    //$$         ServerPlayer player,
    //#else
            Player player,
    //#endif
            Level level, ItemStack stack, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> ci) {
        dds$pendingAir = player instanceof ServerPlayer ? PlayerShulkerRestock.capture((ServerPlayer) player, hand) : null;
    }
    @Inject(method = "useItem", at = @At("RETURN"))
    private void dds$endAir(
    //#if MC >= 11601
    //$$         ServerPlayer player,
    //#else
            Player player,
    //#endif
            Level level, ItemStack stack, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> ci) {
    //#if MC >= 12103
    //$$     if (ci.getReturnValue() instanceof InteractionResult.Success result
    //$$             && PlayerShulkerRestock.shouldCompleteImmediately(player.isUsingItem(), result.wasItemInteraction()))
    //#else
        // Count and stack-identity checks still gate every actual transfer.
        if (ci.getReturnValue() != null && ci.getReturnValue() != InteractionResult.PASS
                && ci.getReturnValue() != InteractionResult.FAIL
                && PlayerShulkerRestock.shouldCompleteImmediately(player.isUsingItem(), true))
    //#endif
            PlayerShulkerRestock.completeImmediate(dds$pendingAir);
        dds$pendingAir = null;
    }
    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void dds$beginOn(
    //#if MC >= 11601
    //$$         ServerPlayer player,
    //#else
            Player player,
    //#endif
            Level level, ItemStack stack, InteractionHand hand,
            BlockHitResult hit, CallbackInfoReturnable<InteractionResult> ci) {
        dds$pendingBlock = player instanceof ServerPlayer ? PlayerShulkerRestock.capture((ServerPlayer) player, hand) : null;
    }
    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void dds$endOn(
    //#if MC >= 11601
    //$$         ServerPlayer player,
    //#else
            Player player,
    //#endif
            Level level, ItemStack stack, InteractionHand hand,
            BlockHitResult hit, CallbackInfoReturnable<InteractionResult> ci) {
    //#if MC >= 12103
    //$$     if (ci.getReturnValue() instanceof InteractionResult.Success result
    //$$             && PlayerShulkerRestock.shouldCompleteImmediately(player.isUsingItem(), result.wasItemInteraction()))
    //#else
        // Count and stack-identity checks still gate every actual transfer.
        if (ci.getReturnValue() != null && ci.getReturnValue() != InteractionResult.PASS
                && ci.getReturnValue() != InteractionResult.FAIL
                && PlayerShulkerRestock.shouldCompleteImmediately(player.isUsingItem(), true))
    //#endif
            PlayerShulkerRestock.completeImmediate(dds$pendingBlock);
        dds$pendingBlock = null;
    }
    //#endif
}
