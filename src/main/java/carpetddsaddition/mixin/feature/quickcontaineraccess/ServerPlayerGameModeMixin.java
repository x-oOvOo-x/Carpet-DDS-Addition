/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import net.minecraft.server.level.ServerPlayerGameMode;
//#if MC >= 11404 && MC <= 260200
//#if MC < 11600
import net.minecraft.world.entity.player.Player;
//#else
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void dds$quickContainerUseInAir(
            //#if MC < 11600
            Player player,
            //#else
            //$$ ServerPlayer player,
            //#endif
            Level level, ItemStack stack, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (!QuickContainerAccessCompat.tryOpenInHand(player, stack, hand)) return;
        //#if MC >= 11502
        player.swing(hand, true);
        //#else
        //$$ player.swing(hand);
        //#endif
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
    //#endif
}
