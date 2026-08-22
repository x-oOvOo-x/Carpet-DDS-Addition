/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.world.entity.Entity;
//$$
//$$ import java.util.Collections;
//$$ import java.util.IdentityHashMap;
//$$ import java.util.Set;
//#endif

public final class UndoEntityLifecycle {
    private UndoEntityLifecycle() {
    }

    //#if MC >= 12109
    //$$ private static final ThreadLocal<Set<Entity>> ADDING = new ThreadLocal<>();
    //$$
    //$$ public static void beginAdding(Entity entity) {
    //$$     Set<Entity> entities = ADDING.get();
    //$$     if (entities == null) {
    //$$         entities = Collections.newSetFromMap(new IdentityHashMap<>());
    //$$         ADDING.set(entities);
    //$$     }
    //$$     entities.add(entity);
    //$$ }
    //$$
    //$$ public static void endAdding(Entity entity) {
    //$$     Set<Entity> entities = ADDING.get();
    //$$     if (entities == null) return;
    //$$
    //$$     entities.remove(entity);
    //$$     if (entities.isEmpty()) {
    //$$         ADDING.remove();
    //$$     }
    //$$ }
    //$$
    //$$ public static boolean isAdding(Entity entity) {
    //$$     Set<Entity> entities = ADDING.get();
    //$$     return entities != null && entities.contains(entity);
    //$$ }
    //$$
    //$$ public static void clear() {
    //$$     ADDING.remove();
    //$$ }
    //#else
    public static void clear() {
    }
    //#endif
}
