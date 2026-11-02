/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.ScheduledDirectDropContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin {
    //#if MC >= 11600
    //$$ @WrapMethod(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z")
    //$$ private boolean dds$scopedScheduledDestroy(BlockPos pos, boolean drop,
    //$$                                           Entity breakingEntity, int maxUpdateDepth,
    //$$                                           Operation<Boolean> original) {
    //$$     try (ScheduledDirectDropContext.DestroyScope ignored =
    //$$                  ScheduledDirectDropContext.openDestroy((Level) (Object) this, pos)) {
    //$$         return original.call(pos, drop, breakingEntity, maxUpdateDepth);
    //$$     }
    //$$ }
    //#elseif MC >= 11500
    @WrapMethod(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z")
    private boolean dds$scopedScheduledDestroy115(BlockPos pos, boolean drop, Entity breakingEntity,
                                                 Operation<Boolean> original) {
        try (ScheduledDirectDropContext.DestroyScope ignored =
                     ScheduledDirectDropContext.openDestroy((Level) (Object) this, pos)) {
            return original.call(pos, drop, breakingEntity);
        }
    }
    //#else
    //$$ @WrapMethod(method = "destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z")
    //$$ private boolean dds$scopedScheduledDestroy114(BlockPos pos, boolean drop,
    //$$                                              Operation<Boolean> original) {
    //$$     try (ScheduledDirectDropContext.DestroyScope ignored =
    //$$                  ScheduledDirectDropContext.openDestroy((Level) (Object) this, pos)) {
    //$$         return original.call(pos, drop);
    //$$     }
    //$$ }
    //#endif
}
