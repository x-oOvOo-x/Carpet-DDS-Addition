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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.damagesource.DamageSource;
//$$ import net.minecraft.world.damagesource.DamageTypes;
//$$ import net.minecraft.world.entity.item.ItemEntity;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ItemEntity.class)
//$$ public abstract class ItemEntityMixin {
//$$     @Unique private UndoScope dds$undoFireDamageScope;
//$$     @Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
//$$             at = @At("HEAD"))
//$$     private void dds$captureBeforeFireDamage(ServerLevel level, DamageSource source, float damage,
//$$                                              CallbackInfoReturnable<Boolean> cir) {
//$$         ItemEntity self = (ItemEntity) (Object) this;
//$$         if (!UndoManager.isRestoring() && UndoContext.current() == null
//$$                 && (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE))
//$$                 && self instanceof UndoBurnOriginAccess access && access.dds$getUndoBurnOriginId() != 0L) {
//$$             UndoScope scope = UndoAsyncOrigin.enterRecord(access.dds$getUndoBurnOriginId());
//$$             if (scope.isActive()) dds$undoFireDamageScope = scope;
//$$         }
//$$         UndoMutationRecorder.recordEntityBefore(self);
//$$     }
//$$
//$$     @Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
//$$             at = @At("RETURN"))
//$$     private void dds$captureAfterFireDamage(ServerLevel level, DamageSource source, float damage,
//$$                                             CallbackInfoReturnable<Boolean> cir) {
//$$         ItemEntity self = (ItemEntity) (Object) this;
//$$         try {
//$$             if (cir.getReturnValue() && !self.isRemoved()) UndoMutationRecorder.recordEntityAfter(self);
//$$         } finally {
//$$             UndoScope scope = dds$undoFireDamageScope;
//$$             dds$undoFireDamageScope = null;
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoItemEntityTarget")
public abstract class ItemEntityMixin {}
//#endif
