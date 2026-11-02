/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.dynamicrecipes;

import carpetddsaddition.feature.dynamicrecipes.compat.UnifiedRecipeCoordinatorCompat;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12103
//$$ import org.spongepowered.asm.mixin.injection.At;
//#if MC >= 260300
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//$$ import net.minecraft.world.item.crafting.Recipe;
//#endif
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 260300
//$$ import net.minecraft.core.HolderLookup;
//#endif
//#endif

/** Exactly one recipe-load hook, regardless of how many DDS recipe rules exist. */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    //#if MC >= 260300
    //$$ @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
    //$$         target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Lnet/minecraft/core/HolderLookup;)Lnet/minecraft/world/item/crafting/RecipeMap;"), index = 0)
    //$$ private HolderLookup<Recipe<?>> carpetDDSAddition$recipes$beforeCreate(
    //$$         HolderLookup<Recipe<?>> original) {
    //$$     return UnifiedRecipeCoordinatorCompat.prepareLookup(original);
    //$$ }
    //$$ @Inject(method = "<init>", at = @At("TAIL"))
    //$$ private void carpetDDSAddition$recipes$afterConstruct(HolderLookup.Provider registries, CallbackInfo ci) {
    //$$     UnifiedRecipeCoordinatorCompat.onManagerPrepared((RecipeManager) (Object) this, registries);
    //$$ }
    //#elseif MC >= 12103
    //$$ @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    //$$ private void carpetDDSAddition$recipes$afterApply(CallbackInfo ci) {
    //$$     UnifiedRecipeCoordinatorCompat.onManagerPrepared((RecipeManager) (Object) this);
    //$$ }
    //#endif
}
