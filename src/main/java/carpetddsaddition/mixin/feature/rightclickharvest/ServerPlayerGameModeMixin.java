/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.rightclickharvest;

import carpetddsaddition.feature.rightclickharvest.compat.RightClickHarvestCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void rightClickHarvest(
            //#if MC >= 11601
            //$$ ServerPlayer player,
            //#else
            Player player,
            //#endif
            Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (!RightClickHarvestCompat.tryHarvest(player, level, hitResult.getBlockPos(), stack)) return;
        // Server-side harvest needs an explicit swing; 1.14 only has the one-argument overload.
        //#if MC >= 11500
        player.swing(hand, true);
        //#else
        //$$ player.swing(hand);
        //#endif
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
