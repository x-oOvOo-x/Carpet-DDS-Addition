/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.stonecuttercutswood;

import carpetddsaddition.feature.stonecuttercutswood.StonecutterCutsWood;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12103
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    //#if MC >= 12103
    //$$ @Inject(method="apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",at=@At("TAIL"))
    //$$ private void carpetDDSAddition$stonecutterCutsWood$afterApply(CallbackInfo ci){StonecutterCutsWood.onRecipeManagerApplied((RecipeManager)(Object)this);}
    //#endif
}
