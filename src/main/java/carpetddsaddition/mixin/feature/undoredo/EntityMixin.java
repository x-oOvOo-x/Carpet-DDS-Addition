/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoBurnOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoRemovalOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoSpawnOriginAccess;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.MoverType;
//$$ import net.minecraft.world.level.portal.TeleportTransition;
//$$ import net.minecraft.world.phys.Vec3;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(Entity.class)
//$$ public abstract class EntityMixin implements UndoOriginAccess, UndoMutationAccess, UndoRemovalOriginAccess,
//$$         UndoSpawnOriginAccess, UndoBurnOriginAccess {
//$$     @Unique private long dds$undoOriginId, dds$undoMutationId, dds$undoRemovalOriginId,
//$$             dds$undoSpawnOriginId, dds$undoBurnOriginId, dds$undoTeleportOriginId;
//$$     @Unique private UndoScope dds$undoRemovalScope, dds$undoTeleportScope;
//$$     @Unique private boolean dds$undoCrossDimensionTeleport;
//$$
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) { dds$undoOriginId = recordId; }
//$$     @Override public long dds$getUndoMutationId() { return dds$undoMutationId; }
//$$     @Override public void dds$setUndoMutationId(long recordId) { dds$undoMutationId = recordId; }
//$$     @Override public long dds$getUndoRemovalOriginId() { return dds$undoRemovalOriginId; }
//$$     @Override public void dds$setUndoRemovalOriginId(long recordId) { dds$undoRemovalOriginId = recordId; }
//$$     @Override public long dds$getUndoSpawnOriginId() { return dds$undoSpawnOriginId; }
//$$     @Override public void dds$setUndoSpawnOriginId(long recordId) { dds$undoSpawnOriginId = recordId; }
//$$     @Override public long dds$getUndoBurnOriginId() { return dds$undoBurnOriginId; }
//$$     @Override public void dds$setUndoBurnOriginId(long recordId) { dds$undoBurnOriginId = recordId; }
//$$
//$$     @Inject(method = "setRemainingFireTicks(I)V", at = @At("HEAD"))
//$$     private void dds$clearExpiredBurnOrigin(int remainingTicks, CallbackInfo ci) {
//$$         Entity self = (Entity) (Object) this;
//$$         if (dds$undoBurnOriginId != 0L && self.getRemainingFireTicks() > 0 && remainingTicks <= 0)
//$$             dds$undoBurnOriginId = 0L;
//$$     }
//$$
//$$     @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
//$$     private void dds$captureBeforeRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
//$$         Entity self = (Entity) (Object) this;
//$$         if (!UndoManager.isRestoring() && UndoContext.current() == null && dds$undoRemovalOriginId != 0L) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(dds$undoRemovalOriginId);
//$$             if (scope.isActive()) dds$undoRemovalScope = scope;
//$$         }
//$$         UndoMutationRecorder.recordEntityBefore(self);
//$$     }
//$$
//$$     @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("RETURN"))
//$$     private void dds$captureAfterRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
//$$         UndoMutationRecorder.recordEntityRemoved((Entity) (Object) this);
//$$         dds$undoRemovalOriginId = dds$undoSpawnOriginId = dds$undoBurnOriginId = 0L;
//$$         UndoScope scope = dds$undoRemovalScope;
//$$         dds$undoRemovalScope = null;
//$$         if (scope != null) scope.close();
//$$     }
//$$
//$$     @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/Entity;",
//$$             at = @At("HEAD"))
//$$     private void dds$beforeCrossDimensionTeleport(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
//$$         dds$undoTeleportScope = null;
//$$         dds$undoTeleportOriginId = 0L;
//$$         dds$undoCrossDimensionTeleport = false;
//$$         if (UndoManager.isRestoring()) return;
//$$         Entity self = (Entity) (Object) this;
//$$         if (self instanceof ServerPlayer || !(self.level() instanceof ServerLevel sourceLevel)
//$$                 || transition.newLevel().dimension().equals(sourceLevel.dimension())) return;
//$$         dds$undoCrossDimensionTeleport = true;
//$$         UndoOriginAccess originAccess = (UndoOriginAccess) self;
//$$         long recordId = originAccess.dds$getUndoOriginId();
//$$         if (recordId == 0L && UndoContext.current() != null) {
//$$             recordId = UndoAsyncOrigin.captureOriginId();
//$$             if (recordId != 0L) originAccess.dds$setUndoOriginId(recordId);
//$$         }
//$$         dds$undoTeleportOriginId = recordId;
//$$         if (UndoContext.current() == null && recordId != 0L && !UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$             if (scope.isActive()) dds$undoTeleportScope = scope;
//$$         }
//$$         if (UndoContext.current() != null) UndoMutationRecorder.recordEntityBefore(self);
//$$     }
//$$
//$$     @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/Entity;",
//$$             at = @At("RETURN"))
//$$     private void dds$afterCrossDimensionTeleport(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
//$$         try {
//$$             if (!dds$undoCrossDimensionTeleport || UndoManager.isRestoring() || UndoContext.current() == null) return;
//$$             Entity moved = cir.getReturnValue();
//$$             if (moved == null || moved instanceof ServerPlayer) return;
//$$             if (dds$undoTeleportOriginId != 0L && moved instanceof UndoOriginAccess access)
//$$                 access.dds$setUndoOriginId(dds$undoTeleportOriginId);
//$$             UndoMutationRecorder.recordEntityAfter(moved);
//$$         } finally {
//$$             dds$undoCrossDimensionTeleport = false;
//$$             dds$undoTeleportOriginId = 0L;
//$$             UndoScope scope = dds$undoTeleportScope;
//$$             dds$undoTeleportScope = null;
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$
//$$     @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
//$$     private void dds$captureBeforePistonMovement(MoverType moverType, Vec3 movement, CallbackInfo ci) {
//$$         if (moverType == MoverType.PISTON) UndoMutationRecorder.recordEntityBefore((Entity) (Object) this);
//$$     }
//$$     @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", at = @At("RETURN"))
//$$     private void dds$captureAfterPistonMovement(MoverType moverType, Vec3 movement, CallbackInfo ci) {
//$$         if (moverType == MoverType.PISTON) UndoMutationRecorder.recordEntityAfter((Entity) (Object) this);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoEntityTarget")
public abstract class EntityMixin {}
//#endif
