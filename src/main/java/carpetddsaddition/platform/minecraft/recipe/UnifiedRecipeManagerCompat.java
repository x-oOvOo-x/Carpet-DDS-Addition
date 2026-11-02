/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.recipe;

import net.minecraft.server.MinecraftServer;

//#if MC >= 260300
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.core.RegistrySetBuilder;
//$$ import net.minecraft.core.registries.Registries;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//$$ import java.lang.reflect.Field;
//$$ import java.util.List;
//#endif

/** 26.3 constructor-only caches are copied from a correctly built candidate manager. */
public final class UnifiedRecipeManagerCompat {
    private UnifiedRecipeManagerCompat() {}
    //#if MC >= 260300
    //$$ private static final ThreadLocal<Boolean> BUILDING = ThreadLocal.withInitial(() -> false);
    //$$ private static final String[] REBUILT_FIELDS = {
    //$$         "recipes", "learnableRecipes", "propertySets", "stonecutterRecipes",
    //$$         "allDisplays", "recipeToDisplay"
    //$$ };
    //$$ public static boolean isBuilding() { return BUILDING.get(); }
    //$$ public static void applyAtRuntime(MinecraftServer server, RecipeManager target,
    //$$         List<RecipeHolder<?>> recipes) {
    //$$     RecipeManager rebuilt = construct(server.registryAccess(), recipes, server);
    //$$     transfer(target, rebuilt);
    //$$ }
    //$$ private static RecipeManager construct(HolderLookup.Provider baseline,
    //$$         List<RecipeHolder<?>> recipes, MinecraftServer server) {
    //$$     HolderLookup.Provider provider = new RegistrySetBuilder()
    //$$             .add(Registries.RECIPE, context -> {
    //$$                 for (RecipeHolder<?> entry : recipes) {
    //$$                     context.register(entry.id(), entry.value());
    //$$                 }
    //$$             }).build(baseline);
    //$$     if (BUILDING.get()) throw new IllegalStateException("nested DDS recipe rebuild");
    //$$     BUILDING.set(true);
    //$$     try {
    //$$         RecipeManager candidate = new RecipeManager(provider);
    //$$         if (server != null) candidate.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    //$$         return candidate;
    //$$     } finally {
    //$$         BUILDING.remove();
    //$$     }
    //$$ }
    //$$ private static void transfer(RecipeManager target, RecipeManager candidate) {
    //$$     try {
    //$$         for (String name : REBUILT_FIELDS) {
    //$$             Field field = RecipeManager.class.getDeclaredField(name);
    //$$             field.setAccessible(true);
    //$$             field.set(target, field.get(candidate));
    //$$         }
    //$$     } catch (ReflectiveOperationException exception) {
    //$$         throw new IllegalStateException("DDS could not copy RecipeManager derived state", exception);
    //$$     }
    //$$ }
    //#endif
}
