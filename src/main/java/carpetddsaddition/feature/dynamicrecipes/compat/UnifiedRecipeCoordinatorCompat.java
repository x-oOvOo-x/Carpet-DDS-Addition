/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.dynamicrecipes.compat;

import net.minecraft.server.MinecraftServer;
import carpetddsaddition.feature.stonecuttercutswood.StonecutterCutsWood;
//#if MC < 260100
import carpetddsaddition.feature.stonecuttercutsdeepslate.StonecutterCutsDeepslate;
//#endif

//#if MC >= 12103
//$$ import carpetddsaddition.feature.stonecuttercutswood.compat.StonecutterCutsWoodCompat;
//#if MC < 260100
//$$ import carpetddsaddition.feature.stonecuttercutsdeepslate.compat.StonecutterCutsDeepslateCompat;
//#endif
//$$ import carpetddsaddition.feature.smeltconcretepowdertoglass.compat.ConcretePowderToGlassCompat;
//$$ import carpetddsaddition.generated.DDSRules;
//$$ import carpetddsaddition.platform.minecraft.recipe.StonecutterRecipeRuntimeCompat;
//$$ import carpetddsaddition.integration.fabric.recipe.FabricRecipeHotSyncCompat;
//#if MC >= 260300
//$$ import carpetddsaddition.platform.minecraft.recipe.RecipeLookupCompat;
//$$ import net.minecraft.core.registries.Registries;
//#endif
//#if MC < 260300
//$$ import carpetddsaddition.platform.minecraft.recipe.RecipeManagerBridge;
//#endif
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//$$ import net.minecraft.world.item.crafting.RecipeType;
//$$ import net.minecraft.world.item.crafting.SingleRecipeInput;
//$$ import java.util.ArrayList;
//$$ import java.util.HashSet;
//$$ import java.util.List;
//$$ import java.util.Set;
//$$ import java.util.Map;
//$$ import java.util.WeakHashMap;
//#if MC >= 260300
//$$ import carpetddsaddition.platform.minecraft.recipe.UnifiedRecipeManagerCompat;
//#endif
//#endif

/** A single, original-snapshot-based commit path for DDS recipe rules. */
public final class UnifiedRecipeCoordinatorCompat {
    private UnifiedRecipeCoordinatorCompat() {}

    //#if MC >= 12103
    //$$ private record Rules(boolean wood, boolean deepslate, boolean concrete) {}
    //$$ private static final class State {
    //$$     final List<RecipeHolder<?>> original;
    //$$     Rules applied;
    //$$     State(List<RecipeHolder<?>> original) { this.original = List.copyOf(original); }
    //$$ }
    //$$ private static final Map<RecipeManager, State> STATES = new WeakHashMap<>();
    //$$ private static Rules rules() {
    //$$     return new Rules(DDSRules.stonecutterCutsWood(),
    //#if MC < 260100
    //$$             DDSRules.stonecutterCutsDeepslate(), DDSRules.smeltConcretePowderToGlass());
    //#else
    //$$             false, DDSRules.smeltConcretePowderToGlass());
    //#endif
    //$$ }
    //$$ public static synchronized void onManagerPrepared(RecipeManager manager
    //#if MC >= 260300
    //$$         , HolderLookup.Provider registries
    //#endif
    //$$ ) {
    //#if MC >= 260300
    //$$     if (UnifiedRecipeManagerCompat.isBuilding()) return;
    //#endif
    //#if MC >= 260300
    //$$     // The original registry is the baseline. The RecipeManager has already
    //$$     // consumed the DDS-enriched lookup inside RecipeMap.create().
    //$$     State state = new State(originalRecipes(registries.lookupOrThrow(Registries.RECIPE)));
    //$$     STATES.put(manager, state);
    //$$     state.applied = rules();
    //#else
    //$$     State state = new State(new ArrayList<>(manager.getRecipes()));
    //$$     STATES.put(manager, state);
    //$$     Rules initial = rules();
    //$$     if (!initial.wood() && !initial.deepslate() && !initial.concrete()) {
    //$$         state.applied = initial;
    //$$         return;
    //$$     }
    //$$     List<RecipeHolder<?>> target = build(state.original, initial,
    //$$             ((RecipeManagerBridge) (Object) manager).carpetDDSAddition$getRegistries());
    //$$     StonecutterRecipeRuntimeCompat.replaceLoadedRecipes(manager, target);
    //$$     FabricRecipeHotSyncCompat.refreshSnapshot(manager);
    //$$     state.applied = initial;
    //#endif
    //$$ }
    //#endif

    public static void onRuleChanged(MinecraftServer server) {
        //#if MC >= 12103
        //$$ if (server == null) return;
        //$$ RecipeManager manager = server.getRecipeManager();
        //$$ State state;
        //$$ Rules next = rules();
        //$$ synchronized (UnifiedRecipeCoordinatorCompat.class) {
        //$$     state = STATES.get(manager);
        //$$     if (state == null) {
        //$$         throw new IllegalStateException("DDS recipe baseline is missing for the live RecipeManager");
        //$$     }
        //$$     if (next.equals(state.applied)) return;
        //$$     List<RecipeHolder<?>> target = build(state.original, next, server.registryAccess());
        //#if MC >= 260300
        //$$     UnifiedRecipeManagerCompat.applyAtRuntime(server, manager, target);
        //#else
        //$$     StonecutterRecipeRuntimeCompat.replaceLoadedRecipes(manager, target);
        //$$     manager.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
        //#endif
        //$$     // The manager mutation is committed even if client notification fails.
        //$$     state.applied = next;
        //$$     FabricRecipeHotSyncCompat.refreshSnapshot(manager);
        //$$     FabricRecipeHotSyncCompat.send(server);
        //$$     StonecutterRecipeRuntimeCompat.syncPlayers(server);
        //$$ }
        //#else
        StonecutterCutsWood.refresh(server, false);
        //#if MC < 260100
        StonecutterCutsDeepslate.refresh(server, false);
        //#endif
        //#endif
    }

    //#if MC >= 260300
    //$$ public static HolderLookup<Recipe<?>> prepareLookup(HolderLookup<Recipe<?>> original) {
    //$$     // A runtime candidate already receives the complete target recipe registry.
    //$$     if (UnifiedRecipeManagerCompat.isBuilding()) return original;
    //$$     Rules current = rules();
    //$$     if (!current.wood() && !current.deepslate() && !current.concrete()) return original;
    //$$     List<RecipeHolder<?>> baseline = originalRecipes(original);
    //$$     // In 26.3, stonecutter result assembly no longer needs registry access.
    //$$     List<RecipeHolder<?>> target = build(baseline, current, null);
    //$$     return target.size() == baseline.size() ? original
    //$$             : RecipeLookupCompat.fromRecipes(original, target);
    //$$ }
    //$$ private static List<RecipeHolder<?>> originalRecipes(HolderLookup<Recipe<?>> lookup) {
    //$$     List<RecipeHolder<?>> entries = new ArrayList<>();
    //$$     lookup.listElements().forEach(holder ->
    //$$             entries.add(new RecipeHolder<>(holder.key(), holder.value())));
    //$$     return entries;
    //$$ }
    //#endif
    //#if MC >= 12103
    //$$ private static List<RecipeHolder<?>> build(List<RecipeHolder<?>> original, Rules rules,
    //$$         HolderLookup.Provider registries) {
    //$$     List<RecipeHolder<?>> target = new ArrayList<>(original);
    //$$     if (rules.wood()) StonecutterCutsWoodCompat.contribute(target);
    //#if MC < 260100
    //$$     if (rules.deepslate()) StonecutterCutsDeepslateCompat.contribute(target, registries);
    //#endif
    //$$     if (rules.concrete()) addConcrete(target, original);
    //$$     return target;
    //$$ }
    //$$ @SuppressWarnings("unchecked")
    //$$ private static void addConcrete(List<RecipeHolder<?>> target, List<RecipeHolder<?>> original) {
    //$$     Set<Object> ids = new HashSet<>();
    //$$     for (RecipeHolder<?> recipe : target) ids.add(recipe.id());
    //$$     for (RecipeHolder<?> candidate : ConcretePowderToGlassCompat.candidateRecipes()) {
    //$$         if (ids.contains(candidate.id())) continue;
    //$$         if (original.stream().anyMatch(existing -> existing.value().getType() == RecipeType.SMELTING
    //$$                 && conflicts(existing, candidate))) continue;
    //$$         target.add(candidate);
    //$$         ids.add(candidate.id());
    //$$     }
    //$$ }
    //$$ @SuppressWarnings({"rawtypes", "unchecked"})
    //$$ private static boolean conflicts(RecipeHolder<?> existing, RecipeHolder<?> candidate) {
    //$$     ItemStack probe = ConcretePowderToGlassCompat.inputFor(candidate.id());
    //$$     if (probe.isEmpty()) return true;
    //$$     try {
    //$$         return ((Recipe) existing.value()).matches(new SingleRecipeInput(probe), null);
    //$$     } catch (RuntimeException exception) {
    //$$         // A custom cooking recipe requiring a Level must take precedence over DDS.
    //$$         return true;
    //$$     }
    //$$ }
    //#endif

    public static synchronized void reset() {
        //#if MC >= 12103
        //$$ STATES.clear();
        //#endif
    }
}
