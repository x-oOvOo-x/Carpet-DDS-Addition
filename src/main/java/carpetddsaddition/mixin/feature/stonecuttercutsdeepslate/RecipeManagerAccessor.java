/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.stonecuttercutsdeepslate;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12103
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

/** Modern RecipeManager recipes-field bridge; older versions keep an empty shell. */
@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    //#if MC >= 12103
    //$$ @Accessor("recipes")
    //$$ void carpetDDSAddition$setRecipes(RecipeMap recipes);
    //#endif
}
