/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.playershulkerrestock;

import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    //#if MC >= 11404 && MC <= 260300
    @Unique private PlayerShulkerRestock.Snapshot dds$consumedHand;
    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void dds$beginConsume(CallbackInfo ci) {
        dds$consumedHand = null;
        if (!((Object) this instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) (Object) this;
        // Only the hand actually being used may be classified as consumed.
        if (!player.isUsingItem() || player.getUseItem() != player.getItemInHand(player.getUsedItemHand())) return;
        dds$consumedHand = PlayerShulkerRestock.capture(player, player.getUsedItemHand());
    }
    @Inject(method = "completeUsingItem", at = @At("RETURN"))
    private void dds$endConsume(CallbackInfo ci) {
        PlayerShulkerRestock.completeHeldUse(dds$consumedHand);
        dds$consumedHand = null;
    }
    //#endif
}
