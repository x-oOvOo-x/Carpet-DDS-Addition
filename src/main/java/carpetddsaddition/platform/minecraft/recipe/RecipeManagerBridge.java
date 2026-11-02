/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.recipe;

//#if MC >= 12103
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#endif
//#if MC >= 12103 && MC < 260300
//$$ import net.minecraft.core.HolderLookup;
//#endif

/** Duck interface implemented by the RecipeManager accessor Mixin. */
public interface RecipeManagerBridge {
    //#if MC >= 12103
    //$$ void carpetDDSAddition$setRecipes(RecipeMap recipes);
    //#endif
    //#if MC >= 12103 && MC < 260300
    //$$ HolderLookup.Provider carpetDDSAddition$getRegistries();
    //#endif
}
