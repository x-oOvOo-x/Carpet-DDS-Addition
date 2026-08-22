/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.EntityBlock;
//#if MC >= 260102
//$$ import net.minecraft.world.level.block.SpreadingSnowyBlock;
//#else
//$$ import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
//#endif
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.BlockEntityType;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.level.chunk.LevelChunk;
//$$ import net.minecraft.world.level.lighting.LightEngine;
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import net.minecraft.world.level.storage.TagValueOutput;
//$$ import java.util.Objects;
//#endif

public final class BlockSnapshot {
    //#if MC >= 12109
    //$$ private final BlockState state;
    //$$ private final BlockEntityType<?> blockEntityType;
    //$$ private final CompoundTag blockEntityData;
    //$$ private final long undoOriginId;
    //$$
    //$$ private BlockSnapshot(BlockState state, BlockEntityType<?> blockEntityType, CompoundTag blockEntityData, long undoOriginId) {
    //$$     this.state = state;
    //$$     this.blockEntityType = blockEntityType;
    //$$     this.blockEntityData = blockEntityData;
    //$$     this.undoOriginId = undoOriginId;
    //$$ }
    //$$
    //$$ public static BlockSnapshot capture(ServerLevel level, BlockPos pos) {
    //$$     BlockState state = level.getBlockState(pos);
    //$$     BlockEntity blockEntity = level.getBlockEntity(pos);
    //$$     return blockEntity == null ? new BlockSnapshot(state, null, null, 0L)
    //$$             : new BlockSnapshot(state, blockEntity.getType(), saveBlockEntityData(level, blockEntity), undoOriginId(blockEntity));
    //$$ }
    //$$ public static long currentUndoOriginId(ServerLevel level, BlockPos pos) { return undoOriginId(level.getBlockEntity(pos)); }
    //$$
    //$$ public boolean matchesCurrent(ServerLevel level, BlockPos pos) {
    //$$     BlockState currentState = level.getBlockState(pos);
    //$$     if (!state.equals(currentState)) return false;
    //$$     BlockEntity current = level.getBlockEntity(pos);
    //$$     if (current == null) return blockEntityType == null && blockEntityData == null && undoOriginId == 0L;
    //$$     if (current.getType() != blockEntityType || undoOriginId(current) != undoOriginId) return false;
    //$$     return Objects.equals(blockEntityData, saveBlockEntityData(level, current));
    //$$ }
    //$$
    //$$ public boolean isOcclusionDegradedToCurrent(ServerLevel level, BlockPos pos) {
    //#if MC >= 260102
    //$$     boolean spreadable = state.getBlock() instanceof SpreadingSnowyBlock;
    //#else
    //$$     boolean spreadable = state.getBlock() instanceof SpreadingSnowyDirtBlock;
    //#endif
    //$$     return spreadable && level.getBlockState(pos).is(Blocks.DIRT);
    //$$ }
    //$$
    //$$ public void restore(ServerLevel level, BlockPos pos) {
    //$$     BlockState currentState = level.getBlockState(pos);
    //$$     BlockEntity current = level.getBlockEntity(pos);
    //$$     boolean sameState = currentState == state || currentState.equals(state);
    //$$     if (sameState && blockEntityData != null && blockEntityType != null && current != null && current.getType() == blockEntityType) {
    //$$         loadBlockEntityData(level, current);
    //$$         restoreUndoOrigin(current);
    //$$         current.setChanged();
    //$$         level.getChunkSource().blockChanged(pos);
    //$$         return;
    //$$     }
    //$$     if (current != null) level.removeBlockEntity(pos);
    //$$     if (!sameState) {
    //$$         LevelChunk chunk = level.getChunkAt(pos);
    //$$         ((UndoChunkAccess) chunk).dds$setBlockStateDirect(pos, state);
    //$$         if (LightEngine.hasDifferentLightProperties(currentState, state)) level.getChunkSource().getLightEngine().checkBlock(pos);
    //$$     }
    //$$     BlockEntity restored = createBlockEntity(level, pos);
    //$$     if (restored != null) level.setBlockEntity(restored);
    //$$     level.getChunkSource().blockChanged(pos);
    //$$     if (!sameState) {
    //$$         level.sendBlockUpdated(pos, currentState, state, Block.UPDATE_CLIENTS);
    //$$         level.updatePOIOnBlockStateChange(pos, currentState, state);
    //$$     }
    //$$ }
    //$$
    //$$ private BlockEntity createBlockEntity(ServerLevel level, BlockPos pos) {
    //$$     if (!state.hasBlockEntity()) return null;
    //$$     BlockEntity blockEntity = blockEntityType == null ? null : blockEntityType.create(pos, state);
    //$$     if (blockEntity == null && state.getBlock() instanceof EntityBlock entityBlock) blockEntity = entityBlock.newBlockEntity(pos, state);
    //$$     if (blockEntity == null) return null;
    //$$     loadBlockEntityData(level, blockEntity);
    //$$     restoreUndoOrigin(blockEntity);
    //$$     return blockEntity;
    //$$ }
    //$$ private static CompoundTag saveBlockEntityData(ServerLevel level, BlockEntity blockEntity) {
    //$$     TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
    //$$     blockEntity.saveWithId(output);
    //$$     return output.buildResult();
    //$$ }
    //$$ private void loadBlockEntityData(ServerLevel level, BlockEntity blockEntity) {
    //$$     if (blockEntityData != null) blockEntity.loadWithComponents(TagValueInput.create(
    //$$             ProblemReporter.DISCARDING, level.registryAccess(), blockEntityData.copy()));
    //$$ }
    //$$ private static long undoOriginId(BlockEntity blockEntity) {
    //$$     return blockEntity instanceof UndoOriginAccess access ? access.dds$getUndoOriginId() : 0L;
    //$$ }
    //$$ private void restoreUndoOrigin(BlockEntity blockEntity) {
    //$$     if (blockEntity instanceof UndoOriginAccess access) access.dds$setUndoOriginId(undoOriginId);
    //$$ }
    //$$ public long estimatedBytes() { return 56L + (blockEntityData == null ? 0L : blockEntityData.sizeInBytes()); }
    //#else
    private BlockSnapshot() {}
    //#endif
}
