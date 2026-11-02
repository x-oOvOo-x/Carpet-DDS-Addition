/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectDropAttribution;
import carpetddsaddition.feature.directblockdrops.DirectDropFallingContext;
import carpetddsaddition.feature.directblockdrops.DirectDropFallingOwner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FallingBlockEntity.class)
public abstract class DirectDropFallingBlockEntityMixin implements DirectDropFallingOwner {
    @Unique private ServerPlayer dds$directDropPlayer;
    @Unique private ServerLevel dds$directDropLevel;
    @Unique private long dds$directDropStartTick;
    @Unique private long dds$directDropGeneration;

    @Override public void dds$setDirectDropOwner(ServerPlayer player, ServerLevel level, long startTick, long generation) {
        dds$directDropPlayer = player;
        dds$directDropLevel = level;
        dds$directDropStartTick = startTick;
        dds$directDropGeneration = generation;
    }

    @Override public ServerPlayer dds$getDirectDropPlayer() { return dds$directDropPlayer; }
    @Override public ServerLevel dds$getDirectDropLevel() { return dds$directDropLevel; }
    @Override public long dds$getDirectDropStartTick() { return dds$directDropStartTick; }
    @Override public long dds$getDirectDropGeneration() { return dds$directDropGeneration; }

    @WrapMethod(method = "tick")
    private void dds$scopedFallingScaffoldingDrop(Operation<Void> original) {
        if (!DirectDropAttribution.valid(dds$directDropPlayer, dds$directDropLevel,
                dds$directDropStartTick, dds$directDropGeneration)) {
            dds$setDirectDropOwner(null, null, 0L, 0L);
            original.call();
            return;
        }
        try (DirectDropFallingContext.Scope ignored = DirectDropFallingContext.open(
                (FallingBlockEntity) (Object) this,
                dds$directDropPlayer, dds$directDropLevel, dds$directDropStartTick, dds$directDropGeneration)) {
            original.call();
        }
    }
}
