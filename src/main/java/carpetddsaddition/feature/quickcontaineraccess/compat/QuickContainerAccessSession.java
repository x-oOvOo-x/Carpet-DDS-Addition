/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;

//#if MC >= 11404 && MC <= 260200
import carpet.patches.EntityPlayerMPFake;
import carpetddsaddition.mixin.feature.quickcontaineraccess.SlotAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.IdentityHashMap;
import java.util.Map;
//#endif

final class QuickContainerAccessSession {
    private QuickContainerAccessSession() {}

    //#if MC >= 11404 && MC <= 260200
    private static final Map<AbstractContainerMenu, Session> SESSIONS_BY_MENU = new IdentityHashMap<>();
    private static final Map<Inventory, Session> SESSIONS_BY_INVENTORY = new IdentityHashMap<>();

    static Inventory inventory(Player player) {
        //#if MC >= 11701
        //$$ return player.getInventory();
        //#else
        return player.inventory;
        //#endif
    }
    static int selectedSlot(Inventory inventory) {
        //#if MC >= 12105
        //$$ return inventory.getSelectedSlot();
        //#else
        return inventory.selected;
        //#endif
    }
    static int offhandSlot() {
        //#if MC >= 11701
        //$$ return Inventory.SLOT_OFFHAND;
        //#else
        return 40;
        //#endif
    }
    static ItemStack carried(AbstractContainerMenu menu, Player player) {
        //#if MC >= 11701
        //$$ return menu.getCarried();
        //#else
        return inventory(player).getCarried();
        //#endif
    }
    static void setCarried(AbstractContainerMenu menu, Player player, ItemStack stack) {
        //#if MC >= 11701
        //$$ menu.setCarried(stack);
        //#else
        inventory(player).setCarried(stack);
        //#endif
    }
    static int containerSlot(Slot slot) {
        //#if MC >= 11701
        //$$ return slot.getContainerSlot();
        //#else
        return ((SlotAccessor) (Object) slot).carpetDdsAddition$getContainerSlot();
        //#endif
    }
    static void syncMenu(ServerPlayer player, AbstractContainerMenu menu) {
        //#if MC >= 11701
        //$$ menu.sendAllDataToRemote();
        //#else
        player.refreshContainer(menu);
        player.broadcastCarriedItem();
        //#endif
    }
    static void syncInventoryMenu(ServerPlayer player) {
        //#if MC >= 11701
        //$$ player.inventoryMenu.broadcastFullState();
        //#else
        player.refreshContainer(player.inventoryMenu);
        player.broadcastCarriedItem();
        //#endif
    }

    static Session get(AbstractContainerMenu menu) { return SESSIONS_BY_MENU.get(menu); }
    static void begin(ServerPlayer player, AbstractContainerMenu menu, SourceRef sourceRef,
                      QuickContainerAccessItems.MenuKind kind, int openedSize) {
        Inventory playerInventory = inventory(player);
        Session previous = SESSIONS_BY_INVENTORY.get(playerInventory);
        if (previous != null) end(previous);
        Session session = new Session(player, playerInventory, menu, sourceRef, kind, openedSize);
        SESSIONS_BY_MENU.put(menu, session);
        SESSIONS_BY_INVENTORY.put(playerInventory, session);
    }
    static Session finish(AbstractContainerMenu menu, Player player) {
        Session session = SESSIONS_BY_MENU.get(menu);
        if (session == null || session.player != player) return null;
        end(session);
        return session;
    }
    static void closeAllActiveMenus() {
        // Closing normally reaches AbstractContainerMenu#removed; snapshot because that callback mutates these maps.
        Session[] sessions = SESSIONS_BY_MENU.values().toArray(new Session[0]);
        for (Session session : sessions) {
            if (session.player.containerMenu == session.menu) session.player.closeContainer();
            else end(session);
        }
        // Safety net for third-party menus that skip the normal removed callback.
        clearAll();
    }
    static void clearAll() {
        SESSIONS_BY_MENU.clear();
        SESSIONS_BY_INVENTORY.clear();
    }
    private static void end(Session session) {
        SESSIONS_BY_MENU.remove(session.menu);
        if (SESSIONS_BY_INVENTORY.get(session.playerInventory) == session) SESSIONS_BY_INVENTORY.remove(session.playerInventory);
    }

    static SourceRef rootRef(ServerPlayer player, Container container, int slot, ItemStack expected) {
        return new SourceRef(player, container, slot, expected);
    }
    static SourceRef sourceRefForActiveClick(Session active, Slot clicked, ItemStack target,
                                             QuickContainerAccessItems.MenuKind nextKind) {
        if (nextKind == null) return null;
        if (clicked.container == active.playerInventory)
            return rootRef(active.player, active.playerInventory, containerSlot(clicked), target);
        // Deliberate nested-storage exception: own Ender Chest -> Shulker Box.
        if (active.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && clicked.container == active.player.getEnderChestInventory()
                && nextKind == QuickContainerAccessItems.MenuKind.SHULKER)
            return rootRef(active.player, active.player.getEnderChestInventory(), containerSlot(clicked), target);
        return null;
    }

    static boolean isPickupAllSourceCollect(AbstractContainerMenu menu, Object clickType, Session session) {
        // QUICK_MOVE moves only the clicked slot; SourceRef#isProtectedMenuSlot handles it. PICKUP_ALL scans the menu.
        if (!"PICKUP_ALL".equals(enumName(clickType))) return false;
        ItemStack carried = carried(menu, session.player);
        return !carried.isEmpty() && session.sourceRef.isPresentInMenu(menu) && session.sourceRef.matchesProtectedItem(carried);
    }
    static String enumName(Object value) { return value instanceof Enum<?> ? ((Enum<?>) value).name() : String.valueOf(value); }

    static final class SourceRef {
        final ServerPlayer player;
        final Container container;
        final int slot;
        /* Identity is intentional: portable mutable sources must keep writing to the exact ItemStack object opened. */
        private ItemStack expectedIdentity;

        private SourceRef(ServerPlayer player, Container container, int slot, ItemStack expected) {
            this.player = player;
            this.container = container;
            this.slot = slot;
            expectedIdentity = expected;
        }
        ItemStack get() { return hasValidContainerSlot() ? container.getItem(slot) : ItemStack.EMPTY; }
        boolean valid() {
            if (!hasValidContainerSlot()) return false;
            ItemStack current = container.getItem(slot);
            if (current != expectedIdentity || current.isEmpty()) return false;
            if (QuickContainerAccessItems.isShulker(current) && current.getCount() != 1) return false;
            return !QuickContainerAccessItems.isAnvil(current) || current.getCount() == 1;
        }
        void set(ItemStack replacement) {
            if (!hasValidContainerSlot()) return;
            container.setItem(slot, replacement);
            container.setChanged();
            expectedIdentity = replacement;
        }
        boolean isProtectedMenuSlot(AbstractContainerMenu menu, int slotId) {
            if (slotId < 0 || slotId >= menu.slots.size()) return false;
            Slot clicked = menu.getSlot(slotId);
            return clicked.container == container && containerSlot(clicked) == slot;
        }
        boolean isProtectedPlayerSwap(int button, Object clickType) {
            if (container != inventory(player) || !"SWAP".equals(enumName(clickType))) return false;
            if (button >= 0 && button <= 8) return slot == button;
            int offhand = offhandSlot();
            return button == offhand && slot == offhand;
        }
        boolean isPresentInMenu(AbstractContainerMenu menu) {
            for (Slot menuSlot : menu.slots)
                if (menuSlot.container == container && containerSlot(menuSlot) == slot) return true;
            return false;
        }
        boolean matchesProtectedItem(ItemStack candidate) {
            return !expectedIdentity.isEmpty() && QuickContainerAccessItems.sameItemAndData(candidate, expectedIdentity);
        }
        private boolean hasValidContainerSlot() { return slot >= 0 && slot < container.getContainerSize(); }
    }

    static final class Session {
        final ServerPlayer player;
        final Inventory playerInventory;
        final AbstractContainerMenu menu;
        final SourceRef sourceRef;
        final QuickContainerAccessItems.MenuKind kind;
        final int openedSize;

        private Session(ServerPlayer player, Inventory playerInventory, AbstractContainerMenu menu, SourceRef sourceRef,
                        QuickContainerAccessItems.MenuKind kind, int openedSize) {
            this.player = player;
            this.playerInventory = playerInventory;
            this.menu = menu;
            this.sourceRef = sourceRef;
            this.kind = kind;
            this.openedSize = openedSize;
        }
        boolean valid() {
            if (!QuickContainerAccess.enabled() || player instanceof EntityPlayerMPFake || !sourceRef.valid()) return false;
            if (kind == QuickContainerAccessItems.MenuKind.SHULKER && QuickContainerAccessStorage.shulkerSize() != openedSize) return false;
            return kind != QuickContainerAccessItems.MenuKind.ENDER_CHEST
                    || player.getEnderChestInventory().getContainerSize() == openedSize;
        }
    }
    //#endif
}
