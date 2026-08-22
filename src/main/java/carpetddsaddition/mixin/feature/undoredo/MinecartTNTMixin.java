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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoTntMinecartAccess;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.damagesource.DamageSource;
    //#if MC >= 12111
    //$$ import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
    //#else
    //$$ import net.minecraft.world.entity.vehicle.MinecartTNT;
    //#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(MinecartTNT.class)
//$$ public abstract class MinecartTNTMixin implements UndoTntMinecartAccess {
//$$     @Unique private static final byte DDS_ARM_NONE = 0, DDS_ARM_ACTIVATOR = 1, DDS_ARM_SUPPORT = 2;
//$$     @Unique private UndoScope dds$undoTickScope, dds$undoPrimeScope, dds$undoExplosionScope;
//$$     @Unique private byte dds$undoArmKind;
//$$     @Unique private long dds$undoArmDeadline;
//$$     @Unique private boolean dds$undoSawAirborne;
//$$
//$$     @Override public void dds$armUndoFromActivator(long recordId) { dds$armUndo(recordId, DDS_ARM_ACTIVATOR); }
//$$     @Override public void dds$armUndoFromSupport(long recordId) { dds$armUndo(recordId, DDS_ARM_SUPPORT); }
//$$
//$$     @Unique
//$$     private void dds$armUndo(long recordId, byte kind) {
//$$         if (recordId == 0L || UndoManager.isRestoring() || UndoContext.current() == null) return;
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         if (self.isRemoved() || self.isPrimed()) return;
//$$         UndoMutationRecorder.recordEntityBefore(self);
//$$         ((UndoOriginAccess) (Object) self).dds$setUndoOriginId(recordId);
//$$         dds$undoArmKind = kind;
//$$         dds$undoSawAirborne = false;
//$$         dds$undoArmDeadline = self.level() instanceof ServerLevel serverLevel
//$$                 ? serverLevel.getGameTime() + (kind == DDS_ARM_SUPPORT ? 200L : 8L) : 0L;
//$$         UndoMutationRecorder.recordEntityAfter(self);
//$$     }
//$$
//$$     @Inject(method = "tick()V", at = @At("HEAD"))
//$$     private void dds$beforeTntMinecartTick(CallbackInfo ci) {
//$$         dds$undoTickScope = null;
//$$         if (UndoManager.isRestoring()) return;
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         if (!(self.level() instanceof ServerLevel)) return;
//$$         long recordId = ((UndoOriginAccess) (Object) self).dds$getUndoOriginId();
//$$         if (UndoContext.current() == null && recordId != 0L && !UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$             if (scope.isActive()) dds$undoTickScope = scope;
//$$         }
//$$         if (UndoContext.current() != null) UndoMutationRecorder.recordEntityBefore(self);
//$$     }
//$$
//$$     @Inject(method = "tick()V", at = @At("RETURN"))
//$$     private void dds$afterTntMinecartTick(CallbackInfo ci) {
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         try {
//$$             if (!UndoManager.isRestoring() && UndoContext.current() != null && !self.isRemoved()) {
//$$                 dds$finishPendingCause(self);
//$$                 UndoMutationRecorder.recordEntityAfter(self);
//$$             }
//$$         } finally {
//$$             UndoScope scope = dds$undoTickScope;
//$$             dds$undoTickScope = null;
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$
//$$     @Unique
//$$     private void dds$finishPendingCause(MinecartTNT self) {
//$$         if (!(self.level() instanceof ServerLevel serverLevel) || dds$undoArmKind == DDS_ARM_NONE) return;
//$$         if (self.isPrimed()) {
//$$             dds$resetArmState();
//$$             return;
//$$         }
//$$         if (dds$undoArmKind == DDS_ARM_SUPPORT) {
//$$             if (!self.onGround()) dds$undoSawAirborne = true;
//$$             if (dds$undoSawAirborne && self.onGround()) {
//$$                 dds$clearPendingOrigin(self);
//$$                 return;
//$$             }
//$$         }
//$$         if (dds$undoArmDeadline != 0L && serverLevel.getGameTime() > dds$undoArmDeadline)
//$$             dds$clearPendingOrigin(self);
//$$     }
//$$
//$$     @Unique private void dds$clearPendingOrigin(MinecartTNT self) {
//$$         ((UndoOriginAccess) (Object) self).dds$setUndoOriginId(0L);
//$$         dds$resetArmState();
//$$     }
//$$     @Unique private void dds$resetArmState() {
//$$         dds$undoArmKind = DDS_ARM_NONE;
//$$         dds$undoSawAirborne = false;
//$$         dds$undoArmDeadline = 0L;
//$$     }
//$$
//$$     @Inject(method = "primeFuse(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
//$$     private void dds$beforePrimeFuse(DamageSource source, CallbackInfo ci) {
//$$         dds$undoPrimeScope = null;
//$$         if (UndoManager.isRestoring()) return;
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         if (!(self.level() instanceof ServerLevel)) return;
//$$         UndoOriginAccess origin = (UndoOriginAccess) (Object) self;
//$$         long recordId = origin.dds$getUndoOriginId();
//$$         if (UndoContext.current() == null && recordId != 0L && !UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$             if (scope.isActive()) dds$undoPrimeScope = scope;
//$$         }
//$$         if (UndoContext.current() == null) return;
//$$         UndoMutationRecorder.recordEntityBefore(self);
//$$         long currentId = UndoAsyncOrigin.captureOriginId();
//$$         if (currentId != 0L && origin.dds$getUndoOriginId() != currentId) origin.dds$setUndoOriginId(currentId);
//$$     }
//$$
//$$     @Inject(method = "primeFuse(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("RETURN"))
//$$     private void dds$afterPrimeFuse(DamageSource source, CallbackInfo ci) {
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         try {
//$$             if (!UndoManager.isRestoring() && UndoContext.current() != null && !self.isRemoved()) {
//$$                 if (self.isPrimed()) dds$resetArmState();
//$$                 UndoMutationRecorder.recordEntityAfter(self);
//$$             }
//$$         } finally {
//$$             UndoScope scope = dds$undoPrimeScope;
//$$             dds$undoPrimeScope = null;
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$
//$$     @Inject(method = "explode(Lnet/minecraft/world/damagesource/DamageSource;D)V", at = @At("HEAD"))
//$$     private void dds$beforeMinecartExplosion(DamageSource source, double speedSqr, CallbackInfo ci) {
//$$         dds$undoExplosionScope = null;
//$$         if (UndoManager.isRestoring()) return;
//$$         MinecartTNT self = (MinecartTNT) (Object) this;
//$$         long recordId = ((UndoOriginAccess) (Object) self).dds$getUndoOriginId();
//$$         if (UndoContext.current() == null && recordId != 0L && !UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$             if (scope.isActive()) dds$undoExplosionScope = scope;
//$$         }
//$$         if (UndoContext.current() != null) UndoMutationRecorder.recordEntityBefore(self);
//$$     }
//$$
//$$     @Inject(method = "explode(Lnet/minecraft/world/damagesource/DamageSource;D)V", at = @At("RETURN"))
//$$     private void dds$afterMinecartExplosion(DamageSource source, double speedSqr, CallbackInfo ci) {
//$$         UndoScope scope = dds$undoExplosionScope;
//$$         dds$undoExplosionScope = null;
//$$         if (scope != null) scope.close();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoMinecartTntTarget")
public abstract class MinecartTNTMixin {}
//#endif
