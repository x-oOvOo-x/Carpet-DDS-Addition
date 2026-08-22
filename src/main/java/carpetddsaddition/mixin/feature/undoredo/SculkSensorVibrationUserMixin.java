/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
//$$ import net.minecraft.world.level.gameevent.GameEvent;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
// String target is intentional: VibrationUser is an inaccessible protected nested class.
//$$ @Mixin(targets = "net.minecraft.world.level.block.entity.SculkSensorBlockEntity$VibrationUser")
//$$ public abstract class SculkSensorVibrationUserMixin {
//$$     @Shadow @Final protected BlockPos blockPos;
//$$     @Unique private UndoScope dds$vibrationUndoScope;
//$$     @Unique private long dds$vibrationUndoOriginId;
//$$
//$$     @Inject(method = "onReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;"
//$$             + "Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;"
//$$             + "Lnet/minecraft/world/entity/Entity;F)V", at = @At("HEAD"))
//$$     private void dds$beforeSculkVibrationArrives(ServerLevel level, BlockPos vibrationSourcePos,
//$$                                                  Holder<GameEvent> event, Entity sourceEntity,
//$$                                                  Entity projectileOwner, float distance, CallbackInfo ci) {
//$$         dds$vibrationUndoScope = null;
//$$         dds$vibrationUndoOriginId = 0L;
//$$         if (UndoManager.isRestoring()) return;
//$$         BlockEntity blockEntity = level.getBlockEntity(blockPos);
//$$         if (!(blockEntity instanceof SculkSensorBlockEntity) || !(blockEntity instanceof UndoOriginAccess origin)) return;
//$$         long recordId = origin.dds$getUndoOriginId();
//$$         if (recordId == 0L) return;
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$         if (scope.isActive()) {
//$$             dds$vibrationUndoScope = scope;
//$$             dds$vibrationUndoOriginId = recordId;
//$$         } else origin.dds$setUndoOriginId(0L);
//$$     }
//$$
//$$     @Inject(method = "onReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;"
//$$             + "Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;"
//$$             + "Lnet/minecraft/world/entity/Entity;F)V", at = @At("RETURN"))
//$$     private void dds$afterSculkVibrationArrives(ServerLevel level, BlockPos vibrationSourcePos,
//$$                                                 Holder<GameEvent> event, Entity sourceEntity,
//$$                                                 Entity projectileOwner, float distance, CallbackInfo ci) {
//$$         UndoScope scope = dds$vibrationUndoScope;
//$$         long recordId = dds$vibrationUndoOriginId;
//$$         dds$vibrationUndoScope = null;
//$$         dds$vibrationUndoOriginId = 0L;
//$$         if (scope != null && recordId != 0L) {
//$$             BlockEntity blockEntity = level.getBlockEntity(blockPos);
//$$             if (blockEntity instanceof SculkSensorBlockEntity && blockEntity instanceof UndoOriginAccess origin
//$$                     && origin.dds$getUndoOriginId() == recordId)
//$$                 UndoMutationRecorder.recordBlockAfter(level, blockPos);
//$$         }
//$$         if (scope != null) scope.close();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoSculkSensorVibrationUserTarget")
public abstract class SculkSensorVibrationUserMixin {}
//#endif
