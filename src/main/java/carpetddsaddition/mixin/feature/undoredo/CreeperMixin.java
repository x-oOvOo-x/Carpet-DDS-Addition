/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoCreeperAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.network.syncher.EntityDataAccessor;
//$$ import net.minecraft.world.InteractionHand;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.entity.monster.Creeper;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.Items;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
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
//$$ @Mixin(Creeper.class)
//$$ public abstract class CreeperMixin implements UndoCreeperAccess {
//$$     @Shadow @Final private static EntityDataAccessor<Boolean> DATA_IS_IGNITED;
//$$     @Shadow private int oldSwell, swell;
//$$     @Shadow public abstract int getSwellDir();
//$$     @Shadow public abstract void setSwellDir(int direction);
//$$     @Unique private long dds$undoIgnitionOriginId, dds$pendingIgnitionOriginId;
//$$     @Unique private UndoScope dds$undoExplosionScope;
//$$     @Unique private boolean dds$clientWasIgnited, dds$clientHasBeforeFuseState, dds$clientHasRedoFuseState;
//$$     @Unique private int dds$clientBeforeOldSwell, dds$clientBeforeSwell, dds$clientRedoOldSwell, dds$clientRedoSwell;
//$$
//$$     @Inject(method = "ignite", at = @At("HEAD"))
//$$     private void dds$beforeIgnite(CallbackInfo ci) {
//$$         if (!UndoManager.isRestoring()) dds$pendingIgnitionOriginId = UndoAsyncOrigin.captureOriginId();
//$$     }
//$$     @Inject(method = "ignite", at = @At("RETURN"))
//$$     private void dds$afterIgnite(CallbackInfo ci) {
//$$         if (UndoManager.isRestoring()) return;
//$$         dds$undoIgnitionOriginId = dds$pendingIgnitionOriginId;
//$$         dds$pendingIgnitionOriginId = 0L;
//$$     }
//$$
//$$     @Inject(method = "tick", at = @At("HEAD"))
//$$     private void dds$syncClientFuseAcrossUndoRedo(CallbackInfo ci) {
//$$         Creeper self = (Creeper) (Object) this;
//$$         if (self.level().isClientSide()) dds$syncClientFuse(self);
//$$     }
//$$     @Unique
//$$     private void dds$syncClientFuse(Creeper self) {
//$$         boolean ignited = self.isIgnited();
//$$         if (dds$clientWasIgnited && !ignited) {
//$$             dds$clientRedoOldSwell = oldSwell;
//$$             dds$clientRedoSwell = swell;
//$$             dds$clientHasRedoFuseState = true;
//$$             if (dds$clientHasBeforeFuseState) {
//$$                 oldSwell = dds$clientBeforeOldSwell;
//$$                 swell = dds$clientBeforeSwell;
//$$             } else oldSwell = swell = 0;
//$$         } else if (!dds$clientWasIgnited && ignited && dds$clientHasRedoFuseState) {
//$$             oldSwell = dds$clientRedoOldSwell;
//$$             swell = dds$clientRedoSwell;
//$$         }
//$$         dds$clientWasIgnited = ignited;
//$$     }
//$$
//$$     @Inject(method = "mobInteract", at = @At("HEAD"))
//$$     private void dds$captureClientBeforeNewIgnition(Player player, InteractionHand hand,
//$$                                                     CallbackInfoReturnable<InteractionResult> cir) {
//$$         Creeper self = (Creeper) (Object) this;
//$$         if (!self.level().isClientSide() || !player.getItemInHand(hand).is(Items.FLINT_AND_STEEL)) return;
//$$         dds$clientHasBeforeFuseState = true;
//$$         dds$clientBeforeOldSwell = oldSwell;
//$$         dds$clientBeforeSwell = swell;
//$$         dds$clientHasRedoFuseState = false;
//$$     }
//$$
//$$     @Inject(method = "explodeCreeper", at = @At("HEAD"))
//$$     private void dds$beforeCreeperExplosion(CallbackInfo ci) {
//$$         Creeper self = (Creeper) (Object) this;
//$$         if (!self.isIgnited() || dds$undoIgnitionOriginId == 0L || UndoManager.isRestoring()
//$$                 || UndoContext.current() != null) return;
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(dds$undoIgnitionOriginId);
//$$         if (scope.isActive()) dds$undoExplosionScope = scope;
//$$     }
//$$     @Inject(method = "explodeCreeper", at = @At("RETURN"))
//$$     private void dds$afterCreeperExplosion(CallbackInfo ci) {
//$$         UndoScope scope = dds$undoExplosionScope;
//$$         dds$undoExplosionScope = null;
//$$         dds$undoIgnitionOriginId = 0L;
//$$         if (scope != null) scope.close();
//$$     }
//$$
//$$     @Override public long dds$getUndoIgnitionOriginId() { return dds$undoIgnitionOriginId; }
//$$     @Override public void dds$setUndoIgnitionOriginId(long recordId) { dds$undoIgnitionOriginId = recordId; }
//$$     @Override public boolean dds$isIgnited() { return ((Creeper) (Object) this).isIgnited(); }
//$$     @Override public void dds$setIgnited(boolean ignited) {
//$$         ((Creeper) (Object) this).getEntityData().set(DATA_IS_IGNITED, ignited);
//$$     }
//$$     @Override public int dds$getOldSwell() { return oldSwell; }
//$$     @Override public void dds$setOldSwell(int value) { oldSwell = value; }
//$$     @Override public int dds$getSwell() { return swell; }
//$$     @Override public void dds$setSwell(int value) { swell = value; }
//$$     @Override public int dds$getSwellDir() { return getSwellDir(); }
//$$     @Override public void dds$setSwellDir(int value) { setSwellDir(value); }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCreeperTarget")
public abstract class CreeperMixin {}
//#endif
