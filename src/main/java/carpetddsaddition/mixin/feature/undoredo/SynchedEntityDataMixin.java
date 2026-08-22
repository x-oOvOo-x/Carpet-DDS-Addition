/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoEntityLifecycle;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.network.syncher.EntityDataAccessor;
//$$ import net.minecraft.network.syncher.SyncedDataHolder;
//$$ import net.minecraft.network.syncher.SynchedEntityData;
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
//$$ @Mixin(SynchedEntityData.class)
//$$ public abstract class SynchedEntityDataMixin {
//$$     @Shadow @Final private SyncedDataHolder entity;
//$$     @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("HEAD"))
//$$     private <T> void dds$captureBeforeTrackedDataChange(EntityDataAccessor<T> accessor, T value,
//$$                                                        boolean force, CallbackInfo ci) {
//$$         if (!(entity instanceof Entity trackedEntity) || UndoContext.current() == null || UndoManager.isRestoring()
//$$                 || UndoEntityLifecycle.isAdding(trackedEntity)) return;
//$$         UndoMutationRecorder.recordEntityBefore(trackedEntity);
//$$     }
//$$     @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("RETURN"))
//$$     private <T> void dds$captureAfterTrackedDataChange(EntityDataAccessor<T> accessor, T value,
//$$                                                       boolean force, CallbackInfo ci) {
//$$         if (!(entity instanceof Entity trackedEntity) || UndoContext.current() == null || UndoManager.isRestoring()
//$$                 || UndoEntityLifecycle.isAdding(trackedEntity)) return;
//$$         UndoMutationRecorder.recordEntityAfter(trackedEntity);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoSynchedEntityDataTarget")
public abstract class SynchedEntityDataMixin {}
//#endif
