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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoSpawnOriginAccess;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.entity.Entity;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ServerLevel.class)
//$$ public abstract class ServerLevelEntityMixin {
//$$     @Inject(method = "addEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"))
//$$     private void dds$beginEntityInsertion(Entity entity, CallbackInfoReturnable<Boolean> cir) {
//$$         if (UndoContext.current() != null && !UndoManager.isRestoring()) UndoEntityLifecycle.beginAdding(entity);
//$$     }
//$$     @Inject(method = "addEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
//$$     private void dds$recordSpawnedEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
//$$         UndoEntityLifecycle.endAdding(entity);
//$$         if (!cir.getReturnValue()) return;
//$$         long recordId = UndoAsyncOrigin.captureOriginId();
//$$         if (recordId == 0L) return;
//$$         if (entity instanceof UndoOriginAccess access && access.dds$getUndoOriginId() == 0L)
//$$             access.dds$setUndoOriginId(recordId);
//$$         if (entity instanceof UndoSpawnOriginAccess access && access.dds$getUndoSpawnOriginId() == 0L)
//$$             access.dds$setUndoSpawnOriginId(recordId);
//$$         UndoMutationRecorder.recordEntitySpawned(entity);
//$$     }
//$$
//$$     @Redirect(method = "tickNonPassenger(Lnet/minecraft/world/entity/Entity;)V",
//$$             at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
//$$     private void dds$tickWithOneShotOrigin(Entity entity) {
//$$         if (!(entity instanceof UndoSpawnOriginAccess access)) {
//$$             entity.tick();
//$$             return;
//$$         }
//$$         long recordId = access.dds$getUndoSpawnOriginId();
//$$         if (recordId == 0L || UndoManager.isRestoring()) {
//$$             entity.tick();
//$$             return;
//$$         }
//$$         access.dds$setUndoSpawnOriginId(0L);
//$$         try (UndoScope ignored = UndoAsyncOrigin.enterRecord(recordId)) {
//$$             UndoMutationRecorder.recordEntityBefore(entity);
//$$             entity.tick();
//$$             UndoMutationRecorder.recordEntityAfter(entity);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerLevelEntityTarget")
public abstract class ServerLevelEntityMixin {}
//#endif
