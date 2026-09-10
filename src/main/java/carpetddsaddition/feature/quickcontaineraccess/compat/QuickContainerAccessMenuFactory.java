/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import net.minecraft.world.entity.player.Player;
//#if MC >= 11404 && MC <= 260200
import carpetddsaddition.core.inventory.DdsEnderMenuFactory;
import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//#if MC >= 12005
//$$ import net.minecraft.core.component.DataComponents;
//#endif
import net.minecraft.network.chat.Component;
//#if MC < 11900
import net.minecraft.network.chat.TranslatableComponent;
//#endif
//#if MC < 11601
import net.minecraft.core.BlockPos;
//#endif
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
//#if MC >= 11502
import net.minecraft.world.inventory.CartographyTableMenu;
//#endif
//#if MC >= 11601
//$$ import net.minecraft.world.inventory.SmithingMenu;
//#endif
//#endif

/** Constructs portable menus and resolves their client-visible titles; session ownership remains separate. */
final class QuickContainerAccessMenuFactory {
    private QuickContainerAccessMenuFactory() {}

    //#if MC >= 11404 && MC <= 260200
    static AbstractContainerMenu create(int containerId, Inventory inventory, ServerPlayer viewer, ServerPlayer owner,
                                        ItemStack source, QuickContainerAccessItems.MenuKind kind, int openedSize,
                                        DdsEnderMenuFactory enderMenuFactory) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) return createShulkerMenu(containerId, inventory, source, openedSize);
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
            if (enderMenuFactory != null) {
                AbstractContainerMenu enhanced = enderMenuFactory.ddsCreateEnderMenu(containerId, inventory, viewer);
                if (enhanced != null) return enhanced;
            }
            return createEnderChestMenu(containerId, inventory, owner, openedSize);
        }
        ContainerLevelAccess access = createLevelAccess(viewer);
        if (kind == QuickContainerAccessItems.MenuKind.CRAFTING_TABLE) return new PortableCraftingMenu(containerId, inventory, access);
        if (kind == QuickContainerAccessItems.MenuKind.STONECUTTER) return new PortableStonecutterMenu(containerId, inventory, access);
        if (kind == QuickContainerAccessItems.MenuKind.ANVIL) return new PortableAnvilMenu(containerId, inventory, access);
        if (kind == QuickContainerAccessItems.MenuKind.LOOM) return new PortableLoomMenu(containerId, inventory, access);
        //#if MC >= 11502
        if (kind == QuickContainerAccessItems.MenuKind.CARTOGRAPHY_TABLE) return new PortableCartographyTableMenu(containerId, inventory, access);
        //#endif
        if (kind == QuickContainerAccessItems.MenuKind.GRINDSTONE) return new PortableGrindstoneMenu(containerId, inventory, access);
        //#if MC >= 11601
        //$$ if (kind == QuickContainerAccessItems.MenuKind.SMITHING_TABLE) return new PortableSmithingMenu(containerId, inventory, access);
        //#endif
        if (kind == QuickContainerAccessItems.MenuKind.ENCHANTING_TABLE) return new PortableEnchantmentMenu(containerId, inventory, access);
        return null;
    }

    static Container openedContainer(AbstractContainerMenu menu, ServerPlayer owner, QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) return owner.getEnderChestInventory();
        return kind == QuickContainerAccessItems.MenuKind.SHULKER && !menu.slots.isEmpty() ? menu.getSlot(0).container : null;
    }

    private static AbstractContainerMenu createShulkerMenu(int containerId, Inventory inventory, ItemStack source, int openedSize) {
        Container container = QuickContainerAccessStorage.createPortableShulkerContainer(source, openedSize);
        if (container == null) return null;
        if (openedSize == 27) return new ShulkerBoxMenu(containerId, inventory, container);
        if (openedSize == 54) return new PortableLargeShulkerMenu(containerId, inventory, container);
        return null;
    }
    private static AbstractContainerMenu createEnderChestMenu(int containerId, Inventory inventory, ServerPlayer owner, int openedSize) {
        Container canonical = owner.getEnderChestInventory();
        if (canonical.getContainerSize() != openedSize) return null;
        Container portable = new PortableEnderContainer(owner, canonical);
        if (openedSize == 54) return ChestMenu.sixRows(containerId, inventory, portable);
        if (openedSize == 27) return ChestMenu.threeRows(containerId, inventory, portable);
        return null;
    }

    private static ContainerLevelAccess createLevelAccess(ServerPlayer player) {
        //#if MC >= 12001
        //$$ return ContainerLevelAccess.create(player.serverLevel(), player.blockPosition());
        //#else
        //#if MC >= 11601
        //$$ return ContainerLevelAccess.create(player.level, player.blockPosition());
        //#else
        //#if MC >= 11502
        return ContainerLevelAccess.create(player.level, new BlockPos(player.getX(), player.getY(), player.getZ()));
        //#else
        //$$ return ContainerLevelAccess.create(player.level, new BlockPos(player.x, player.y, player.z));
        //#endif
        //#endif
        //#endif
    }

    static Component titleFor(ItemStack source, QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) {
            //#if MC >= 12005
            //$$ if (source.has(DataComponents.CUSTOM_NAME)) return source.getHoverName();
            //#else
            if (source.hasCustomHoverName()) return source.getHoverName();
            //#endif
            //#if MC >= 11900
            //$$ return Component.translatable("container.shulkerBox");
            //#else
            return new TranslatableComponent("container.shulkerBox");
            //#endif
        }
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
            //#if MC >= 11900
            //$$ return Component.translatable("container.enderchest");
            //#else
            return new TranslatableComponent("container.enderchest");
            //#endif
        }
        if (kind == QuickContainerAccessItems.MenuKind.CRAFTING_TABLE) {
            //#if MC >= 11900
            //$$ return Component.translatable("container.crafting");
            //#else
            return new TranslatableComponent("container.crafting");
            //#endif
        }
        if (kind == QuickContainerAccessItems.MenuKind.STONECUTTER) {
            //#if MC >= 11900
            //$$ return Component.translatable("container.stonecutter");
            //#else
            return new TranslatableComponent("container.stonecutter");
            //#endif
        }
        if (kind == QuickContainerAccessItems.MenuKind.ANVIL) {
            //#if MC >= 11900
            //$$ return Component.translatable("container.repair");
            //#else
            return new TranslatableComponent("container.repair");
            //#endif
        }
        return source.getHoverName();
    }

    private static final class PortableEnderContainer implements Container, DdsInventoryOwnerView {
        private final ServerPlayer owner; private final Container delegate;
        private PortableEnderContainer(ServerPlayer owner, Container delegate) { this.owner = owner; this.delegate = delegate; }
        @Override public int getContainerSize() { return delegate.getContainerSize(); }
        @Override public boolean isEmpty() { return delegate.isEmpty(); }
        @Override public ItemStack getItem(int slot) { return delegate.getItem(slot); }
        @Override public ItemStack removeItem(int slot, int count) { return delegate.removeItem(slot, count); }
        @Override public ItemStack removeItemNoUpdate(int slot) { return delegate.removeItemNoUpdate(slot); }
        @Override public void setItem(int slot, ItemStack stack) { delegate.setItem(slot, stack); }
        @Override public int getMaxStackSize() { return delegate.getMaxStackSize(); }
        //#if MC >= 12006
        //$$ @Override public int getMaxStackSize(ItemStack stack) { return delegate.getMaxStackSize(stack); }
        //#endif
        @Override public void setChanged() { delegate.setChanged(); }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public boolean canPlaceItem(int slot, ItemStack stack) { return delegate.canPlaceItem(slot, stack); }
        @Override public void clearContent() { delegate.clearContent(); }
        @Override public ServerPlayer ddsInventoryOwner() { return owner; }
        @Override public Container ddsCanonicalContainer() { return delegate; }
    }

    private static final class PortableLargeShulkerMenu extends ChestMenu {
        private PortableLargeShulkerMenu(int id, Inventory inventory, Container container) {
            super(MenuType.GENERIC_9x6, id, inventory, container, 6);
        }
        @Override protected Slot addSlot(Slot slot) {
            if (!(slot.container instanceof Inventory)) {
                int containerSlot = QuickContainerAccessSession.containerSlot(slot);
                return super.addSlot(new ContainerRestrictedSlot(slot.container, containerSlot, slot.x, slot.y));
            }
            return super.addSlot(slot);
        }
        @Override public boolean stillValid(Player player) { return true; }
    }

    private static final class ContainerRestrictedSlot extends Slot {
        private final int containerSlot;
        private ContainerRestrictedSlot(Container container, int containerSlot, int x, int y) {
            super(container, containerSlot, x, y); this.containerSlot = containerSlot;
        }
        @Override public boolean mayPlace(ItemStack stack) { return container.canPlaceItem(containerSlot, stack) && super.mayPlace(stack); }
    }

    private static final class PortableCraftingMenu extends CraftingMenu {
        private PortableCraftingMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    private static final class PortableStonecutterMenu extends StonecutterMenu {
        private PortableStonecutterMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    private static final class PortableAnvilMenu extends AnvilMenu {
        private PortableAnvilMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    private static final class PortableLoomMenu extends LoomMenu {
        private PortableLoomMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    //#if MC >= 11502
    private static final class PortableCartographyTableMenu extends CartographyTableMenu {
        private PortableCartographyTableMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    //#endif
    private static final class PortableGrindstoneMenu extends GrindstoneMenu {
        private PortableGrindstoneMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    //#if MC >= 11601
    //$$ private static final class PortableSmithingMenu extends SmithingMenu {
    //$$     private PortableSmithingMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
    //$$     @Override public boolean stillValid(Player player) { return true; }
    //$$ }
    //#endif
    private static final class PortableEnchantmentMenu extends EnchantmentMenu {
        private PortableEnchantmentMenu(int id, Inventory inventory, ContainerLevelAccess access) { super(id, inventory, access); }
        @Override public boolean stillValid(Player player) { return true; }
    }
    //#endif
}
