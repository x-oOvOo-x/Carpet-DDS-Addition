/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.commands.data.EntityDataAccessor;
//$$ import net.minecraft.world.entity.Entity;
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
//$$ @Mixin(EntityDataAccessor.class)
//$$ public abstract class EntityDataAccessorMixin {
//$$     @Shadow @Final private Entity entity;
//$$     @Inject(method = "setData", at = @At("HEAD"))
//$$     private void dds$captureBeforeEntityDataCommand(CompoundTag tag, CallbackInfo ci) {
//$$         UndoMutationRecorder.recordEntityBefore(entity);
//$$     }
//$$     @Inject(method = "setData", at = @At("RETURN"))
//$$     private void dds$captureAfterEntityDataCommand(CompoundTag tag, CallbackInfo ci) {
//$$         UndoMutationRecorder.recordEntityAfter(entity);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoEntityDataAccessorTarget")
public abstract class EntityDataAccessorMixin {}
//#endif
