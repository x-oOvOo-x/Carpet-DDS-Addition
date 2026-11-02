/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.recipe;

//#if MC >= 260300
//$$ import carpetddsaddition.mixin.platform.minecraft.recipe.HolderReferenceInvoker;
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.core.HolderSet;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.tags.TagKey;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import java.util.LinkedHashMap;
//$$ import java.util.List;
//$$ import java.util.Map;
//$$ import java.util.Optional;
//$$ import java.util.stream.Stream;
//#endif

/** Preserves Fabric's RecipeMap.create post-processing for 26.3 runtime recipes. */
public final class RecipeLookupCompat {
    private RecipeLookupCompat(){}

    //#if MC >= 260300
    //$$ public static HolderLookup<Recipe<?>> fromRecipes(HolderLookup<Recipe<?>> base,List<RecipeHolder<?>> recipes){
    //$$     Map<ResourceKey<Recipe<?>>,Holder.Reference<Recipe<?>>> entries=new LinkedHashMap<>();
    //$$     for(RecipeHolder<?> recipe:recipes){
    //$$         Holder.Reference<Recipe<?>> original=base.get(recipe.id()).orElse(null);
    //$$         if(original!=null&&original.value()==recipe.value()){
    //$$             entries.put(recipe.id(),original);
    //$$         }else{
    //$$             Holder.Reference<Recipe<?>> added=Holder.Reference.createStandAlone(base,recipe.id());
    //$$             ((HolderReferenceInvoker)(Object)added).carpetDDSAddition$bindValue(recipe.value());
    //$$             entries.put(recipe.id(),added);
    //$$         }
    //$$     }
    //$$     return new HolderLookup<Recipe<?>>(){
    //$$         @Override public Stream<Holder.Reference<Recipe<?>>> listElements(){return entries.values().stream();}
    //$$         @Override public Optional<Holder.Reference<Recipe<?>>> get(ResourceKey<Recipe<?>> key){return Optional.ofNullable(entries.get(key));}
    //$$         @Override public Stream<HolderSet.Named<Recipe<?>>> listTags(){return base.listTags();}
    //$$         @Override public Optional<HolderSet.Named<Recipe<?>>> get(TagKey<Recipe<?>> tag){return base.get(tag);}
    //$$     };
    //$$ }
    //#endif
}
