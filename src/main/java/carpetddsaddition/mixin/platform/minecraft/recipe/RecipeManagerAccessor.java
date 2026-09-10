/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.platform.minecraft.recipe;

import carpetddsaddition.platform.minecraft.recipe.RecipeManagerBridge;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12103
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

/** Modern RecipeManager field bridge; older versions keep an empty shell. */
@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor extends RecipeManagerBridge {
    //#if MC >= 12103
    //$$ @Override @Accessor("recipes") void carpetDDSAddition$setRecipes(RecipeMap recipes);
    //$$ @Override @Accessor("registries") HolderLookup.Provider carpetDDSAddition$getRegistries();
    //#endif
}
