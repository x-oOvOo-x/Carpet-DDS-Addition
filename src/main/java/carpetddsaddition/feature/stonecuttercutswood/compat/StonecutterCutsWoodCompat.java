/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood.compat;

import carpetddsaddition.CarpetDDSAddition;
import carpetddsaddition.core.rules.DdsRuleEvents;
import carpetddsaddition.feature.stonecuttercutswood.WoodRecipeCatalog;
import carpetddsaddition.feature.stonecuttercutswood.WoodRecipeSpec;
import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.platform.minecraft.recipe.StonecutterRecipeRuntimeCompat;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

//#if MC >= 12103
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.resources.ResourceKey;
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//$$ import net.minecraft.world.item.Item;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//$$ import net.minecraft.world.item.crafting.Ingredient;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//$$ import net.minecraft.world.item.crafting.StonecutterRecipe;
//#endif
//#if MC >= 260000
//$$ import net.minecraft.world.item.ItemStackTemplate;
//#endif

public final class StonecutterCutsWoodCompat {
    private static Boolean appliedState;
    private static Object appliedRecipeManager;
    private static final Set<Object> GENERATED_ENTRIES=Collections.newSetFromMap(new IdentityHashMap<Object,Boolean>());
    private StonecutterCutsWoodCompat(){}

    public static DdsRuleEvents.Observer ruleObserver(){
        //#if MC >= 12103
        //$$ return DdsRuleEvents.observe(DDSRules::stonecutterCutsWood,server->refresh(server,false));
        //#else
        return DdsRuleEvents.observe(()->false,server->{});
        //#endif
    }
    public static void refresh(MinecraftServer server,boolean force){
        //#if MC >= 12103
        //$$ if(server==null)return;
        //$$ boolean enabled=DDSRules.stonecutterCutsWood(); RecipeManager manager=server.getRecipeManager();
        //$$ if(!force&&appliedRecipeManager==manager&&appliedState!=null&&appliedState.booleanValue()==enabled)return;
        //$$ rebuild(server,manager,enabled); appliedRecipeManager=manager; appliedState=Boolean.valueOf(enabled);
        //#endif
    }
    public static void reset(){appliedState=null; appliedRecipeManager=null; GENERATED_ENTRIES.clear();}

    //#if MC >= 12103
    //$$ private static void rebuild(MinecraftServer server,RecipeManager manager,boolean enabled){
    //$$     List<RecipeHolder<?>> recipes=new ArrayList<RecipeHolder<?>>(manager.getRecipes()); recipes.removeIf(GENERATED_ENTRIES::contains); GENERATED_ENTRIES.clear();
    //$$     if(enabled)addGeneratedRecipes(recipes); StonecutterRecipeRuntimeCompat.replace(server,manager,recipes);
    //$$ }
    //$$ private static void addGeneratedRecipes(List<RecipeHolder<?>> recipes){
    //$$     Set<ResourceKey<Recipe<?>>> usedIds=new HashSet<ResourceKey<Recipe<?>>>(); for(RecipeHolder<?> holder:recipes)usedIds.add(holder.id());
    //$$     for(WoodRecipeSpec spec:WoodRecipeCatalog.recipes()){
    //$$         Item input=item(spec.input()),output=item(spec.output()); if(input==null||output==null||input==Items.AIR||output==Items.AIR)continue;
    //$$         ResourceKey<Recipe<?>> id=recipeId(spec); if(!usedIds.add(id))continue;
    //#if MC >= 260000
    //$$         StonecutterRecipe recipe=new StonecutterRecipe(new Recipe.CommonInfo(true),Ingredient.of(input),ItemStackTemplate.fromNonEmptyStack(new ItemStack(output,spec.count())));
    //#else
    //$$         StonecutterRecipe recipe=new StonecutterRecipe("",Ingredient.of(input),new ItemStack(output,spec.count()));
    //#endif
    //$$         RecipeHolder<StonecutterRecipe> holder=new RecipeHolder<StonecutterRecipe>(id,recipe); recipes.add(holder); GENERATED_ENTRIES.add(holder);
    //$$     }
    //$$ }
    //$$ private static Item item(String path){
    //#if MC >= 12111
    //$$     return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("minecraft",path));
    //#else
    //$$     return BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft",path));
    //#endif
    //$$ }
    //$$ private static ResourceKey<Recipe<?>> recipeId(WoodRecipeSpec spec){
    //#if MC >= 12111
    //$$     return ResourceKey.create(Registries.RECIPE,Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID,"stonecutter_cuts_wood/"+spec.input()+"_to_"+spec.output()));
    //#else
    //$$     return ResourceKey.create(Registries.RECIPE,ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID,"stonecutter_cuts_wood/"+spec.input()+"_to_"+spec.output()));
    //#endif
    //$$ }
    //#endif
}
