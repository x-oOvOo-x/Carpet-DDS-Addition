/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncFreeze;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.world.entity.item.FallingBlockEntity;
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
//$$ @Mixin(FallingBlockEntity.class)
//$$ public abstract class FallingBlockEntityMixin implements UndoOriginAccess {
//$$     @Unique private long dds$undoOriginId;
//$$     @Unique private UndoScope dds$undoTickScope;
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) { dds$undoOriginId = recordId; }
//$$
//$$     @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
//$$     private void dds$beforeFallingBlockTick(CallbackInfo ci) {
//$$         if (dds$undoOriginId == 0L || UndoManager.isRestoring()) return;
//$$         if (UndoAsyncFreeze.isFrozen(dds$undoOriginId)) {
//$$             ci.cancel();
//$$             return;
//$$         }
//$$         if (UndoContext.current() != null) return;
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(dds$undoOriginId);
//$$         if (scope.isActive()) dds$undoTickScope = scope;
//$$     }
//$$     @Inject(method = "tick", at = @At("RETURN"))
//$$     private void dds$afterFallingBlockTick(CallbackInfo ci) {
//$$         UndoScope scope = dds$undoTickScope;
//$$         dds$undoTickScope = null;
//$$         if (scope != null) scope.close();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoFallingBlockEntityTarget")
public abstract class FallingBlockEntityMixin {}
//#endif
