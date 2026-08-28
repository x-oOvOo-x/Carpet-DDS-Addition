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
import carpetddsaddition.core.inventory.DdsDirectSlotAccess;
import carpetddsaddition.core.inventory.DdsEnderMenuFactory;
import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
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
    private static final Map<ItemStack, ServerPlayer> SOURCE_LEASES = new IdentityHashMap<>();

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
                      QuickContainerAccessItems.MenuKind kind, int openedSize, Container openedContainer) {
        Inventory playerInventory = inventory(player);
        Session previous = SESSIONS_BY_INVENTORY.get(playerInventory);
        if (previous != null) end(previous);
        if (!sourceRef.canLease()) return;
        sourceRef.lease();
        Session session = new Session(player, playerInventory, menu, sourceRef, kind, openedSize, openedContainer);
        SESSIONS_BY_MENU.put(menu, session);
        SESSIONS_BY_INVENTORY.put(playerInventory, session);
    }
    static Session finish(AbstractContainerMenu menu, Player player) {
        Session session = SESSIONS_BY_MENU.get(menu);
        if (session == null || session.player != player) return null;
        end(session); return session;
    }
    static void closeAllActiveMenus() {
        Session[] sessions = SESSIONS_BY_MENU.values().toArray(new Session[0]);
        for (Session session : sessions) {
            if (session.player.containerMenu == session.menu) session.player.closeContainer();
            else end(session);
        }
        clearAll();
    }
    static void clearAll() { SESSIONS_BY_MENU.clear(); SESSIONS_BY_INVENTORY.clear(); SOURCE_LEASES.clear(); }
    private static void end(Session session) {
        SESSIONS_BY_MENU.remove(session.menu);
        if (SESSIONS_BY_INVENTORY.get(session.playerInventory) == session) SESSIONS_BY_INVENTORY.remove(session.playerInventory);
        session.sourceRef.release();
    }

    static SourceRef rootRef(ServerPlayer player, Container container, int slot, ItemStack expected) {
        return SourceRef.item(player, player, null, null, container, slot, expected, null);
    }
    static SourceRef rootRefForMenu(ServerPlayer player, AbstractContainerMenu menu, Slot clicked, ItemStack target) {
        if (clicked.container == inventory(player)) return rootRef(player, clicked.container, containerSlot(clicked), target);
        ServerPlayer owner = ownerFor(player, menu, clicked.container);
        DdsEnderMenuFactory factory = enderMenuFactory(menu);
        Container canonical = canonicalContainer(clicked.container);
        boolean vanillaEnderRoot = owner != null && canonical == owner.getEnderChestInventory() && factory == null;
        SourceRef guard = SourceRef.guard(player, owner, vanillaEnderRoot ? null : menu, null, factory);
        PathNode parentPath = externalStoragePath(owner, clicked.container, guard);
        return SourceRef.item(player, owner, guard, parentPath, clicked.container, containerSlot(clicked), target, factory);
    }
    static SourceRef sourceRefForActiveClick(Session active, Slot clicked, ItemStack target,
                                             QuickContainerAccessItems.MenuKind nextKind) {
        if (nextKind == null || !safeDirectSlot(active.player, clicked)) return null;
        if (clicked.container == active.playerInventory)
            return rootRef(active.player, active.playerInventory, containerSlot(clicked), target);
        return SourceRef.item(active.player, active.inventoryOwner, active.sourceRef, active.path,
                clicked.container, containerSlot(clicked), target, active.sourceRef.enderMenuFactory);
    }
    private static PathNode externalStoragePath(ServerPlayer owner, Container container, SourceRef guard) {
        Container canonical = canonicalContainer(container);
        if (owner != null && canonical == owner.getEnderChestInventory())
            return new PathNode(null, QuickContainerAccessItems.MenuKind.ENDER_CHEST, canonical, canonical.getContainerSize(), guard);
        return null;
    }

    static ServerPlayer ownerFor(ServerPlayer viewer, AbstractContainerMenu menu, Container container) {
        if (container == inventory(viewer)) return viewer;
        ServerPlayer owner = ownerOf(container);
        if (owner == null) owner = ownerOf(menu);
        return owner != null ? owner : viewer;
    }
    private static ServerPlayer ownerOf(Object value) {
        return value instanceof DdsInventoryOwnerView ? ((DdsInventoryOwnerView) value).ddsInventoryOwner() : null;
    }
    private static DdsEnderMenuFactory enderMenuFactory(Object value) {
        return value instanceof DdsEnderMenuFactory ? (DdsEnderMenuFactory) value : null;
    }
    static Container canonicalContainer(Container container) {
        if (container instanceof DdsInventoryOwnerView) {
            Container canonical = ((DdsInventoryOwnerView) container).ddsCanonicalContainer();
            if (canonical != null) return canonical;
        }
        return container;
    }
    static boolean safeDirectSlot(ServerPlayer player, Slot slot) {
        if (slot == null || !slot.mayPickup(player)) return false;
        int index = containerSlot(slot);
        if (slot.container instanceof DdsDirectSlotAccess
                && !((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(index)) return false;
        ItemStack stack = slot.getItem();
        return stack.isEmpty() || slot.mayPlace(stack);
    }
    static boolean isLeasedByOther(ServerPlayer player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ServerPlayer holder = SOURCE_LEASES.get(stack);
        return holder != null && holder != player;
    }

    static boolean isPickupAllSourceCollect(AbstractContainerMenu menu, Object clickType, Session session) {
        if (!"PICKUP_ALL".equals(enumName(clickType))) return false;
        ItemStack carried = carried(menu, session.player);
        return !carried.isEmpty() && session.sourceRef.hasPresentProtectedItem(menu, carried);
    }
    static String enumName(Object value) { return value instanceof Enum<?> ? ((Enum<?>) value).name() : String.valueOf(value); }

    static final class SourceRef {
        final ServerPlayer player, inventoryOwner;
        final SourceRef parent;
        final PathNode parentPath;
        final Container container;
        final int slot;
        final AbstractContainerMenu guardMenu;
        final DdsEnderMenuFactory enderMenuFactory;
        private ItemStack expectedIdentity;

        private SourceRef(ServerPlayer player, ServerPlayer inventoryOwner, SourceRef parent, PathNode parentPath,
                          Container container, int slot, ItemStack expected, AbstractContainerMenu guardMenu,
                          DdsEnderMenuFactory enderMenuFactory) {
            this.player = player; this.inventoryOwner = inventoryOwner; this.parent = parent; this.parentPath = parentPath;
            this.container = container; this.slot = slot; this.expectedIdentity = expected; this.guardMenu = guardMenu;
            this.enderMenuFactory = enderMenuFactory;
        }
        static SourceRef guard(ServerPlayer player, ServerPlayer owner, AbstractContainerMenu menu, PathNode parentPath,
                               DdsEnderMenuFactory factory) {
            return new SourceRef(player, owner, null, parentPath, null, -1, ItemStack.EMPTY, menu, factory);
        }
        static SourceRef item(ServerPlayer player, ServerPlayer owner, SourceRef parent, PathNode parentPath,
                              Container container, int slot, ItemStack expected, DdsEnderMenuFactory factory) {
            return new SourceRef(player, owner, parent, parentPath, container, slot, expected, null, factory);
        }
        boolean hasItemSource() { return container != null; }
        ItemStack get() { return hasItemSource() && hasValidContainerSlot() ? container.getItem(slot) : ItemStack.EMPTY; }
        boolean valid() {
            if (parent != null && !parent.valid() || inventoryOwner == null || !inventoryOwner.isAlive()) return false;
            if (!hasItemSource()) return guardMenu == null || guardMenu.stillValid(player);
            if (!hasValidContainerSlot()) return false;
            ItemStack current = container.getItem(slot);
            if (current != expectedIdentity || current.isEmpty()) return false;
            if (QuickContainerAccessItems.isShulker(current) && current.getCount() != 1) return false;
            return !QuickContainerAccessItems.isAnvil(current) || current.getCount() == 1;
        }
        void set(ItemStack replacement) {
            if (!hasValidContainerSlot()) return;
            ItemStack previous = expectedIdentity;
            container.setItem(slot, replacement); container.setChanged(); expectedIdentity = replacement;
            if (SOURCE_LEASES.get(previous) == player) SOURCE_LEASES.remove(previous);
            if (!replacement.isEmpty()) SOURCE_LEASES.put(replacement, player);
        }
        boolean isLeafProtectedMenuSlot(AbstractContainerMenu menu, int slotId) {
            if (!hasItemSource() || slotId < 0 || slotId >= menu.slots.size()) return false;
            Slot clicked = menu.getSlot(slotId);
            if (clicked.container == container && containerSlot(clicked) == slot) return true;
            return expectedIdentity != null && !expectedIdentity.isEmpty() && clicked.getItem() == expectedIdentity
                    && canonicalContainer(clicked.container) == canonicalContainer(container);
        }
        boolean isProtectedMenuSlot(AbstractContainerMenu menu, int slotId) {
            return isLeafProtectedMenuSlot(menu, slotId) || parent != null && parent.isProtectedMenuSlot(menu, slotId);
        }
        boolean isProtectedPlayerSwap(int button, Object clickType) {
            if (parent != null && parent.isProtectedPlayerSwap(button, clickType)) return true;
            if (container != inventory(player) || !"SWAP".equals(enumName(clickType))) return false;
            if (button >= 0 && button <= 8) return slot == button;
            int offhand = offhandSlot();
            return button == offhand && slot == offhand;
        }
        boolean hasPresentProtectedItem(AbstractContainerMenu menu, ItemStack candidate) {
            if (hasItemSource() && isPresentInMenu(menu) && matchesProtectedItem(candidate)) return true;
            return parent != null && parent.hasPresentProtectedItem(menu, candidate);
        }
        private boolean isPresentInMenu(AbstractContainerMenu menu) {
            if (!hasItemSource()) return false;
            for (Slot menuSlot : menu.slots)
                if (menuSlot.container == container && containerSlot(menuSlot) == slot) return true;
            return false;
        }
        private boolean matchesProtectedItem(ItemStack candidate) {
            return expectedIdentity != null && !expectedIdentity.isEmpty()
                    && QuickContainerAccessItems.sameItemAndData(candidate, expectedIdentity);
        }
        private boolean hasValidContainerSlot() { return container != null && slot >= 0 && slot < container.getContainerSize(); }
        boolean canLease() {
            if (parent != null && !parent.canLease()) return false;
            if (!hasItemSource() || expectedIdentity.isEmpty()) return true;
            ServerPlayer holder = SOURCE_LEASES.get(expectedIdentity);
            return holder == null || holder == player;
        }
        void lease() {
            if (parent != null) parent.lease();
            if (hasItemSource() && !expectedIdentity.isEmpty()) SOURCE_LEASES.put(expectedIdentity, player);
        }
        void release() {
            if (hasItemSource() && SOURCE_LEASES.get(expectedIdentity) == player) SOURCE_LEASES.remove(expectedIdentity);
            if (parent != null) parent.release();
        }
    }

    static final class PathNode {
        final PathNode parent;
        final QuickContainerAccessItems.MenuKind kind;
        final Container openedContainer;
        final int openedSize;
        final SourceRef sourceRef;

        private PathNode(PathNode parent, QuickContainerAccessItems.MenuKind kind, Container openedContainer,
                         int openedSize, SourceRef sourceRef) {
            this.parent = parent; this.kind = kind; this.openedContainer = openedContainer;
            this.openedSize = openedSize; this.sourceRef = sourceRef;
        }
        PathNode findOpenedContainer(Container container) {
            for (PathNode node = this; node != null; node = node.parent) if (node.openedContainer == container) return node;
            return null;
        }
        PathNode findSourceMenuSlot(AbstractContainerMenu menu, int slotId) {
            for (PathNode node = this; node != null; node = node.parent)
                if (node.sourceRef != null && node.sourceRef.isLeafProtectedMenuSlot(menu, slotId)) return node;
            return null;
        }
        boolean valid(ServerPlayer viewer) {
            if (parent != null && !parent.valid(viewer) || sourceRef == null || !sourceRef.valid()) return false;
            if (kind == QuickContainerAccessItems.MenuKind.SHULKER)
                return openedContainer != null && openedContainer.getContainerSize() == openedSize
                        && QuickContainerAccessStorage.shulkerSize() == openedSize;
            if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
                ServerPlayer owner = sourceRef.inventoryOwner;
                return owner != null && openedContainer == owner.getEnderChestInventory()
                        && openedContainer.getContainerSize() == openedSize;
            }
            return true;
        }
    }

    static final class Session {
        final ServerPlayer player, inventoryOwner;
        final Inventory playerInventory;
        final AbstractContainerMenu menu;
        final SourceRef sourceRef;
        final QuickContainerAccessItems.MenuKind kind;
        final int openedSize;
        final PathNode path;

        private Session(ServerPlayer player, Inventory playerInventory, AbstractContainerMenu menu, SourceRef sourceRef,
                        QuickContainerAccessItems.MenuKind kind, int openedSize, Container openedContainer) {
            this.player = player; this.inventoryOwner = sourceRef.inventoryOwner; this.playerInventory = playerInventory;
            this.menu = menu; this.sourceRef = sourceRef; this.kind = kind; this.openedSize = openedSize;
            this.path = new PathNode(sourceRef.parentPath, kind, openedContainer, openedSize, sourceRef);
        }
        boolean valid() {
            return QuickContainerAccess.enabled() && !(player instanceof EntityPlayerMPFake) && player.isAlive() && path.valid(player);
        }
    }
    //#endif
}
