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
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#endif
//#if MC >= 260300
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mutable;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#elseif MC >= 12103
//$$ import net.minecraft.core.HolderLookup;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

/** Version boundary for mutating RecipeManager's loaded recipe map. */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerAccessor implements RecipeManagerBridge {
    //#if MC >= 260300
    //$$ @Shadow @Final @Mutable private RecipeMap recipes;
    //$$ @Override public void carpetDDSAddition$setRecipes(RecipeMap recipes) { this.recipes = recipes; }
    //#elseif MC >= 12103
    //$$ @Override @Accessor("recipes") public abstract void carpetDDSAddition$setRecipes(RecipeMap recipes);
    //$$ @Override @Accessor("registries") public abstract HolderLookup.Provider carpetDDSAddition$getRegistries();
    //#endif
}
