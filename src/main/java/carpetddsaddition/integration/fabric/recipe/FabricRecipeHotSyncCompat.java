/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.fabric.recipe;

import net.minecraft.server.MinecraftServer;
import net.fabricmc.loader.api.FabricLoader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
//#if MC >= 12103
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//#endif

/**
 * Optional Fabric recipe resynchronization. The published lifecycle event is
 * the only public dispatch point; it also invokes other mods' reload listeners.
 * No Fabric API runtime dependency is imposed on servers without Fabric API.
 */
public final class FabricRecipeHotSyncCompat {
    private FabricRecipeHotSyncCompat() {}

    /**
     * Fabric's SynchronizedRecipes snapshot is separate from the RecipeMap. Vanilla
     * recipe replacement does not rebuild it, so refresh it before notifying mods.
     * This path must remain optional for servers without Fabric Recipe API.
     */
    //#if MC >= 12103
    //$$ public static void refreshSnapshot(RecipeManager manager) {
    //$$     if (!FabricLoader.getInstance().isModLoaded("fabric-recipe-api-v1")) return;
    //$$     try {
    //$$         Object oldSnapshot = manager.getClass().getMethod("getSynchronizedRecipes").invoke(manager);
    //$$         Object newSnapshot = createSnapshot(
    //$$                 Class.forName("net.fabricmc.fabric.impl.recipe.sync.SynchronizedRecipesImpl"),
    //$$                 manager.getRecipes());
    //$$         if (oldSnapshot == null || !replaceSnapshotField(manager, oldSnapshot, newSnapshot)) {
    //$$             throw new IllegalStateException("DDS could not locate Fabric synchronized recipe snapshot");
    //$$         }
    //$$         if (Boolean.getBoolean("carpetddsaddition.recipe_snapshot_audit")) {
    //$$             // Verify the index actually exposed by Fabric after field replacement.
    //$$             verifyRecipeIds(manager.getRecipes(),
    //$$                     manager.getClass().getMethod("getSynchronizedRecipes").invoke(manager));
    //$$             System.out.println("[Carpet DDS Addition] Fabric recipe snapshot IDs match ("
    //$$                     + manager.getRecipes().size() + " recipes).");
    //$$         }
    //$$     } catch (ReflectiveOperationException exception) {
    //$$         throw new IllegalStateException("DDS could not refresh Fabric recipe snapshot", exception);
    //$$     }
    //$$ }
    //#endif

    /** Fabric API provides an iterable factory across RecipeMap-backed and indexed versions. */
    static Object createSnapshot(Class<?> implementation, Iterable<?> recipes)
            throws ReflectiveOperationException {
        return implementation.getMethod("of", Iterable.class).invoke(null, recipes);
    }

    /** Optional runtime audit against the actual Fabric index; no extra work unless enabled. */
    static void verifyRecipeIds(Iterable<?> liveRecipes, Object snapshot)
            throws ReflectiveOperationException {
        Object recipes = snapshot.getClass().getMethod("recipes").invoke(snapshot);
        if (!(recipes instanceof Iterable<?>)) {
            throw new IllegalStateException("Fabric synchronized recipes are not iterable");
        }
        Set<Object> liveIds = recipeIds(liveRecipes);
        Set<Object> indexedIds = recipeIds((Iterable<?>) recipes);
        if (!liveIds.equals(indexedIds)) {
            Set<Object> missing = new HashSet<>(liveIds);
            missing.removeAll(indexedIds);
            Set<Object> stale = new HashSet<>(indexedIds);
            stale.removeAll(liveIds);
            throw new IllegalStateException("DDS Fabric recipe snapshot mismatch: missing="
                    + missing + ", stale=" + stale);
        }
    }

    private static Set<Object> recipeIds(Iterable<?> recipes) throws ReflectiveOperationException {
        Set<Object> ids = new HashSet<>();
        for (Object recipe : recipes) {
            ids.add(recipe.getClass().getMethod("id").invoke(recipe));
        }
        return ids;
    }

    /** Match the current snapshot by identity, avoiding reliance on Fabric's Mixin field name. */
    static boolean replaceSnapshotField(Object manager, Object current, Object updated)
            throws IllegalAccessException {
        for (Field field : manager.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || !field.getType().isInstance(updated)) continue;
            field.setAccessible(true);
            if (field.get(manager) != current) continue;
            field.set(manager, updated);
            return true;
        }
        return false;
    }

    public static void send(MinecraftServer server) {
        //#if MC >= 12103
        //$$ if (!FabricLoader.getInstance().isModLoaded("fabric-recipe-api-v1")) return;
        //$$ try {
        //$$     Class<?> events = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents");
        //$$     Object event = events.getField("SYNC_DATA_PACK_CONTENTS").get(null);
        //$$     Object invoker = Class.forName("net.fabricmc.fabric.api.event.Event")
        //$$             .getMethod("invoker").invoke(event);
        //$$     Class<?> listener = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$SyncDataPackContents");
        //$$     for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        //$$         listener.getMethod("onSyncDataPackContents", ServerPlayer.class, boolean.class)
        //$$                 .invoke(invoker, player, false);
        //$$     }
        //$$ } catch (ReflectiveOperationException exception) {
        //$$     throw new IllegalStateException("DDS could not dispatch Fabric recipe synchronization", exception);
        //$$ }
        //#endif
    }
}
