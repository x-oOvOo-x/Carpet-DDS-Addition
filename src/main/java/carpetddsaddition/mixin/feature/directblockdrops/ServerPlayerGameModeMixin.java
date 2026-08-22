/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectBlockDrops;
import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void carpetDDSAddition$beginDirectBlockDrops(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (DirectBlockDrops.enabled()) DirectDropContext.push(player);
    }

    // Unconditional: a context opened at HEAD must close even if the rule changes mid-call.
    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void carpetDDSAddition$endDirectBlockDrops(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        DirectDropContext.pop();
    }
}
