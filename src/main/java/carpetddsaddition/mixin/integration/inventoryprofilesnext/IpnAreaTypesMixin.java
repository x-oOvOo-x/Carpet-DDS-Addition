/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.inventoryprofilesnext;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.ipn.DdsIpnCompat;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ @Pseudo
//$$ @Mixin(targets = "org.anti_ad.mc.ipnext.inventory.AreaTypes", remap = false)
//$$ public abstract class IpnAreaTypesMixin {
//$$     @Inject(method = "getItemStorage", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
//$$     private void dds$fixedFakeStorage(CallbackInfoReturnable<Object> cir) { cir.setReturnValue(DdsIpnCompat.wrapStorageArea(cir.getReturnValue())); }
//$$
//$$     @Inject(method = "getSortableItemStorage", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
//$$     private void dds$fixedFakeSortArea(CallbackInfoReturnable<Object> cir) { cir.setReturnValue(DdsIpnCompat.wrapSortableArea(cir.getReturnValue())); }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.IpnAreaTypesTarget")
public abstract class IpnAreaTypesMixin {}
//#endif
