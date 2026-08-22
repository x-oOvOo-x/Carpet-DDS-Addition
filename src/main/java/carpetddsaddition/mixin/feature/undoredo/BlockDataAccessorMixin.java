/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.commands.data.BlockDataAccessor;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(BlockDataAccessor.class)
//$$ public abstract class BlockDataAccessorMixin {
//$$     @Shadow @Final private BlockEntity entity;
//$$     @Shadow @Final private BlockPos pos;
//$$     @Inject(method = "setData", at = @At("HEAD"))
//$$     private void dds$captureBeforeDataCommand(CompoundTag tag, CallbackInfo ci) {
//$$         if (entity.getLevel() instanceof ServerLevel serverLevel)
//$$             UndoMutationRecorder.recordBlockBefore(serverLevel, pos);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoBlockDataAccessorTarget")
public abstract class BlockDataAccessorMixin {}
//#endif
