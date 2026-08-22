/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoHopperAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.HopperBlock;
//$$ import net.minecraft.world.level.block.SculkSensorBlock;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
//$$ import net.minecraft.world.level.chunk.LevelChunk;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(LevelChunk.class)
//$$ public abstract class LevelChunkMixin {
//$$     @Shadow @Final Level level;
//$$
//$$     @Inject(method = "setBlockState", at = @At("HEAD"))
//$$     private void dds$captureBeforeBlockChange(BlockPos pos, BlockState newState, int flags,
//$$                                               CallbackInfoReturnable<BlockState> cir) {
//$$         if (UndoContext.current() == null || UndoManager.isRestoring() || !(level instanceof ServerLevel serverLevel)) return;
//$$         BlockState oldState = level.getBlockState(pos);
//$$         if (oldState == newState || oldState.equals(newState)) return;
//$$         UndoMutationRecorder.recordBlockBefore(serverLevel, pos);
//$$         dds$armUnlockedHopper(serverLevel, pos, oldState, newState);
//$$     }
//$$
//$$     @Inject(method = "setBlockState", at = @At("RETURN"))
//$$     private void dds$captureAfterBlockChange(BlockPos pos, BlockState newState, int flags,
//$$                                              CallbackInfoReturnable<BlockState> cir) {
//$$         if (!(level instanceof ServerLevel serverLevel)) return;
//$$         if (UndoContext.current() != null && !UndoManager.isRestoring()) UndoMutationRecorder.recordBlockAfter(serverLevel, pos);
//$$         dds$releaseFinishedSculkVibration(serverLevel, pos, newState);
//$$     }
//$$
//$$     private void dds$releaseFinishedSculkVibration(ServerLevel level, BlockPos pos, BlockState state) {
//$$         if (!state.hasProperty(SculkSensorBlock.PHASE) || state.getValue(SculkSensorBlock.PHASE) != SculkSensorPhase.INACTIVE) return;
//$$         BlockEntity blockEntity = level.getBlockEntity(pos);
//$$         if (!(blockEntity instanceof SculkSensorBlockEntity) || !(blockEntity instanceof UndoOriginAccess origin)
//$$                 || origin.dds$getUndoOriginId() == 0L) return;
//$$         origin.dds$setUndoOriginId(0L);
//$$         UndoMutationRecorder.recordBlockAfter(level, pos);
//$$     }
//$$
//$$     @Inject(method = "setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("HEAD"))
//$$     private void dds$attachBlockEntityUndoOrigin(BlockEntity blockEntity, CallbackInfo ci) {
//$$         if (!(level instanceof ServerLevel serverLevel) || UndoContext.current() == null || UndoManager.isRestoring()) return;
//$$         if (blockEntity instanceof UndoOriginAccess access && access.dds$getUndoOriginId() == 0L) {
//$$             long recordId = UndoAsyncOrigin.captureOriginId();
//$$             if (recordId == 0L) return;
//$$             access.dds$setUndoOriginId(recordId);
//$$             if (blockEntity instanceof UndoHopperAccess hopperAccess)
//$$                 hopperAccess.dds$setUndoTransferDeadline(serverLevel.getGameTime() + UndoHopperAccess.IDLE_GRACE_TICKS);
//$$         }
//$$     }
//$$
//$$     @Inject(method = "setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", at = @At("RETURN"))
//$$     private void dds$captureAfterBlockEntityInstall(BlockEntity blockEntity, CallbackInfo ci) {
//$$         if (UndoContext.current() != null && !UndoManager.isRestoring() && level instanceof ServerLevel serverLevel)
//$$             UndoMutationRecorder.recordBlockAfter(serverLevel, blockEntity.getBlockPos());
//$$     }
//$$
//$$     private void dds$armUnlockedHopper(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState) {
//$$         if (!oldState.is(Blocks.HOPPER) || !newState.is(Blocks.HOPPER)
//$$                 || oldState.getValue(HopperBlock.ENABLED) || !newState.getValue(HopperBlock.ENABLED)) return;
//$$         BlockEntity blockEntity = level.getBlockEntity(pos);
//$$         if (!(blockEntity instanceof UndoHopperAccess hopperAccess)) return;
//$$         long recordId = UndoAsyncOrigin.captureOriginId();
//$$         if (recordId == 0L) return;
//$$         hopperAccess.dds$setUndoOriginId(recordId);
//$$         hopperAccess.dds$setUndoTransferDeadline(level.getGameTime() + UndoHopperAccess.IDLE_GRACE_TICKS);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoLevelChunkTarget")
public abstract class LevelChunkMixin {}
//#endif
