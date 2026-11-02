/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock.compat;

import carpetddsaddition.feature.playershulkerrestock.AutoRestockTransfer;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessLease;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
//#if MC >= 11404 && MC <= 260300
import net.minecraft.core.NonNullList;
//#if MC >= 12005
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.component.ItemContainerContents;
//#else
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
//#endif
//#endif

/** Server-authoritative refill using one source stack, with carried inventory preferred. */
public final class PlayerShulkerRestockStorage {
    private PlayerShulkerRestockStorage() {}

    public static boolean restock(ServerPlayer player, int targetSlot, ItemStack expected, int observedCount) {
        //#if MC >= 11404 && MC <= 260300
        Inventory inv = PlayerShulkerRestockCompat.inventory(player);
        if (targetSlot < 0 || targetSlot >= inv.getContainerSize() || !player.isAlive()
                || player.connection == null || player.containerMenu == null
                || !targetMatches(inv, targetSlot, expected, observedCount)) return false;
        int room = expected.getMaxStackSize() - observedCount;
        if (room <= 0) return false;
        int capacity = QuickContainerAccessLease.shulkerCapacity();
        if (fromLoose(player, inv, inv, 36, targetSlot, expected, observedCount, room)) return true;
        if (fromBundles(player, inv, inv, 36, targetSlot, expected, observedCount, room)) return true;
        if (fromShulkers(player, inv, inv, 36, targetSlot, expected, observedCount, room, capacity)) return true;
        if (fromShulkerBundles(player, inv, inv, 36, targetSlot, expected, observedCount, room, capacity))
            return true;
        Container ender = player.getEnderChestInventory();
        if (fromLoose(player, inv, ender, ender.getContainerSize(), targetSlot, expected, observedCount, room))
            return true;
        if (fromBundles(player, inv, ender, ender.getContainerSize(), targetSlot, expected, observedCount, room))
            return true;
        if (fromShulkers(player, inv, ender, ender.getContainerSize(),
                targetSlot, expected, observedCount, room, capacity)) return true;
        return fromShulkerBundles(player, inv, ender, ender.getContainerSize(),
                targetSlot, expected, observedCount, room, capacity);
        //#else
        //$$ return false;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260300
    private static boolean fromLoose(ServerPlayer player, Inventory inv, Container storage, int size,
                                     int targetSlot, ItemStack expected, int observedCount, int room) {
        for (int index = 0; index < size; index++) {
            // For the carried inventory, use main storage (9-35) before other hotbar slots (0-8).
            int slot = storage == inv ? (index + 9) % 36 : index;
            if (storage == inv && slot == targetSlot) continue;
            ItemStack source = storage.getItem(slot);
            if (source.isEmpty() || QuickContainerAccessLease.isHeldByAnyone(source)
                    || !QuickContainerAccessItems.sameItemAndData(source, expected)) continue;
            if (AutoRestockTransfer.commit(room, source.getCount(),
                    () -> storage.getItem(slot) == source && !QuickContainerAccessLease.isHeldByAnyone(source)
                            && targetMatches(inv, targetSlot, expected, observedCount),
                    moved -> {
                        ItemStack remainder = copyCount(source, source.getCount() - moved);
                        storage.setItem(slot, remainder.isEmpty() ? ItemStack.EMPTY : remainder);
                        finish(player, inv, storage, targetSlot, expected, observedCount + moved);
                    })) return true;
        }
        return false;
    }

    /** Bundle sources are considered after loose stacks, before unopened shulker boxes. */
    private static boolean fromBundles(ServerPlayer player, Inventory inv, Container storage, int size,
                                       int targetSlot, ItemStack expected, int observedCount, int room) {
        for (int index = 0; index < size; index++) {
            int slot = storage == inv ? (index + 9) % 36 : index;
            if (storage == inv && slot == targetSlot) continue;
            ItemStack bundle = storage.getItem(slot);
            if (bundle.isEmpty() || QuickContainerAccessLease.isHeldByAnyone(bundle)) continue;
            final int sourceSlot = slot;
            if (PlayerShulkerRestockBundleCompat.withdraw(bundle, expected, room,
                    () -> storage.getItem(sourceSlot) == bundle
                            && !QuickContainerAccessLease.isHeldByAnyone(bundle)
                            && targetMatches(inv, targetSlot, expected, observedCount),
                    (updated, moved) -> {
                        storage.setItem(sourceSlot, updated);
                        finish(player, inv, storage, targetSlot, expected, observedCount + moved);
                    })) return true;
        }
        return false;
    }

    /** One-level bundle-in-shulker lookup, with the same AMS overflow/lease guards. */
    private static boolean fromShulkerBundles(ServerPlayer player, Inventory inv, Container storage, int size,
                                              int targetSlot, ItemStack expected, int observedCount,
                                              int room, int capacity) {
    //#if MC >= 12005
    //$$ if (capacity != 27 && capacity != 54) return false;
    //$$ for (int boxSlot = 0; boxSlot < size; boxSlot++) {
    //$$     if (storage == inv && boxSlot == targetSlot) continue;
    //$$     ItemStack box = storage.getItem(boxSlot);
    //$$     if (box.isEmpty() || box.getCount() != 1 || !QuickContainerAccessItems.isShulker(box)
    //$$             || QuickContainerAccessLease.isHeldByAnyone(box)) continue;
    //$$     NonNullList<ItemStack> contents = NonNullList.withSize(54, ItemStack.EMPTY);
    //$$     box.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
    //$$     if (capacity < 54 && overflow(contents, capacity)) continue;
    //$$     for (int sourceSlot = 0; sourceSlot < capacity; sourceSlot++) {
    //$$         ItemStack bundle = contents.get(sourceSlot);
    //$$         if (bundle.isEmpty() || QuickContainerAccessLease.isHeldByAnyone(bundle)) continue;
    //$$         final int containerSlot = boxSlot, itemSlot = sourceSlot;
    //$$         if (PlayerShulkerRestockBundleCompat.withdraw(bundle, expected, room,
    //$$                 () -> storage.getItem(containerSlot) == box
    //$$                         && contents.get(itemSlot) == bundle
    //$$                         && !QuickContainerAccessLease.isHeldByAnyone(box)
    //$$                         && !QuickContainerAccessLease.isHeldByAnyone(bundle)
    //$$                         && targetMatches(inv, targetSlot, expected, observedCount),
    //$$                 (updated, moved) -> {
    //$$                     contents.set(itemSlot, updated);
    //$$                     box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
    //$$                     finish(player, inv, storage, targetSlot, expected, observedCount + moved);
    //$$                 })) return true;
    //$$     }
    //$$ }
    //#endif
        return false;
    }

    private static boolean fromShulkers(ServerPlayer player, Inventory inv, Container storage, int size,
                                         int targetSlot, ItemStack expected, int observedCount,
                                         int room, int capacity) {
        // QCA's AMS detector returns 0 when an installed AMS capacity is unknown.
        if (capacity != 27 && capacity != 54) return false;
        for (int boxSlot = 0; boxSlot < size; boxSlot++) {
            if (storage == inv && boxSlot == targetSlot) continue;
            ItemStack box = storage.getItem(boxSlot);
            if (box.isEmpty() || box.getCount() != 1 || !QuickContainerAccessItems.isShulker(box)
                    || QuickContainerAccessLease.isHeldByAnyone(box)) continue;
            NonNullList<ItemStack> contents = NonNullList.withSize(54, ItemStack.EMPTY);
    //#if MC >= 12005
    //$$             box.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
        //#else
                CompoundTag blockEntityTag = box.getTagElement("BlockEntityTag");
                if (blockEntityTag != null) ContainerHelper.loadAllItems(blockEntityTag, contents);
        //#endif
            if (capacity < 54 && overflow(contents, capacity)) continue;
            for (int sourceSlot = 0; sourceSlot < capacity; sourceSlot++) {
                ItemStack source = contents.get(sourceSlot);
                if (source.isEmpty() || !QuickContainerAccessItems.sameItemAndData(source, expected)) continue;
                final int containerSlot = boxSlot;
                final int itemSlot = sourceSlot;
                if (AutoRestockTransfer.commit(room, source.getCount(),
                        () -> storage.getItem(containerSlot) == box && !QuickContainerAccessLease.isHeldByAnyone(box)
                                && targetMatches(inv, targetSlot, expected, observedCount),
                        moved -> {
                            ItemStack remainder = copyCount(source, source.getCount() - moved);
                            contents.set(itemSlot, remainder.isEmpty() ? ItemStack.EMPTY : remainder);
    //#if MC >= 12005
    //$$                                             box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
                        //#else
                                                CompoundTag updatedTag = box.getOrCreateTagElement("BlockEntityTag");
                                                updatedTag.remove("Items");
                                                ContainerHelper.saveAllItems(updatedTag, contents);
                        //#endif
                            finish(player, inv, storage, targetSlot, expected, observedCount + moved);
                        })) return true;
            }
        }
        return false;
    }

    private static void finish(ServerPlayer player, Inventory inv, Container source, int targetSlot,
                               ItemStack expected, int newCount) {
        inv.setItem(targetSlot, copyCount(expected, newCount));
        inv.setChanged();
        if (source != inv) source.setChanged();
        player.inventoryMenu.broadcastChanges();
        if (player.containerMenu != player.inventoryMenu) player.containerMenu.broadcastChanges();
    }

    private static boolean targetMatches(Inventory inv, int slot, ItemStack expected, int observedCount) {
        ItemStack target = inv.getItem(slot);
        return target.isEmpty() ? observedCount == 0
                : target.getCount() == observedCount && QuickContainerAccessItems.sameItemAndData(target, expected);
    }

    private static boolean overflow(NonNullList<ItemStack> contents, int capacity) {
        for (int i = capacity; i < contents.size(); i++) if (!contents.get(i).isEmpty()) return true;
        return false;
    }

    private static ItemStack copyCount(ItemStack source, int count) {
    //#if MC >= 11903
    //$$     return source.copyWithCount(count);
    //#else
        ItemStack copy = source.copy();
        copy.setCount(count);
        return copy;
    //#endif
    }
    //#endif
}
