/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.block.CrafterBlock;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.CrafterBlockEntity;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(CrafterBlock.class)
//$$ public abstract class CrafterBlockMixin {
//$$     @Inject(method = "dispenseFrom", at = @At("HEAD"))
//$$     private void dds$captureCrafterBeforeCraft(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
//$$         if (UndoContext.current() == null || UndoManager.isRestoring()) return;
//$$         UndoMutationRecorder.recordBlockBefore(level, pos);
//$$     }
//$$
//$$     @Inject(method = "dispenseFrom", at = @At("RETURN"))
//$$     private void dds$captureCrafterAfterCraft(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
//$$         if (UndoContext.current() == null || UndoManager.isRestoring()) return;
//$$         BlockEntity blockEntity = level.getBlockEntity(pos);
//$$         BlockState currentState = level.getBlockState(pos);
//$$         if (blockEntity instanceof CrafterBlockEntity && blockEntity instanceof UndoOriginAccess access
//$$                 && currentState.hasProperty(CrafterBlock.CRAFTING) && currentState.getValue(CrafterBlock.CRAFTING)) {
//$$             long recordId = UndoAsyncOrigin.captureOriginId();
//$$             if (recordId != 0L) access.dds$setUndoOriginId(recordId);
//$$         }
//$$         UndoMutationRecorder.recordBlockAfter(level, pos);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCrafterBlockTarget")
public abstract class CrafterBlockMixin {}
//#endif
