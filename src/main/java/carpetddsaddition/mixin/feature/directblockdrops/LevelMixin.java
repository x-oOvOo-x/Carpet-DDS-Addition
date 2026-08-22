/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.ScheduledDirectDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    //#if MC >= 11600
    //$$ @Inject(
    //$$         method = "destroyBlock(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Z" +
    //$$                 "Lnet/minecraft/world/entity/Entity;" +
    //$$                 "I" +
    //$$                 ")Z",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$beginScheduledDirectDropDestroy(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
    //$$     ScheduledDirectDropContext.beginDestroy((Level) (Object) this, pos);
    //$$ }
    //$$
    //$$ @Inject(
    //$$         method = "destroyBlock(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Z" +
    //$$                 "Lnet/minecraft/world/entity/Entity;" +
    //$$                 "I" +
    //$$                 ")Z",
    //$$         at = @At("RETURN")
    //$$ )
    //$$ private void carpetDDSAddition$endScheduledDirectDropDestroy(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
    //$$     ScheduledDirectDropContext.endDestroy();
    //$$ }
    //#elseif MC >= 11500
    @Inject(
            method = "destroyBlock(" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Z" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    ")Z",
            at = @At("HEAD")
    )
    private void carpetDDSAddition$beginScheduledDirectDropDestroy115(BlockPos pos, boolean drop, Entity breakingEntity, CallbackInfoReturnable<Boolean> cir) {
        ScheduledDirectDropContext.beginDestroy((Level) (Object) this, pos);
    }

    @Inject(
            method = "destroyBlock(" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Z" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    ")Z",
            at = @At("RETURN")
    )
    private void carpetDDSAddition$endScheduledDirectDropDestroy115(BlockPos pos, boolean drop, Entity breakingEntity, CallbackInfoReturnable<Boolean> cir) {
        ScheduledDirectDropContext.endDestroy();
    }
    //#else
    //$$ @Inject(
    //$$         method = "destroyBlock(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Z" +
    //$$                 ")Z",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$beginScheduledDirectDropDestroy114(BlockPos pos, boolean drop, CallbackInfoReturnable<Boolean> cir) {
    //$$     ScheduledDirectDropContext.beginDestroy((Level) (Object) this, pos);
    //$$ }
    //$$
    //$$ @Inject(
    //$$         method = "destroyBlock(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Z" +
    //$$                 ")Z",
    //$$         at = @At("RETURN")
    //$$ )
    //$$ private void carpetDDSAddition$endScheduledDirectDropDestroy114(BlockPos pos, boolean drop, CallbackInfoReturnable<Boolean> cir) {
    //$$     ScheduledDirectDropContext.endDestroy();
    //$$ }
    //#endif
}
