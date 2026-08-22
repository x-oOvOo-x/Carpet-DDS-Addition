/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.feature.enhancednametag.compat;

//#if MC >= 11904
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//#endif
//#if MC >= 12005 && MC < 12104
//$$ import net.minecraft.core.component.DataComponents;
//#endif

/** Minecraft-version API boundary for enhancedNameTag (1.19.4+). */
public final class EnhancedNameTagCompat {
    private EnhancedNameTagCompat() {}

    //#if MC >= 12104
    //$$ public static Component getCustomName(ItemStack stack) { return stack.getCustomName(); }
    //#endif
    //#if MC >= 12005 && MC < 12104
    //$$ public static Component getCustomName(ItemStack stack) { return stack.get(DataComponents.CUSTOM_NAME); }
    //#endif
    //#if MC >= 11904 && MC < 12005
    //$$ public static Component getCustomName(ItemStack stack) { return stack.hasCustomHoverName() ? stack.getHoverName() : null; }
    //#endif
    //#if MC >= 11904
    //$$ public static boolean hasCustomName(ItemStack stack) { return getCustomName(stack) != null; }
    //$$ public static String getCustomNameString(ItemStack stack) {
    //$$     Component name = getCustomName(stack);
    //$$     return name == null ? null : name.getString();
    //$$ }
    //#endif

    //#if MC >= 12000
    //$$ public static Level getLevel(Entity entity) { return entity.level(); }
    //#endif
    //#if MC >= 11904 && MC < 12000
    //$$ public static Level getLevel(Entity entity) { return entity.getLevel(); }
    //#endif

    //#if MC >= 260000
    //$$ public static String getDimensionId(Level level) { return level.dimension().identifier().toString(); }
    //#elseif MC >= 11904
    //$$ public static String getDimensionId(Level level) { return level.dimension().location().toString(); }
    //#endif
}
