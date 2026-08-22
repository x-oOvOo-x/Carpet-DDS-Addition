/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import net.minecraft.world.entity.player.Player;
//#if MC >= 11404 && MC <= 260200
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
    static AbstractContainerMenu create(int containerId, Inventory inventory, ServerPlayer player, ItemStack source,
                                        QuickContainerAccessItems.MenuKind kind, int openedSize) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) return createShulkerMenu(containerId, inventory, source, openedSize);
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) return createEnderChestMenu(containerId, inventory, player, openedSize);
        ContainerLevelAccess access = createLevelAccess(player);
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

    private static AbstractContainerMenu createShulkerMenu(int containerId, Inventory inventory, ItemStack source, int openedSize) {
        Container container = QuickContainerAccessStorage.createPortableShulkerContainer(source, openedSize);
        if (openedSize == 27) return new ShulkerBoxMenu(containerId, inventory, container);
        /* AMS 54-slot compatibility deliberately exposes GENERIC_9x6 while replacing storage slots with restricted slots. */
        if (openedSize == 54) return new PortableLargeShulkerMenu(containerId, inventory, container);
        return null;
    }
    private static AbstractContainerMenu createEnderChestMenu(int containerId, Inventory inventory, ServerPlayer player, int openedSize) {
        Container enderChest = player.getEnderChestInventory();
        if (openedSize == 54) return ChestMenu.sixRows(containerId, inventory, enderChest);
        if (openedSize == 27) return ChestMenu.threeRows(containerId, inventory, enderChest);
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

    /** GENERIC_9x6 protocol with shulker insertion restrictions preserved independently of AMS' ShulkerBoxMenu mixin. */
    private static final class PortableLargeShulkerMenu extends ChestMenu {
        private PortableLargeShulkerMenu(int id, Inventory inventory, Container container) {
            super(MenuType.GENERIC_9x6, id, inventory, container, 6);
        }
        @Override protected Slot addSlot(Slot slot) {
            // ChestMenu constructor dispatches here; player Inventory slots remain ordinary slots.
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
            super(container, containerSlot, x, y);
            this.containerSlot = containerSlot;
        }
        @Override public boolean mayPlace(ItemStack stack) {
            return container.canPlaceItem(containerSlot, stack) && super.mayPlace(stack);
        }
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
