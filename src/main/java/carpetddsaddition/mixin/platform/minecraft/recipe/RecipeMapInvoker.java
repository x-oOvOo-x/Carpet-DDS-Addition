/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.platform.minecraft.recipe;

//#if MC >= 260300
//$$ import com.google.common.collect.Multimap;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.world.item.crafting.Recipe;
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//$$ import net.minecraft.world.item.crafting.RecipeType;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//$$ import java.util.Map;
//$$
//$$ @Mixin(RecipeMap.class)
//$$ public interface RecipeMapInvoker {
//$$     @Invoker("<init>")
//$$     static RecipeMap carpetDDSAddition$create(
//$$             Multimap<RecipeType<?>, RecipeHolder<?>> byType,
//$$             Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey) {
//$$         throw new AssertionError("Mixin constructor invoker was not transformed");
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.RecipeMapTarget")
public interface RecipeMapInvoker {}
//#endif
