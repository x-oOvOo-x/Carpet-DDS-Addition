/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//#if MC >= 11800
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropContext;
//$$ import carpetddsaddition.feature.directblockdrops.PendingDirectDropTicks;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.ticks.TickPriority;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

//#if MC >= 12103
//$$ import net.minecraft.world.level.ScheduledTickAccess;
//#elseif MC >= 11800
//$$ import net.minecraft.world.level.LevelAccessor;
//#endif

//#if MC >= 12103
//$$ @Mixin(ScheduledTickAccess.class)
//#elseif MC >= 11800
//$$ @Mixin(LevelAccessor.class)
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.mixin.placeholder.DirectBlockDropsScheduledTickPlaceholder")
//#endif
public interface ScheduledTickAccessMixin {
    //#if MC >= 12103
    //$$ @Inject(
    //$$         method = "scheduleTick(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Lnet/minecraft/world/level/block/Block;" +
    //$$                 "I" +
    //$$                 ")V",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$rememberDirectDropBlockTickDefaultPriorityNew(BlockPos pos, Block block, int delay, CallbackInfo ci) {
    //$$     carpetDDSAddition$recordScheduledBlockTick(pos, block, delay);
    //$$ }
    //#endif

    //#if MC >= 11800 && MC < 12103
    //$$ @Inject(
    //$$         method = "scheduleTick(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Lnet/minecraft/world/level/block/Block;" +
    //$$                 "I" +
    //$$                 ")V",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$rememberDirectDropBlockTickDefaultPriorityOld(BlockPos pos, Block block, int delay, CallbackInfo ci) {
    //$$     carpetDDSAddition$recordScheduledBlockTick(pos, block, delay);
    //$$ }
    //#endif

    //#if MC >= 12103
    //$$ @Inject(
    //$$         method = "scheduleTick(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Lnet/minecraft/world/level/block/Block;" +
    //$$                 "I" +
    //$$                 "Lnet/minecraft/world/ticks/TickPriority;" +
    //$$                 ")V",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$rememberDirectDropBlockTickPriorityNew(BlockPos pos, Block block, int delay, TickPriority priority, CallbackInfo ci) {
    //$$     carpetDDSAddition$recordScheduledBlockTick(pos, block, delay);
    //$$ }
    //#endif

    //#if MC >= 11800 && MC < 12103
    //$$ @Inject(
    //$$         method = "scheduleTick(" +
    //$$                 "Lnet/minecraft/core/BlockPos;" +
    //$$                 "Lnet/minecraft/world/level/block/Block;" +
    //$$                 "I" +
    //$$                 "Lnet/minecraft/world/ticks/TickPriority;" +
    //$$                 ")V",
    //$$         at = @At("HEAD")
    //$$ )
    //$$ private void carpetDDSAddition$rememberDirectDropBlockTickPriorityOld(BlockPos pos, Block block, int delay, TickPriority priority, CallbackInfo ci) {
    //$$     carpetDDSAddition$recordScheduledBlockTick(pos, block, delay);
    //$$ }
    //#endif

    //#if MC >= 11800
    //$$ private void carpetDDSAddition$recordScheduledBlockTick(BlockPos pos, Block block, int delay) {
    //$$     ServerPlayer player = DirectDropContext.getPlayer();
    //$$     if (player == null) return;
    //$$     ServerLevel level;
    //#if MC >= 12106
    //$$     level = player.level();
    //#elseif MC >= 12000
    //$$     level = player.serverLevel();
    //#else
    //$$     level = player.getLevel();
    //#endif
    //$$     PendingDirectDropTicks.record(level, pos, block, delay, player);
    //$$ }
    //#endif
}
