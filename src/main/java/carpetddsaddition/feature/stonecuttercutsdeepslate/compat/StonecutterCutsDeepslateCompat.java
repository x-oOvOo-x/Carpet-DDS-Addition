/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate.compat;

import carpetddsaddition.CarpetDDSAddition;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

//#if MC >= 11700
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//$$ import net.minecraft.world.item.crafting.Ingredient;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//$$ import net.minecraft.world.item.crafting.StonecutterRecipe;
//#endif
//#if MC >= 12002
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//#endif
//#if MC >= 12103
//$$ import carpetddsaddition.mixin.feature.stonecuttercutsdeepslate.RecipeManagerAccessor;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//$$ import net.minecraft.world.item.crafting.SingleRecipeInput;
//#endif
//#if MC >= 11700
//#if MC < 12111
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#endif
//#if MC >= 260000
//$$ import net.minecraft.world.item.ItemStackTemplate;
//#endif

public final class StonecutterCutsDeepslateCompat {
    private static Boolean appliedState;
    private static Object appliedRecipeManager;
    // Identity tracking prevents DDS from removing a datapack/mod recipe that reuses a generated id after /reload.
    private static final Set<Object> GENERATED_ENTRIES = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    private StonecutterCutsDeepslateCompat() {}

    public static void refresh(MinecraftServer server, boolean force) {
        //#if MC >= 11700
        //$$ if (server == null) return;
        //$$ boolean enabled = DDSRules.stonecutterCutsDeepslate();
        //$$ RecipeManager manager = server.getRecipeManager();
        //$$ if (!force && appliedRecipeManager == manager && appliedState != null && appliedState.booleanValue() == enabled) return;
        //$$ rebuild(server, manager, enabled);
        //$$ appliedRecipeManager = manager;
        //$$ appliedState = Boolean.valueOf(enabled);
        //#endif
    }

    public static void reset() {
        appliedState = null;
        appliedRecipeManager = null;
        GENERATED_ENTRIES.clear();
    }

    //#if MC >= 11700
    //$$ private static void rebuild(MinecraftServer server, RecipeManager manager, boolean enabled) {
    //#if MC >= 12002
    //$$     List<RecipeHolder<?>> recipes = new ArrayList<RecipeHolder<?>>(manager.getRecipes());
    //#else
    //$$     List<Recipe<?>> recipes = new ArrayList<Recipe<?>>(manager.getRecipes());
    //#endif
    //$$     recipes.removeIf(GENERATED_ENTRIES::contains);
    //$$     GENERATED_ENTRIES.clear();
    //$$     if (enabled) addMirroredRecipes(server, recipes);
    //#if MC >= 12103
    //$$     ((RecipeManagerAccessor) (Object) manager).carpetDDSAddition$setRecipes(RecipeMap.create(recipes));
    //$$     manager.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    //#else
    //$$     manager.replaceRecipes(recipes);
    //#endif
    //$$     syncPlayers(server);
    //$$ }
    //#endif

    private static void syncPlayers(MinecraftServer server) {
        if (server == null || server.getPlayerList() == null || server.getPlayerList().getPlayers().isEmpty()) return;
        server.getPlayerList().reloadResources();
    }

    //#if MC >= 12002
    //$$ private static void addMirroredRecipes(MinecraftServer server, List<RecipeHolder<?>> recipes) {
    //$$     List<RecipeHolder<?>> originalRecipes = new ArrayList<RecipeHolder<?>>(recipes);
    //$$     Set<Object> usedIds = new HashSet<Object>();
    //$$     for (RecipeHolder<?> holder : recipes) usedIds.add(holder.id());
    //$$     ItemStack cobbledDeepslate = new ItemStack(Items.COBBLED_DEEPSLATE);
    //$$     ItemStack deepslate = new ItemStack(Items.DEEPSLATE);
    //$$     for (RecipeHolder<?> holder : originalRecipes) {
    //$$         Recipe<?> recipe = holder.value();
    //$$         if (!(recipe instanceof StonecutterRecipe)) continue;
    //$$         StonecutterRecipe source = (StonecutterRecipe) recipe;
    //$$         if (!shouldMirror(source, cobbledDeepslate, deepslate)) continue;
    //#if MC >= 12103
    //$$         ResourceKey<Recipe<?>> generatedId = generatedRecipeId(holder);
    //#else
    //$$         ResourceLocation generatedId = generatedRecipeId(holder.id());
    //#endif
    //$$         if (!usedIds.add(generatedId)) continue;
    //$$         StonecutterRecipe mirror = createMirror(server, source, cobbledDeepslate, generatedId);
    //$$         RecipeHolder<StonecutterRecipe> generated = new RecipeHolder<StonecutterRecipe>(generatedId, mirror);
    //$$         recipes.add(generated);
    //$$         GENERATED_ENTRIES.add(generated);
    //$$     }
    //$$ }
    //#elseif MC >= 11700
    //$$ private static void addMirroredRecipes(MinecraftServer server, List<Recipe<?>> recipes) {
    //$$     List<Recipe<?>> originalRecipes = new ArrayList<Recipe<?>>(recipes);
    //$$     Set<Object> usedIds = new HashSet<Object>();
    //$$     for (Recipe<?> recipe : recipes) usedIds.add(recipe.getId());
    //$$     ItemStack cobbledDeepslate = new ItemStack(Items.COBBLED_DEEPSLATE);
    //$$     ItemStack deepslate = new ItemStack(Items.DEEPSLATE);
    //$$     for (Recipe<?> recipe : originalRecipes) {
    //$$         if (!(recipe instanceof StonecutterRecipe)) continue;
    //$$         StonecutterRecipe source = (StonecutterRecipe) recipe;
    //$$         if (!shouldMirror(source, cobbledDeepslate, deepslate)) continue;
    //$$         ResourceLocation generatedId = generatedRecipeId(source.getId());
    //$$         if (!usedIds.add(generatedId)) continue;
    //$$         StonecutterRecipe mirror = createMirror(server, source, cobbledDeepslate, generatedId);
    //$$         recipes.add(mirror);
    //$$         GENERATED_ENTRIES.add(mirror);
    //$$     }
    //$$ }
    //#endif

    //#if MC >= 11700
    //$$ private static boolean shouldMirror(StonecutterRecipe source, ItemStack cobbledDeepslate, ItemStack deepslate) {
    //#if MC >= 12103
    //$$     Ingredient ingredient = source.input();
    //#else
    //$$     Ingredient ingredient = source.getIngredients().get(0);
    //#endif
    //$$     return ingredient.test(cobbledDeepslate) && !ingredient.test(deepslate);
    //$$ }
    //#endif

    //#if MC >= 11700
    //$$ private static StonecutterRecipe createMirror(MinecraftServer server, StonecutterRecipe source, ItemStack cobbledDeepslate, Object generatedId) {
    //$$     ItemStack result = getResult(server, source, cobbledDeepslate);
    //#if MC >= 260000
    //$$     return new StonecutterRecipe(new Recipe.CommonInfo(source.showNotification()), Ingredient.of(Items.DEEPSLATE), ItemStackTemplate.fromNonEmptyStack(result));
    //#elseif MC >= 12103
    //$$     return new StonecutterRecipe(source.group(), Ingredient.of(Items.DEEPSLATE), result);
    //#elseif MC >= 12002
    //$$     return new StonecutterRecipe(source.getGroup(), Ingredient.of(Items.DEEPSLATE), result);
    //#else
    //$$     return new StonecutterRecipe((ResourceLocation) generatedId, source.getGroup(), Ingredient.of(Items.DEEPSLATE), result);
    //#endif
    //$$ }
    //#endif

    //#if MC >= 11700
    //$$ private static ItemStack getResult(MinecraftServer server, StonecutterRecipe source, ItemStack cobbledDeepslate) {
    //#if MC >= 260000
    //$$     return source.assemble(new SingleRecipeInput(cobbledDeepslate)).copy();
    //#elseif MC >= 12103
    //$$     return source.assemble(new SingleRecipeInput(cobbledDeepslate), server.registryAccess()).copy();
    //#elseif MC >= 11904
    //$$     return source.getResultItem(server.registryAccess()).copy();
    //#else
    //$$     return source.getResultItem().copy();
    //#endif
    //$$ }
    //#endif

    //#if MC >= 11700
    //#if MC < 12103
    //$$ private static ResourceLocation generatedRecipeId(ResourceLocation sourceId) {
    //$$     String path = "stonecutter_cuts_deepslate/" + sourceId.getNamespace() + "/" + sourceId.getPath();
    //#if MC >= 12101
    //$$     return ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path);
    //#else
    //$$     return new ResourceLocation(CarpetDDSAddition.MOD_ID, path);
    //#endif
    //$$ }
    //#endif
    //#endif

    //#if MC >= 12103
    //$$ private static ResourceKey<Recipe<?>> generatedRecipeId(RecipeHolder<?> sourceHolder) {
    //#if MC >= 12111
    //$$     Identifier sourceId = sourceHolder.id().identifier();
    //$$     Identifier generatedId = Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "stonecutter_cuts_deepslate/" + sourceId.getNamespace() + "/" + sourceId.getPath());
    //#else
    //$$     ResourceLocation sourceId = sourceHolder.id().location();
    //$$     ResourceLocation generatedId = ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "stonecutter_cuts_deepslate/" + sourceId.getNamespace() + "/" + sourceId.getPath());
    //#endif
    //$$     return ResourceKey.create(Registries.RECIPE, generatedId);
    //$$ }
    //#endif
}
