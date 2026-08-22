/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//#if MC >= 12005
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.component.ItemContainerContents;
//#else
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
//#endif
//#endif

/** Quick-storage mechanics and item-backed shulker persistence. */
final class QuickContainerAccessStorage {
    private QuickContainerAccessStorage() {}

    //#if MC >= 11404 && MC <= 260200
    static boolean handleStorageInteraction(ServerPlayer player, AbstractContainerMenu menu, int slotId, int button,
                                            Object clickType, Inventory playerInventory) {
        if (!"PICKUP".equals(QuickContainerAccessSession.enumName(clickType)) || button != 1
                || slotId < 0 || slotId >= menu.slots.size()) return false;
        Slot slot = menu.getSlot(slotId);
        if (slot.container != playerInventory) return false;
        ItemStack target = slot.getItem();
        ItemStack carried = QuickContainerAccessSession.carried(menu, player);
        if (carried.isEmpty()) return false;

        // Cursor storage owns storage-on-storage interaction; check it before target storage.
        StorageHandle carriedStorage = storageFor(player, carried);
        if (carriedStorage != null) {
            if (target.isEmpty()) {
                ItemStack extracted = extractLast(carriedStorage);
                if (extracted.isEmpty()) return true; // consume; never let vanilla place the storage item
                if (!slot.mayPlace(extracted)) {
                    insert(carriedStorage, extracted);
                    return true;
                }
                slot.set(extracted);
                slot.setChanged();
                return true;
            }
            int before = target.getCount();
            insert(carriedStorage, target);
            if (target.getCount() != before) {
                if (target.isEmpty()) slot.set(ItemStack.EMPTY);
                slot.setChanged();
            }
            return true; // full/invalid storage is still a handled QCA click
        }

        StorageHandle targetStorage = storageFor(player, target);
        if (targetStorage != null) {
            int before = carried.getCount();
            insert(targetStorage, carried);
            if (carried.getCount() != before && carried.isEmpty())
                QuickContainerAccessSession.setCarried(menu, player, ItemStack.EMPTY);
            return true;
        }
        return false;
    }

    static Container createPortableShulkerContainer(ItemStack source, int size) { return new PortableShulkerContainer(source, size); }
    static int shulkerSize() { return QuickContainerAccessAmsCompat.largeShulkerBoxEnabled() ? 54 : 27; }

    private static StorageHandle storageFor(ServerPlayer player, ItemStack host) {
        if (host.isEmpty() || host.getCount() != 1) return null;
        if (QuickContainerAccessItems.isShulker(host)) return new StorageHandle(new PortableShulkerContainer(host, shulkerSize()), true);
        if (QuickContainerAccessItems.isItem(host, Items.ENDER_CHEST)) return new StorageHandle(player.getEnderChestInventory(), false);
        return null;
    }

    private static int insert(StorageHandle storage, ItemStack source) {
        if (source.isEmpty() || storage.shulker && QuickContainerAccessItems.isShulker(source)) return 0;
        Container container = storage.container;
        int initial = source.getCount();
        for (int i = 0; i < container.getContainerSize() && !source.isEmpty(); i++) {
            ItemStack existing = container.getItem(i);
            if (existing.isEmpty() || !container.canPlaceItem(i, source)
                    || !QuickContainerAccessItems.sameItemAndData(existing, source)) continue;
            int limit = Math.min(container.getMaxStackSize(), existing.getMaxStackSize());
            int move = Math.min(source.getCount(), limit - existing.getCount());
            if (move > 0) {
                existing.grow(move);
                source.shrink(move);
            }
        }
        for (int i = 0; i < container.getContainerSize() && !source.isEmpty(); i++) {
            if (!container.getItem(i).isEmpty() || !container.canPlaceItem(i, source)) continue;
            int move = Math.min(source.getCount(), Math.min(container.getMaxStackSize(), source.getMaxStackSize()));
            container.setItem(i, copyWithCountCompat(source, move));
            source.shrink(move);
        }
        if (source.getCount() != initial) container.setChanged();
        return initial - source.getCount();
    }

    private static ItemStack copyWithCountCompat(ItemStack source, int count) {
        //#if MC >= 11903
        //$$ return source.copyWithCount(count);
        //#else
        ItemStack copy = source.copy();
        copy.setCount(count);
        return copy;
        //#endif
    }
    private static ItemStack extractLast(StorageHandle storage) {
        Container container = storage.container;
        for (int i = container.getContainerSize() - 1; i >= 0; i--) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            container.setItem(i, ItemStack.EMPTY);
            container.setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private static final class StorageHandle {
        private final Container container;
        private final boolean shulker;
        private StorageHandle(Container container, boolean shulker) {
            this.container = container;
            this.shulker = shulker;
        }
    }

    private static final class PortableShulkerContainer extends SimpleContainer {
        private final ItemStack source;
        private final int size;
        private PortableShulkerContainer(ItemStack source, int size) {
            super(load(source, size));
            this.source = source;
            this.size = size;
        }
        private static ItemStack[] load(ItemStack source, int size) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            //#if MC >= 12005
            //$$ ItemContainerContents contents = source.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            //$$ contents.copyInto(stacks);
            //#else
            CompoundTag blockEntityTag = source.getTagElement("BlockEntityTag");
            if (blockEntityTag != null) ContainerHelper.loadAllItems(blockEntityTag, stacks);
            //#endif
            return stacks.toArray(new ItemStack[0]);
        }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) {
            return !QuickContainerAccessItems.isShulker(stack) && super.canPlaceItem(slot, stack);
        }
        @Override public void setChanged() {
            super.setChanged();
            // Backing source must remain the same singular portable shulker.
            if (source.getCount() != 1) return;
            NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            for (int i = 0; i < getContainerSize(); i++) stacks.set(i, getItem(i));
            //#if MC >= 12005
            //$$ source.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
            //#else
            CompoundTag blockEntityTag = source.getOrCreateTagElement("BlockEntityTag");
            blockEntityTag.remove("Items"); // avoid stale NBT when extracting the final item
            ContainerHelper.saveAllItems(blockEntityTag, stacks);
            //#endif
        }
    }
    //#endif
}
