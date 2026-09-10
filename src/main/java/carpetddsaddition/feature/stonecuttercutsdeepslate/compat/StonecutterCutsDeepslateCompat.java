/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate.compat;

import carpetddsaddition.CarpetDDSAddition;
import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.platform.minecraft.recipe.StonecutterRecipeRuntimeCompat;
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
//$$ import carpetddsaddition.platform.minecraft.recipe.RecipeManagerBridge;
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.world.item.crafting.SingleRecipeInput;
//#endif
//#if MC >= 11700 && MC < 12111
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#endif
//#if MC >= 260000
//$$ import net.minecraft.world.item.ItemStackTemplate;
//#endif

public final class StonecutterCutsDeepslateCompat {
    private static Boolean appliedState; private static Object appliedRecipeManager;
    private static final Set<Object> GENERATED_ENTRIES=Collections.newSetFromMap(new IdentityHashMap<Object,Boolean>());
    private StonecutterCutsDeepslateCompat(){}

    public static void onRecipeManagerApplied(Object value){
        //#if MC >= 12103
        //$$ RecipeManager manager=(RecipeManager)value; boolean enabled=DDSRules.stonecutterCutsDeepslate(); StonecutterRecipeRuntimeCompat.replaceLoadedRecipes(manager,buildModernRecipes(manager,enabled)); remember(manager,enabled);
        //#endif
    }
    public static void refreshLegacyLifecycle(MinecraftServer server){
        //#if MC >= 11700 && MC < 12103
        //$$ refresh(server,true);
        //#endif
    }
    public static void refresh(MinecraftServer server,boolean force){
        //#if MC >= 11700
        //$$ if(server==null)return; boolean enabled=DDSRules.stonecutterCutsDeepslate(); RecipeManager manager=server.getRecipeManager();
        //$$ if(!force&&appliedRecipeManager==manager&&appliedState!=null&&appliedState.booleanValue()==enabled)return;
        //#if MC >= 12103
        //$$ StonecutterRecipeRuntimeCompat.replace(server,manager,buildModernRecipes(manager,enabled));
        //#else
        //$$ rebuildLegacy(server,manager,enabled);
        //#endif
        //$$ remember(manager,enabled);
        //#endif
    }
    public static void reset(){appliedState=null; appliedRecipeManager=null; GENERATED_ENTRIES.clear();}
    private static void remember(Object manager,boolean enabled){appliedRecipeManager=manager; appliedState=Boolean.valueOf(enabled);}

    //#if MC >= 12103
    //$$ private static List<RecipeHolder<?>> buildModernRecipes(RecipeManager manager,boolean enabled){
    //$$     List<RecipeHolder<?>> recipes=new ArrayList<RecipeHolder<?>>(manager.getRecipes()); recipes.removeIf(GENERATED_ENTRIES::contains); GENERATED_ENTRIES.clear();
    //$$     if(enabled)addMirroredRecipes(((RecipeManagerBridge)(Object)manager).carpetDDSAddition$getRegistries(),recipes); return recipes;
    //$$ }
    //$$ private static void addMirroredRecipes(HolderLookup.Provider registries,List<RecipeHolder<?>> recipes){
    //$$     List<RecipeHolder<?>> original=new ArrayList<RecipeHolder<?>>(recipes); Set<Object> usedIds=new HashSet<Object>(); for(RecipeHolder<?> holder:recipes)usedIds.add(holder.id());
    //$$     ItemStack cobbled=new ItemStack(Items.COBBLED_DEEPSLATE),deepslate=new ItemStack(Items.DEEPSLATE);
    //$$     for(RecipeHolder<?> holder:original){Recipe<?> recipe=holder.value(); if(!(recipe instanceof StonecutterRecipe))continue; StonecutterRecipe source=(StonecutterRecipe)recipe; if(!shouldMirror(source,cobbled,deepslate))continue;
    //$$         ResourceKey<Recipe<?>> id=generatedRecipeId(holder); if(!usedIds.add(id))continue; StonecutterRecipe mirror=createModernMirror(registries,source,cobbled); RecipeHolder<StonecutterRecipe> generated=new RecipeHolder<StonecutterRecipe>(id,mirror); recipes.add(generated); GENERATED_ENTRIES.add(generated);}
    //$$ }
    //$$ private static StonecutterRecipe createModernMirror(HolderLookup.Provider registries,StonecutterRecipe source,ItemStack cobbled){ItemStack result=getModernResult(registries,source,cobbled);
    //#if MC >= 260000
    //$$     return new StonecutterRecipe(new Recipe.CommonInfo(source.showNotification()),Ingredient.of(Items.DEEPSLATE),ItemStackTemplate.fromNonEmptyStack(result));
    //#else
    //$$     return new StonecutterRecipe(source.group(),Ingredient.of(Items.DEEPSLATE),result);
    //#endif
    //$$ }
    //$$ private static ItemStack getModernResult(HolderLookup.Provider registries,StonecutterRecipe source,ItemStack cobbled){
    //#if MC >= 260000
    //$$     return source.assemble(new SingleRecipeInput(cobbled)).copy();
    //#else
    //$$     return source.assemble(new SingleRecipeInput(cobbled),registries).copy();
    //#endif
    //$$ }
    //#endif

    //#if MC >= 11700 && MC < 12103
    //$$ private static void rebuildLegacy(MinecraftServer server,RecipeManager manager,boolean enabled){
    //#if MC >= 12002
    //$$     List<RecipeHolder<?>> recipes=new ArrayList<RecipeHolder<?>>(manager.getRecipes());
    //#else
    //$$     List<Recipe<?>> recipes=new ArrayList<Recipe<?>>(manager.getRecipes());
    //#endif
    //$$     recipes.removeIf(GENERATED_ENTRIES::contains); GENERATED_ENTRIES.clear(); if(enabled)addLegacyMirroredRecipes(server,recipes); StonecutterRecipeRuntimeCompat.replace(server,manager,recipes);
    //$$ }
    //#if MC >= 12002
    //$$ private static void addLegacyMirroredRecipes(MinecraftServer server,List<RecipeHolder<?>> recipes){
    //$$     List<RecipeHolder<?>> original=new ArrayList<RecipeHolder<?>>(recipes); Set<Object> usedIds=new HashSet<Object>(); for(RecipeHolder<?> holder:recipes)usedIds.add(holder.id()); ItemStack cobbled=new ItemStack(Items.COBBLED_DEEPSLATE),deepslate=new ItemStack(Items.DEEPSLATE);
    //$$     for(RecipeHolder<?> holder:original){Recipe<?> recipe=holder.value(); if(!(recipe instanceof StonecutterRecipe))continue; StonecutterRecipe source=(StonecutterRecipe)recipe; if(!shouldMirror(source,cobbled,deepslate))continue; ResourceLocation id=generatedRecipeId(holder.id()); if(!usedIds.add(id))continue; StonecutterRecipe mirror=createLegacyMirror(server,source,cobbled,id); RecipeHolder<StonecutterRecipe> generated=new RecipeHolder<StonecutterRecipe>(id,mirror); recipes.add(generated); GENERATED_ENTRIES.add(generated);}
    //$$ }
    //#else
    //$$ private static void addLegacyMirroredRecipes(MinecraftServer server,List<Recipe<?>> recipes){
    //$$     List<Recipe<?>> original=new ArrayList<Recipe<?>>(recipes); Set<Object> usedIds=new HashSet<Object>(); for(Recipe<?> recipe:recipes)usedIds.add(recipe.getId()); ItemStack cobbled=new ItemStack(Items.COBBLED_DEEPSLATE),deepslate=new ItemStack(Items.DEEPSLATE);
    //$$     for(Recipe<?> recipe:original){if(!(recipe instanceof StonecutterRecipe))continue; StonecutterRecipe source=(StonecutterRecipe)recipe; if(!shouldMirror(source,cobbled,deepslate))continue; ResourceLocation id=generatedRecipeId(source.getId()); if(!usedIds.add(id))continue; StonecutterRecipe mirror=createLegacyMirror(server,source,cobbled,id); recipes.add(mirror); GENERATED_ENTRIES.add(mirror);}
    //$$ }
    //#endif
    //$$ private static StonecutterRecipe createLegacyMirror(MinecraftServer server,StonecutterRecipe source,ItemStack cobbled,ResourceLocation id){ItemStack result=getLegacyResult(server,source);
    //#if MC >= 12002
    //$$     return new StonecutterRecipe(source.getGroup(),Ingredient.of(Items.DEEPSLATE),result);
    //#else
    //$$     return new StonecutterRecipe(id,source.getGroup(),Ingredient.of(Items.DEEPSLATE),result);
    //#endif
    //$$ }
    //$$ private static ItemStack getLegacyResult(MinecraftServer server,StonecutterRecipe source){
    //#if MC >= 11904
    //$$     return source.getResultItem(server.registryAccess()).copy();
    //#else
    //$$     return source.getResultItem().copy();
    //#endif
    //$$ }
    //$$ private static ResourceLocation generatedRecipeId(ResourceLocation sourceId){String path="stonecutter_cuts_deepslate/"+sourceId.getNamespace()+"/"+sourceId.getPath();
    //#if MC >= 12101
    //$$     return ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID,path);
    //#else
    //$$     return new ResourceLocation(CarpetDDSAddition.MOD_ID,path);
    //#endif
    //$$ }
    //#endif

    //#if MC >= 11700
    //$$ private static boolean shouldMirror(StonecutterRecipe source,ItemStack cobbled,ItemStack deepslate){
    //#if MC >= 12103
    //$$     Ingredient ingredient=source.input();
    //#else
    //$$     Ingredient ingredient=source.getIngredients().get(0);
    //#endif
    //$$     return ingredient.test(cobbled)&&!ingredient.test(deepslate);
    //$$ }
    //#endif

    //#if MC >= 12103
    //$$ private static ResourceKey<Recipe<?>> generatedRecipeId(RecipeHolder<?> sourceHolder){
    //#if MC >= 12111
    //$$     Identifier sourceId=sourceHolder.id().identifier(); Identifier generatedId=Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID,"stonecutter_cuts_deepslate/"+sourceId.getNamespace()+"/"+sourceId.getPath());
    //#else
    //$$     ResourceLocation sourceId=sourceHolder.id().location(); ResourceLocation generatedId=ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID,"stonecutter_cuts_deepslate/"+sourceId.getNamespace()+"/"+sourceId.getPath());
    //#endif
    //$$     return ResourceKey.create(Registries.RECIPE,generatedId);
    //$$ }
    //#endif
}
