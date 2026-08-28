/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.inventoryprofilesnext;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerScreen;
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.ipn.DdsIpnCompat;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ @Pseudo
//$$ @Mixin(targets = "org.anti_ad.mc.ipnext.integration.HintsManagerNG", remap = false)
//$$ public abstract class IpnHintsManagerMixin {
//$$     @Inject(method = "getIgnoredClass", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
//$$     private void dds$allowFakePlayerInventory(Class<?> type, CallbackInfoReturnable<Class<?>> cir) {
//$$         if (type == DdsFakePlayerScreen.class && DdsIpnCompat.level1Ready()) cir.setReturnValue(null);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.IpnHintsManagerTarget")
public abstract class IpnHintsManagerMixin {}
//#endif
