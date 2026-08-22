/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.gca;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerMenus;
//$$ import net.minecraft.core.MappedRegistry;
//$$ import net.minecraft.core.Registry;
//#if MC >= 11903
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ @Mixin(MappedRegistry.class)
//$$ public abstract class DdsFakePlayerMenuRegistryMixin {
//$$     @Inject(method = "freeze", at = @At("HEAD"))
//$$     private void dds$registerFakePlayerMenuBeforeFreeze(CallbackInfoReturnable<Registry<?>> cir) {
//#if MC >= 11903
//$$         if ((Object) this != BuiltInRegistries.MENU) return;
//#else
//$$         if ((Object) this != Registry.MENU) return;
//#endif
//$$         DdsFakePlayerMenus.register();
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DdsFakePlayerMenuRegistryTarget")
public abstract class DdsFakePlayerMenuRegistryMixin {}
//#endif
