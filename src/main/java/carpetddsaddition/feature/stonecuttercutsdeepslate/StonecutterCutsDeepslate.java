/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate;

import carpetddsaddition.feature.stonecuttercutsdeepslate.compat.StonecutterCutsDeepslateAvailabilityCompat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;

public final class StonecutterCutsDeepslate {
    private StonecutterCutsDeepslate() {}
    public static boolean enabled(){return StonecutterCutsDeepslateAvailabilityCompat.enabled();}
    public static void onRecipeManagerApplied(RecipeManager manager){StonecutterCutsDeepslateAvailabilityCompat.onRecipeManagerApplied(manager);}
    public static void refreshLegacyLifecycle(MinecraftServer server){StonecutterCutsDeepslateAvailabilityCompat.refreshLegacyLifecycle(server);}
    public static void refresh(MinecraftServer server,boolean force){StonecutterCutsDeepslateAvailabilityCompat.refresh(server,force);}
    public static void reset(){StonecutterCutsDeepslateAvailabilityCompat.reset();}
}
