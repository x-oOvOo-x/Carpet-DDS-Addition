/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.smeltconcretepowdertoglass;

import carpetddsaddition.feature.smeltconcretepowdertoglass.compat.ConcretePowderToGlassCompat;
//#if MC >= 12103
//$$ import net.minecraft.resources.ResourceKey;
//#else
import net.minecraft.resources.ResourceLocation;
//#endif
//#if MC < 12101
import net.minecraft.world.Container;
//#endif
import net.minecraft.world.item.crafting.Recipe;
//#if MC >= 12002
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//#else
import net.minecraft.world.item.crafting.SmeltingRecipe;
//#endif
//#if MC >= 12101
//$$ import net.minecraft.world.item.crafting.RecipeInput;
//#endif
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;

/** Supplies DDS synthetic concrete-powder recipes only after vanilla lookup fails. */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    //#if MC >= 12101
    //$$ @Inject(
    //$$         method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
    //$$         at = @At("RETURN"), cancellable = true
    //$$ )
    //$$ @SuppressWarnings({"rawtypes", "unchecked"})
    //$$ private void carpetDDSAddition$smeltConcretePowderToGlass$getRecipeFor(RecipeType recipeType, RecipeInput input,
    //$$         Level level, CallbackInfoReturnable<Optional> cir) {
    //$$     Optional vanillaResult = cir.getReturnValue();
    //$$     if (vanillaResult != null && vanillaResult.isPresent()) return;
    //$$     Optional<RecipeHolder<?>> ddsRecipe = ConcretePowderToGlassCompat.findRecipe(recipeType, input);
    //$$     if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    //$$ }
    //#elseif MC >= 12002
    //$$ @Inject(
    //$$         method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
    //$$         at = @At("RETURN"), cancellable = true
    //$$ )
    //$$ @SuppressWarnings({"rawtypes", "unchecked"})
    //$$ private void carpetDDSAddition$smeltConcretePowderToGlass$getRecipeFor(RecipeType recipeType, Container input,
    //$$         Level level, CallbackInfoReturnable<Optional> cir) {
    //$$     Optional vanillaResult = cir.getReturnValue();
    //$$     if (vanillaResult != null && vanillaResult.isPresent()) return;
    //$$     Optional<RecipeHolder<?>> ddsRecipe = ConcretePowderToGlassCompat.findRecipe(recipeType, input);
    //$$     if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    //$$ }
    //#else
    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("RETURN"), cancellable = true
    )
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void carpetDDSAddition$smeltConcretePowderToGlass$getRecipeFor(RecipeType recipeType, Container input,
            Level level, CallbackInfoReturnable<Optional> cir) {
        Optional vanillaResult = cir.getReturnValue();
        if (vanillaResult != null && vanillaResult.isPresent()) return;
        Optional<SmeltingRecipe> ddsRecipe = ConcretePowderToGlassCompat.findRecipe(recipeType, input);
        if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    }
    //#endif

    // ID lookup intentionally remains ungated: furnaces resolve completed recipe IDs later for XP.
    //#if MC >= 12103
    //$$ @Inject(method = "byKey", at = @At("RETURN"), cancellable = true)
    //$$ @SuppressWarnings({"rawtypes", "unchecked"})
    //$$ private void carpetDDSAddition$smeltConcretePowderToGlass$byKey(ResourceKey<Recipe<?>> recipeId,
    //$$         CallbackInfoReturnable<Optional> cir) {
    //$$     Optional vanillaResult = cir.getReturnValue();
    //$$     if (vanillaResult != null && vanillaResult.isPresent()) return;
    //$$     Optional<RecipeHolder<?>> ddsRecipe = ConcretePowderToGlassCompat.findRecipeById(recipeId);
    //$$     if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    //$$ }
    //#elseif MC >= 12002
    //$$ @Inject(method = "byKey", at = @At("RETURN"), cancellable = true)
    //$$ @SuppressWarnings({"rawtypes", "unchecked"})
    //$$ private void carpetDDSAddition$smeltConcretePowderToGlass$byKey(ResourceLocation recipeId,
    //$$         CallbackInfoReturnable<Optional> cir) {
    //$$     Optional vanillaResult = cir.getReturnValue();
    //$$     if (vanillaResult != null && vanillaResult.isPresent()) return;
    //$$     Optional<RecipeHolder<?>> ddsRecipe = ConcretePowderToGlassCompat.findRecipeById(recipeId);
    //$$     if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    //$$ }
    //#else
    @Inject(method = "byKey", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void carpetDDSAddition$smeltConcretePowderToGlass$byKey(ResourceLocation recipeId,
            CallbackInfoReturnable<Optional> cir) {
        Optional vanillaResult = cir.getReturnValue();
        if (vanillaResult != null && vanillaResult.isPresent()) return;
        Optional<SmeltingRecipe> ddsRecipe = ConcretePowderToGlassCompat.findRecipeById(recipeId);
        if (ddsRecipe.isPresent()) cir.setReturnValue((Optional) ddsRecipe);
    }
    //#endif
}
