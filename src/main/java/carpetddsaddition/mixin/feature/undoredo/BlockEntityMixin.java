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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(BlockEntity.class)
//$$ public abstract class BlockEntityMixin {
//$$     @Inject(method = "setChanged()V", at = @At("HEAD"))
//$$     private void dds$trackUndoBlockEntityChange(CallbackInfo ci) {
//$$         if (UndoContext.current() == null || UndoManager.isRestoring()) return;
//$$         BlockEntity self = (BlockEntity) (Object) this;
//$$         if (!(self.getLevel() instanceof ServerLevel serverLevel)) return;
//$$         UndoMutationRecorder.confirmBlockEntityChange(serverLevel, self.getBlockPos());
//$$         UndoMutationRecorder.recordBlockAfter(serverLevel, self.getBlockPos());
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoBlockEntityTarget")
public abstract class BlockEntityMixin {}
//#endif
