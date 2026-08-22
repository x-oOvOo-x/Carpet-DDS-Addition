/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContainerCompat;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.Entity;
    //#if MC >= 12111
    //$$ import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
    //#else
    //$$ import net.minecraft.world.entity.vehicle.MinecartHopper;
    //#endif
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
//$$ @Mixin(MinecartHopper.class)
//$$ public abstract class MinecartHopperMixin {
//$$     @Unique private UndoScope dds$undoSuctionScope;
//$$     @Inject(method = "suckInItems()Z", at = @At("HEAD"))
//$$     private void dds$beforeSuckInItems(CallbackInfoReturnable<Boolean> cir) {
//$$         dds$undoSuctionScope = null;
//$$         if (UndoManager.isRestoring()) return;
//$$         Entity self = (Entity) (Object) this;
//$$         if (UndoContext.current() == null && self instanceof UndoOriginAccess access) {
//$$             long recordId = access.dds$getUndoOriginId();
//$$             if (recordId != 0L && !UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$                 UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$                 if (scope.isActive()) dds$undoSuctionScope = scope;
//$$             }
//$$         }
//$$         if (UndoContext.current() != null) UndoContainerCompat.captureBefore((Container) (Object) this);
//$$     }
//$$
//$$     @Inject(method = "suckInItems()Z", at = @At("RETURN"))
//$$     private void dds$afterSuckInItems(CallbackInfoReturnable<Boolean> cir) {
//$$         try {
//$$             if (UndoContext.current() != null && !UndoManager.isRestoring() && cir.getReturnValue())
//$$                 UndoContainerCompat.captureAfter((Container) (Object) this);
//$$         } finally {
//$$             UndoScope scope = dds$undoSuctionScope;
//$$             dds$undoSuctionScope = null;
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoMinecartHopperTarget")
public abstract class MinecartHopperMixin {}
//#endif
