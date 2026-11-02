/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.directblockdrops.PendingDirectDropSupportCascade;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow protected ServerPlayer player;

    /** RETURN injection is skipped by CCE update suppression and other exceptions. */
    @WrapMethod(method = "destroyBlock")
    private boolean carpetDDSAddition$scopedDirectBlockDrops(BlockPos pos, Operation<Boolean> original) {
        try (DirectDropContext.Scope ignored = DirectDropContext.enter(player)) {
            boolean broken = original.call(pos);
            if (broken) PendingDirectDropSupportCascade.recordBreak(player, pos);
            return broken;
        }
    }
}
