/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock.compat;

import carpetddsaddition.feature.playershulkerrestock.AutoRestockTransfer;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import net.minecraft.world.item.ItemStack;
import java.util.function.BooleanSupplier;
import java.util.function.BiConsumer;
//#if MC >= 12005
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.BundleItem;
//$$ import net.minecraft.world.item.component.BundleContents;
//#if MC >= 260102
//$$ import net.minecraft.world.item.ItemStackTemplate;
//#endif
//$$ import java.util.ArrayList;
//$$ import java.util.List;
//#endif

/** Extract from one original bundle stack; never merge two source stacks or mutate an uncommitted source. */
public final class PlayerShulkerRestockBundleCompat {
    private PlayerShulkerRestockBundleCompat() {}

    private static final int MAX_BUNDLE_DEPTH = 16;

    public static boolean withdraw(ItemStack bundle, ItemStack expected, int room, BooleanSupplier valid,
                                   BiConsumer<ItemStack, Integer> apply) {
    //#if MC >= 12005
    //$$ Extraction extracted = extract(bundle, expected, room, 0);
    //$$ return extracted != null && AutoRestockTransfer.commit(room, extracted.moved, valid,
    //$$         moved -> apply.accept(extracted.updated, moved));
    //#else
        return false;
    //#endif
    }

    //#if MC >= 12005
    //$$ private static Extraction extract(ItemStack bundle, ItemStack expected, int room, int depth) {
    //$$     if (depth >= MAX_BUNDLE_DEPTH || bundle.isEmpty() || bundle.getCount() != 1
    //$$             || !(bundle.getItem() instanceof BundleItem)) return null;
    //$$     BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
    //$$     if (contents == null || contents.isEmpty()) return null;
    //$$     List<ItemStack> items = new ArrayList<>();
    //#if MC >= 260102
    //$$     for (ItemStackTemplate template : contents.items()) {
    //$$         ItemStack item = template.create();
    //$$         if (item.isEmpty()) return null; // Leave malformed containers untouched.
    //$$         items.add(item);
    //$$     }
    //#else
    //$$     for (ItemStack item : contents.items()) items.add(item.copy());
    //#endif
    //$$     for (int index = 0; index < items.size(); index++) {
    //$$         ItemStack source = items.get(index);
    //$$         if (source.isEmpty()) continue;
    //$$         if (QuickContainerAccessItems.sameItemAndData(source, expected)) {
    //$$             int moved = Math.min(room, source.getCount());
    //$$             if (moved <= 0) continue;
    //$$             int remaining = source.getCount() - moved;
    //$$             if (remaining == 0) items.remove(index);
    //$$             else items.set(index, source.copyWithCount(remaining));
    //$$             return rebuilt(bundle, items, moved);
    //$$         }
    //$$         Extraction nested = extract(source, expected, room, depth + 1);
    //$$         if (nested != null) {
    //$$             items.set(index, nested.updated);
    //$$             return rebuilt(bundle, items, nested.moved);
    //$$         }
    //$$     }
    //$$     return null;
    //$$ }
    //$$
    //$$ private static Extraction rebuilt(ItemStack original, List<ItemStack> contents, int moved) {
    //$$     ItemStack updated = original.copy();
    //#if MC >= 260102
    //$$     List<ItemStackTemplate> templates = new ArrayList<>();
    //$$     for (ItemStack item : contents) templates.add(ItemStackTemplate.fromNonEmptyStack(item));
    //$$     updated.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(templates));
    //#else
    //$$     updated.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(contents));
    //#endif
    //$$     return new Extraction(updated, moved);
    //$$ }
    //$$
    //$$ private static final class Extraction {
    //$$     final ItemStack updated;
    //$$     final int moved;
    //$$     Extraction(ItemStack updated, int moved) {
    //$$         this.updated = updated;
    //$$         this.moved = moved;
    //$$     }
    //$$ }
    //#endif

}
