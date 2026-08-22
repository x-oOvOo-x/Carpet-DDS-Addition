/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//#if MC >= 11404 && MC <= 260200
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
//#endif

/** Shared, side-safe classification of vanilla quick-container targets. */
public final class QuickContainerAccessItems {
    private QuickContainerAccessItems() {}

    public enum MenuKind { SHULKER, ENDER_CHEST, CRAFTING_TABLE, STONECUTTER, ANVIL, LOOM, CARTOGRAPHY_TABLE, GRINDSTONE, SMITHING_TABLE, ENCHANTING_TABLE }

    public static MenuKind kindOf(ItemStack stack) {
        //#if MC >= 11404 && MC <= 260200
        if (stack.isEmpty()) return null;
        if (isShulker(stack)) return MenuKind.SHULKER;
        if (isItem(stack, Items.ENDER_CHEST)) return MenuKind.ENDER_CHEST;
        if (isItem(stack, Items.CRAFTING_TABLE)) return MenuKind.CRAFTING_TABLE;
        if (isItem(stack, Items.STONECUTTER)) return MenuKind.STONECUTTER;
        //#if MC >= 11601
        //$$ if (isAnvil(stack)) return MenuKind.ANVIL;
        //#endif
        if (isItem(stack, Items.LOOM)) return MenuKind.LOOM;
        //#if MC >= 11502
        if (isItem(stack, Items.CARTOGRAPHY_TABLE)) return MenuKind.CARTOGRAPHY_TABLE;
        //#endif
        if (isItem(stack, Items.GRINDSTONE)) return MenuKind.GRINDSTONE;
        //#if MC >= 11601
        //$$ if (isItem(stack, Items.SMITHING_TABLE)) return MenuKind.SMITHING_TABLE;
        //#endif
        if (isItem(stack, Items.ENCHANTING_TABLE)) return MenuKind.ENCHANTING_TABLE;
        //#endif
        return null;
    }

    public static boolean canOpenInHand(MenuKind kind, ItemStack stack) {
        //#if MC >= 11404 && MC <= 260200
        return kind != null && (kind != MenuKind.SHULKER && kind != MenuKind.ANVIL || stack.getCount() == 1);
        //#else
        //$$ return false;
        //#endif
    }
    public static boolean isStorageHostCandidate(ItemStack stack) {
        //#if MC >= 11404 && MC <= 260200
        return !stack.isEmpty() && stack.getCount() == 1 && (isShulker(stack) || isItem(stack, Items.ENDER_CHEST));
        //#else
        //$$ return false;
        //#endif
    }
    static boolean isItem(ItemStack stack, Item item) {
        //#if MC >= 11701
        //$$ return stack.is(item);
        //#else
        return stack.getItem() == item;
        //#endif
    }
    static boolean isAnvil(ItemStack stack) {
        //#if MC >= 11701
        //$$ return stack.is(ItemTags.ANVIL);
        //#else
        return stack.getItem().is(ItemTags.ANVIL);
        //#endif
    }
    public static boolean isShulker(ItemStack stack) {
        //#if MC >= 11404 && MC <= 260200
        return stack.getItem() instanceof BlockItem && Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock;
        //#else
        //$$ return false;
        //#endif
    }
    public static boolean sameItemAndData(ItemStack first, ItemStack second) {
        //#if MC >= 12005
        //$$ return ItemStack.isSameItemSameComponents(first, second);
        //#else
        //#if MC >= 11701
        //$$ return ItemStack.isSameItemSameTags(first, second);
        //#else
        return first.sameItem(second) && ItemStack.tagMatches(first, second);
        //#endif
        //#endif
    }
}
