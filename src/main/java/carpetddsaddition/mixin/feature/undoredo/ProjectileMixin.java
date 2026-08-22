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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.world.entity.projectile.Projectile;
//$$ import net.minecraft.world.phys.HitResult;
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
//$$ @Mixin(Projectile.class)
//$$ public abstract class ProjectileMixin implements UndoOriginAccess {
//$$     @Unique private long dds$undoOriginId;
//$$     @Unique private UndoScope dds$undoHitScope;
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) { dds$undoOriginId = recordId; }
//$$
//$$     @Inject(method = "onHit", at = @At("HEAD"))
//$$     private void dds$beforeProjectileHit(HitResult hitResult, CallbackInfo ci) {
//$$         if (dds$undoOriginId == 0L || UndoManager.isRestoring() || UndoContext.current() != null) return;
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(dds$undoOriginId);
//$$         if (scope.isActive()) dds$undoHitScope = scope;
//$$     }
//$$     @Inject(method = "onHit", at = @At("RETURN"))
//$$     private void dds$afterProjectileHit(HitResult hitResult, CallbackInfo ci) {
//$$         UndoScope scope = dds$undoHitScope;
//$$         dds$undoHitScope = null;
//$$         if (scope != null) scope.close();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoProjectileTarget")
public abstract class ProjectileMixin {}
//#endif
