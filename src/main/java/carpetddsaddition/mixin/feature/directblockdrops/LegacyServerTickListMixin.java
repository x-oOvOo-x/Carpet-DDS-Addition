/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//#if MC < 11800
import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.directblockdrops.PendingDirectDropTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

//#if MC < 11800
@Pseudo
@Mixin(targets = "net.minecraft.world.level.ServerTickList")
//#else
//$$ @Pseudo
//$$ @Mixin(targets = "carpetddsaddition.disabled.DirectBlockDropsLegacyServerTickListTarget")
//#endif
public abstract class LegacyServerTickListMixin {
    //#if MC < 11800
    @Inject(
            method = "scheduleTick(" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Ljava/lang/Object;" +
                    "I" +
                    "Lnet/minecraft/world/level/TickPriority;" +
                    ")V",
            at = @At("HEAD")
    )
    private void carpetDDSAddition$rememberLegacyDirectDropBlockTick(BlockPos pos, Object type, int delay, TickPriority priority, CallbackInfo ci) {
        ServerPlayer player = DirectDropContext.getPlayer();
        if (player == null || !(type instanceof Block)) return;
        ServerLevel level = player.getLevel();
        PendingDirectDropTicks.record(level, pos, (Block) type, delay, player);
    }
    //#endif
}
