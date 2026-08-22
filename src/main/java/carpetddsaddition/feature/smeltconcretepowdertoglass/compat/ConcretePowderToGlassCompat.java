/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.smeltconcretepowdertoglass.compat;

import carpetddsaddition.CarpetDDSAddition;
import carpetddsaddition.generated.DDSRules;
//#if MC >= 12103
//$$ import net.minecraft.core.registries.Registries;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
import net.minecraft.resources.ResourceLocation;
//#endif
//#if MC >= 12103
//$$ import net.minecraft.resources.ResourceKey;
//#endif
//#if MC < 12101
import net.minecraft.world.Container;
//#endif
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//#if MC >= 260102
//$$ import net.minecraft.world.item.ItemStackTemplate;
//$$ import net.minecraft.world.item.crafting.AbstractCookingRecipe;
//#endif
//#if MC >= 11903
//$$ import net.minecraft.world.item.crafting.CookingBookCategory;
//#endif
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
//#if MC >= 12002
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//#endif
//#if MC >= 12101
//$$ import net.minecraft.world.item.crafting.RecipeInput;
//$$ import net.minecraft.world.item.crafting.SingleRecipeInput;
//#endif
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/** Synthetic furnace recipes; vanilla lookup always takes priority. */
public final class ConcretePowderToGlassCompat {
    private static final float EXPERIENCE = 0.1F;
    private static final int COOKING_TIME = 200;

    //#if MC >= 12002
    //$$ private static final Map<Item, RecipeHolder<?>> RECIPES_BY_INPUT = new IdentityHashMap<Item, RecipeHolder<?>>();
    //#if MC >= 12103
    //$$ private static final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> RECIPES_BY_ID = new HashMap<ResourceKey<Recipe<?>>, RecipeHolder<?>>();
    //#else
    //$$ private static final Map<ResourceLocation, RecipeHolder<?>> RECIPES_BY_ID = new HashMap<ResourceLocation, RecipeHolder<?>>();
    //#endif
    //#else
    private static final Map<Item, SmeltingRecipe> RECIPES_BY_INPUT = new IdentityHashMap<Item, SmeltingRecipe>();
    private static final Map<ResourceLocation, SmeltingRecipe> RECIPES_BY_ID = new HashMap<ResourceLocation, SmeltingRecipe>();
    //#endif

    static {
        //#if MC >= 260200
        //$$ register("white", Items.CONCRETE_POWDER.white(), Items.STAINED_GLASS.white());
        //$$ register("orange", Items.CONCRETE_POWDER.orange(), Items.STAINED_GLASS.orange());
        //$$ register("magenta", Items.CONCRETE_POWDER.magenta(), Items.STAINED_GLASS.magenta());
        //$$ register("light_blue", Items.CONCRETE_POWDER.lightBlue(), Items.STAINED_GLASS.lightBlue());
        //$$ register("yellow", Items.CONCRETE_POWDER.yellow(), Items.STAINED_GLASS.yellow());
        //$$ register("lime", Items.CONCRETE_POWDER.lime(), Items.STAINED_GLASS.lime());
        //$$ register("pink", Items.CONCRETE_POWDER.pink(), Items.STAINED_GLASS.pink());
        //$$ register("gray", Items.CONCRETE_POWDER.gray(), Items.STAINED_GLASS.gray());
        //$$ register("light_gray", Items.CONCRETE_POWDER.lightGray(), Items.STAINED_GLASS.lightGray());
        //$$ register("cyan", Items.CONCRETE_POWDER.cyan(), Items.STAINED_GLASS.cyan());
        //$$ register("purple", Items.CONCRETE_POWDER.purple(), Items.STAINED_GLASS.purple());
        //$$ register("blue", Items.CONCRETE_POWDER.blue(), Items.STAINED_GLASS.blue());
        //$$ register("brown", Items.CONCRETE_POWDER.brown(), Items.STAINED_GLASS.brown());
        //$$ register("green", Items.CONCRETE_POWDER.green(), Items.STAINED_GLASS.green());
        //$$ register("red", Items.CONCRETE_POWDER.red(), Items.STAINED_GLASS.red());
        //$$ register("black", Items.CONCRETE_POWDER.black(), Items.STAINED_GLASS.black());
        //#else
        register("white", Items.WHITE_CONCRETE_POWDER, Items.WHITE_STAINED_GLASS);
        register("orange", Items.ORANGE_CONCRETE_POWDER, Items.ORANGE_STAINED_GLASS);
        register("magenta", Items.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_STAINED_GLASS);
        register("light_blue", Items.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_STAINED_GLASS);
        register("yellow", Items.YELLOW_CONCRETE_POWDER, Items.YELLOW_STAINED_GLASS);
        register("lime", Items.LIME_CONCRETE_POWDER, Items.LIME_STAINED_GLASS);
        register("pink", Items.PINK_CONCRETE_POWDER, Items.PINK_STAINED_GLASS);
        register("gray", Items.GRAY_CONCRETE_POWDER, Items.GRAY_STAINED_GLASS);
        register("light_gray", Items.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_STAINED_GLASS);
        register("cyan", Items.CYAN_CONCRETE_POWDER, Items.CYAN_STAINED_GLASS);
        register("purple", Items.PURPLE_CONCRETE_POWDER, Items.PURPLE_STAINED_GLASS);
        register("blue", Items.BLUE_CONCRETE_POWDER, Items.BLUE_STAINED_GLASS);
        register("brown", Items.BROWN_CONCRETE_POWDER, Items.BROWN_STAINED_GLASS);
        register("green", Items.GREEN_CONCRETE_POWDER, Items.GREEN_STAINED_GLASS);
        register("red", Items.RED_CONCRETE_POWDER, Items.RED_STAINED_GLASS);
        register("black", Items.BLACK_CONCRETE_POWDER, Items.BLACK_STAINED_GLASS);
        //#endif
    }

    private ConcretePowderToGlassCompat() {}

    //#if MC >= 12101
    //$$ public static Optional<RecipeHolder<?>> findRecipe(RecipeType<?> recipeType, RecipeInput input) {
    //$$     if (!DDSRules.smeltConcretePowderToGlass() || recipeType != RecipeType.SMELTING || !(input instanceof SingleRecipeInput)) return Optional.empty();
    //$$     return Optional.ofNullable(RECIPES_BY_INPUT.get(((SingleRecipeInput) input).item().getItem()));
    //$$ }
    //#elseif MC >= 12002
    //$$ public static Optional<RecipeHolder<?>> findRecipe(RecipeType<?> recipeType, Container input) {
    //$$     if (!DDSRules.smeltConcretePowderToGlass() || recipeType != RecipeType.SMELTING) return Optional.empty();
    //$$     ItemStack stack = firstItem(input);
    //$$     return stack.isEmpty() ? Optional.empty() : Optional.ofNullable(RECIPES_BY_INPUT.get(stack.getItem()));
    //$$ }
    //#else
    public static Optional<SmeltingRecipe> findRecipe(RecipeType<?> recipeType, Container input) {
        if (!DDSRules.smeltConcretePowderToGlass() || recipeType != RecipeType.SMELTING) return Optional.empty();
        ItemStack stack = firstItem(input);
        return stack.isEmpty() ? Optional.empty() : Optional.ofNullable(RECIPES_BY_INPUT.get(stack.getItem()));
    }
    //#endif

    //#if MC < 12101
    private static ItemStack firstItem(Container input) { return input == null || input.getContainerSize() <= 0 ? ItemStack.EMPTY : input.getItem(0); }
    //#endif

    // ID lookup intentionally ignores the current rule: furnaces may resolve completed recipe IDs later for XP.
    //#if MC >= 12103
    //$$ public static Optional<RecipeHolder<?>> findRecipeById(ResourceKey<Recipe<?>> recipeId) { return Optional.ofNullable(RECIPES_BY_ID.get(recipeId)); }
    //#elseif MC >= 12002
    //$$ public static Optional<RecipeHolder<?>> findRecipeById(ResourceLocation recipeId) { return Optional.ofNullable(RECIPES_BY_ID.get(recipeId)); }
    //#else
    public static Optional<SmeltingRecipe> findRecipeById(ResourceLocation recipeId) { return Optional.ofNullable(RECIPES_BY_ID.get(recipeId)); }
    //#endif

    public static boolean isEnabledFurnaceInput(ItemStack stack) {
        return DDSRules.smeltConcretePowderToGlass() && !stack.isEmpty() && RECIPES_BY_INPUT.containsKey(stack.getItem());
    }

    private static void register(String color, Item concretePowder, Item stainedGlass) {
        //#if MC >= 12111
        //$$ ResourceKey<Recipe<?>> recipeId = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(
        //$$         CarpetDDSAddition.MOD_ID, "smelt_concrete_powder_to_glass/" + color));
        //#elseif MC >= 12103
        //$$ ResourceKey<Recipe<?>> recipeId = ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(
        //$$         CarpetDDSAddition.MOD_ID, "smelt_concrete_powder_to_glass/" + color));
        //#elseif MC >= 12101
        //$$ ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "smelt_concrete_powder_to_glass/" + color);
        //#else
        ResourceLocation recipeId = new ResourceLocation(CarpetDDSAddition.MOD_ID, "smelt_concrete_powder_to_glass/" + color);
        //#endif

        //#if MC >= 260102
        //$$ SmeltingRecipe recipe = new SmeltingRecipe(new Recipe.CommonInfo(true),
        //$$         new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.BLOCKS, ""), Ingredient.of(concretePowder),
        //$$         new ItemStackTemplate(stainedGlass), EXPERIENCE, COOKING_TIME);
        //#elseif MC >= 12002
        //$$ SmeltingRecipe recipe = new SmeltingRecipe("", CookingBookCategory.BLOCKS, Ingredient.of(concretePowder),
        //$$         new ItemStack(stainedGlass), EXPERIENCE, COOKING_TIME);
        //#elseif MC >= 11903
        //$$ SmeltingRecipe recipe = new SmeltingRecipe(recipeId, "", CookingBookCategory.BLOCKS, Ingredient.of(concretePowder),
        //$$         new ItemStack(stainedGlass), EXPERIENCE, COOKING_TIME);
        //#else
        SmeltingRecipe recipe = new SmeltingRecipe(recipeId, "", Ingredient.of(concretePowder), new ItemStack(stainedGlass), EXPERIENCE, COOKING_TIME);
        //#endif

        //#if MC >= 12002
        //$$ RecipeHolder<SmeltingRecipe> holder = new RecipeHolder<SmeltingRecipe>(recipeId, recipe);
        //$$ RECIPES_BY_INPUT.put(concretePowder, holder);
        //$$ RECIPES_BY_ID.put(recipeId, holder);
        //#else
        RECIPES_BY_INPUT.put(concretePowder, recipe);
        RECIPES_BY_ID.put(recipeId, recipe);
        //#endif
    }
}
