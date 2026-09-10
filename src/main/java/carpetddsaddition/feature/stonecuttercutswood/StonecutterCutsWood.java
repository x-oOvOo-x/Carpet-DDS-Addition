/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

import carpetddsaddition.core.rules.DdsRuleEvents;
import carpetddsaddition.feature.stonecuttercutswood.compat.StonecutterCutsWoodCompat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;

public final class StonecutterCutsWood {
    private StonecutterCutsWood() {}
    public static DdsRuleEvents.Observer ruleObserver(){return StonecutterCutsWoodCompat.ruleObserver();}
    public static void onRecipeManagerApplied(RecipeManager manager){StonecutterCutsWoodCompat.onRecipeManagerApplied(manager);}
    public static void refresh(MinecraftServer server,boolean force){StonecutterCutsWoodCompat.refresh(server,force);}
    public static void reset(){StonecutterCutsWoodCompat.reset();}
}
