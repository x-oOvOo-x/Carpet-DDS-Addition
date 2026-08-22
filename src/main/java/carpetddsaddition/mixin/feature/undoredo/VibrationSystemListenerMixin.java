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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoRecord;
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
//$$ import net.minecraft.world.level.gameevent.GameEvent;
//$$ import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
//$$ import net.minecraft.world.phys.Vec3;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(VibrationSystem.Listener.class)
//$$ public abstract class VibrationSystemListenerMixin {
//$$     @Shadow @Final private VibrationSystem system;
//$$     @Unique private long dds$pendingUndoOriginId;
//$$
//$$     @Inject(method = "handleGameEvent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/Holder;"
//$$             + "Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/phys/Vec3;)Z",
//$$             at = @At("HEAD"))
//$$     private void dds$beforeSculkVibrationScheduled(ServerLevel level, Holder<GameEvent> event,
//$$                                                    GameEvent.Context context, Vec3 sourcePos,
//$$                                                    CallbackInfoReturnable<Boolean> cir) {
//$$         dds$pendingUndoOriginId = 0L;
//$$         if (UndoManager.isRestoring() || !(system instanceof SculkSensorBlockEntity sensor) || sensor.getLevel() != level) return;
//$$         UndoRecord record = UndoContext.current();
//$$         if (record == null) return;
//$$         UndoMutationRecorder.recordBlockCandidate(level, sensor.getBlockPos());
//$$         dds$pendingUndoOriginId = record.id();
//$$     }
//$$
//$$     @Inject(method = "handleGameEvent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/Holder;"
//$$             + "Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/phys/Vec3;)Z",
//$$             at = @At("RETURN"))
//$$     private void dds$afterSculkVibrationScheduled(ServerLevel level, Holder<GameEvent> event,
//$$                                                   GameEvent.Context context, Vec3 sourcePos,
//$$                                                   CallbackInfoReturnable<Boolean> cir) {
//$$         if (UndoManager.isRestoring() || !(system instanceof SculkSensorBlockEntity sensor)
//$$                 || sensor.getLevel() != level || !cir.getReturnValue()) {
//$$             dds$pendingUndoOriginId = 0L;
//$$             return;
//$$         }
//$$         UndoOriginAccess origin = (UndoOriginAccess) (Object) sensor;
//$$         long pendingId = dds$pendingUndoOriginId;
//$$         dds$pendingUndoOriginId = 0L;
//$$         if (pendingId == 0L || UndoAsyncOrigin.captureOriginId() != pendingId) {
//$$             origin.dds$setUndoOriginId(0L);
//$$             return;
//$$         }
//$$         origin.dds$setUndoOriginId(pendingId);
//$$         UndoMutationRecorder.confirmBlockEntityChange(level, sensor.getBlockPos());
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoVibrationSystemListenerTarget")
public abstract class VibrationSystemListenerMixin {}
//#endif
