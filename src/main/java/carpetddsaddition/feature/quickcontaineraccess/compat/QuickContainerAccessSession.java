/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.integration.gca.fakeplayer.DdsGcaFakePlayerBridge;
import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;

//#if MC >= 11904
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySession;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import carpetddsaddition.network.DdsServerNetwork;
//#endif

//#if MC >= 12111
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventoryCanonicalEnderMenu;
//#endif

//#if MC >= 11404 && MC <= 260300
import carpet.patches.EntityPlayerMPFake;
import carpetddsaddition.core.inventory.DdsCanonicalSlotMapping;
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

    //#if MC >= 11404 && MC <= 260300
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
    static void closeInvalidMenus() {
        Session[] sessions = SESSIONS_BY_MENU.values().toArray(new Session[0]);
        for (Session session : sessions) {
            // Revocation is sticky: re-enabling the rule cannot revive a stale provider grant.
            session.sourceRef.enderAuthorityValid();
            if (session.valid()) continue;
            if (Boolean.getBoolean("dds.playerinventory.trace"))
                org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.qca")
                        .warn("qca_session_invalid id="+session.menu.containerId
                                +" kind="+session.kind+" source_valid="+session.sourceRef.valid()
                                +" owner_alive="+session.inventoryOwner.isAlive());
            if (session.player.containerMenu == session.menu) session.player.closeContainer();
            else end(session);
        }
    }
    static void closeAllActiveMenus() {
        Session[] sessions = SESSIONS_BY_MENU.values().toArray(new Session[0]);
        for (Session session : sessions) {
            if (session.player.containerMenu == session.menu) session.player.closeContainer();
            else end(session);
        }
        clearAll();
    }
    static void clearAll() {
        SESSIONS_BY_MENU.clear(); SESSIONS_BY_INVENTORY.clear(); SOURCE_LEASES.clear();
    }
    private static void end(Session session) {
        SESSIONS_BY_MENU.remove(session.menu);
        if (SESSIONS_BY_INVENTORY.get(session.playerInventory) == session) SESSIONS_BY_INVENTORY.remove(session.playerInventory);
        session.sourceRef.release();
    }

    static SourceRef rootRef(ServerPlayer player, Container container, int slot, ItemStack expected) {
        return SourceRef.item(player, player, null, null, container, slot, expected, null, 0, false);
    }
    static SourceRef rootRefForMenu(ServerPlayer player, AbstractContainerMenu menu, Slot clicked, ItemStack target) {
        if (clicked.container == inventory(player)) return rootRef(player, clicked.container, containerSlot(clicked), target);
        ServerPlayer owner = ownerFor(player, menu, clicked.container);
        DdsEnderMenuFactory factory = enderMenuFactory(menu);
        Container canonical = canonicalContainer(clicked.container);
        boolean vanillaEnderRoot = owner != null && canonical == owner.getEnderChestInventory() && factory == null;
        int authorizedEnderSize = providerAuthorizedEnderSize(player, menu, owner);
        boolean extensionBacked = extensionBackedEnderAuthorization(player, menu, owner, authorizedEnderSize);
        SourceRef guard = SourceRef.guard(player, owner,
                vanillaEnderRoot ? null : menu, null, factory, authorizedEnderSize, extensionBacked);
        PathNode parentPath = externalStoragePath(player, menu, owner, clicked.container, guard);
        return SourceRef.item(player, owner, guard, parentPath, clicked.container, containerSlot(clicked), target,
                factory, authorizedEnderSize, extensionBacked);
    }
    static SourceRef sourceRefForActiveClick(Session active, Slot clicked, ItemStack target,
                                             QuickContainerAccessItems.MenuKind nextKind) {
        if (nextKind == null || !safeDirectSlot(active.player, clicked)) return null;
        if (clicked.container == active.playerInventory)
            return rootRef(active.player, active.playerInventory, containerSlot(clicked), target);
        return SourceRef.item(active.player, active.inventoryOwner, active.sourceRef, active.path,
                clicked.container, containerSlot(clicked), target, active.sourceRef.enderMenuFactory,
                active.sourceRef.authorizedEnderSize, active.sourceRef.extensionBackedEnder);
    }
    private static PathNode externalStoragePath(ServerPlayer viewer, AbstractContainerMenu menu,
                                                ServerPlayer owner, Container container, SourceRef guard) {
        Container canonical = canonicalContainer(container);
        if (owner == null || canonical != owner.getEnderChestInventory()) return null;
        int visibleSize = remoteEnderVisibleSize(viewer, menu, owner);
        return visibleSize == 0 ? null
                : new PathNode(null, QuickContainerAccessItems.MenuKind.ENDER_CHEST, canonical, visibleSize, guard);
    }

    static int providerAuthorizedEnderSize(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        if (viewer == null || menu == null || owner == null || owner == viewer) return 0;
        int providerSize = remoteEnderVisibleSize(viewer, menu, owner);
        if (providerSize == 0) providerSize = gcaAuthorizedEnderSize(viewer, menu, owner);
        //#if MC >= 260102
        //$$ // Only a verified Org fake menu may grant the legacy 27 QCA slots.
        //$$ // Extension OFF must never promote this to 54 AMS slots.
        //$$ if (providerSize == 0 && !PlayerInventoryExtension.enabled()
        //$$         && orgLegacyQcaEnderAuthorized(viewer, menu, owner)) providerSize = 27;
        //#endif
        //#if MC >= 260102
        //$$ // A strict Org inventory session, not the legacy Remote rule, authorizes
        //$$ // opening the owner's canonical 27/54-slot Ender from an actual QCA item click.
        //$$ if (providerSize == 0 && strictOrgWholeEnderAuthorized(viewer, menu, owner))
        //$$     return actualRemoteEnderSize(owner);
        //#endif
        if (providerSize <= 0) return providerSize;
        // An exact strict GCA Session can authorize the canonical AMS size even when the
        // Provider exposes only the first 27 Ender slots; legacy and unknown sessions stay bounded.
        if (providerSize == 27 && strictGcaWholeEnderAuthorized(viewer, menu, owner)) {
            int whole = actualRemoteEnderSize(owner);
            if (whole != 0) return whole;
        }
        // Never elevate a Provider's 27-slot authorization without verified strict
        // GCA/Org whole-Ender permission, regardless of Minecraft version.
        return providerSize;
    }

    //#if MC >= 260102
    //$$ /** Legacy-QCA Ender access is bounded to the authenticated fake owner. */
    //$$ private static boolean orgLegacyQcaEnderAuthorized(ServerPlayer viewer, AbstractContainerMenu menu,
    //$$                                                     ServerPlayer owner) {
    //$$     if (!QuickContainerAccess.enabled() || viewer == null || menu == null
    //$$             || !(owner instanceof EntityPlayerMPFake) || viewer.containerMenu != menu
    //$$             || !menu.stillValid(viewer) || actualRemoteEnderSize(owner) < 27) return false;
    //$$     String type = menu.getClass().getName();
    //$$     if (!type.equals("boat.carpetorgaddition.wheel.screen.PlayerInventoryScreenHandler")
    //$$             && !type.equals("boat.carpetorgaddition.wheel.screen.WithButtonPlayerInventoryScreenHandler"))
    //$$         return false;
    //$$     return DdsRemotePlayerOwnerResolver.owner(menu) == owner && owner.isAlive();
    //$$ }
    //#endif

    //#if MC >= 260102
    //$$ // Reuse Extension's exact provider session and negotiated structural capability.
    //$$ // No title matching, remote boolean or inferred fake-player ownership is sufficient.
    //$$ private static boolean strictOrgWholeEnderAuthorized(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
    //$$     if (!PlayerInventoryExtension.enabled() || viewer == null || menu == null || owner == null
    //$$             || viewer.containerMenu != menu
    //$$             || !DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C)
    //$$             || !DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.STRUCTURE_RESULT_S2C))
    //$$         return false;
    //$$     String name = menu.getClass().getName();
    //$$     if (!name.equals("boat.carpetorgaddition.wheel.screen.PlayerInventoryScreenHandler")
    //$$             && !name.equals("boat.carpetorgaddition.wheel.screen.WithButtonPlayerInventoryScreenHandler"))
    //$$         return false;
    //$$     DdsPlayerInventorySession session =
    //$$             DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID());
    //$$     return session != null && "org".equals(session.providerId())
    //$$             && session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.INVENTORY
    //$$             && (session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_FAKE
    //$$                     || session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_PLAYER)
    //$$             && session.liveTarget() == owner;
    //$$ }
    //#endif

    private static int gcaAuthorizedEnderSize(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        Inventory viewerInventory = inventory(viewer);
        boolean gcaInventory = false;
        for (Slot slot : menu.slots) {
            if (slot.container == viewerInventory || ownerOf(slot.container) != owner) continue;
            if (slot.container.getClass().getName().endsWith(".PlayerInventoryContainer")) {
                gcaInventory = true;
                break;
            }
        }
        if (!gcaInventory) return 0;
        //#if MC >= 11904
        //$$ if (!DdsGcaFakePlayerBridge.shouldOpenGcaEnderChest(viewer)) return -1;
        //$$ if (strictGcaWholeEnderAuthorized(viewer, menu, owner)) return actualRemoteEnderSize(owner);
        //$$ return 27;
        //#else
        return 0;
        //#endif
    }

    private static boolean strictGcaWholeEnderAuthorized(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        //#if MC >= 11904
        //$$ if (!PlayerInventoryExtension.enabled()
        //$$         || !DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C)
        //$$         || !DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.STRUCTURE_RESULT_S2C)) return false;
        //$$ DdsPlayerInventorySession session =
        //$$         DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID());
        //$$ return session != null
        //$$         && "gca".equals(session.providerId())
        //$$         && (session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.INVENTORY
        //$$                 || session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.ENDER_CHEST
        //$$                 && DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled())
        //$$         && (session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_FAKE
        //$$                 || session.targetType() == DdsPlayerInventorySession.TargetType.LIVE_PLAYER)
        //$$         && session.liveTarget() == owner;
        //#else
        return false;
        //#endif
    }

    /** Track only capacity that would not be available without a strict Extension session. */
    private static boolean extensionBackedEnderAuthorization(ServerPlayer viewer, AbstractContainerMenu menu,
                                                              ServerPlayer owner, int authorizedSize) {
        if (authorizedSize <= 0 || viewer == null || owner == null || owner == viewer
                || !PlayerInventoryExtension.enabled()) return false;
        //#if MC >= 260102
        //$$ if (strictOrgWholeEnderAuthorized(viewer, menu, owner)
        //$$         && remoteEnderVisibleSize(viewer, menu, owner) == 0) return true;
        //#endif
        return authorizedSize > 27 && remoteEnderVisibleSize(viewer, menu, owner) < 54
                && strictGcaWholeEnderAuthorized(viewer, menu, owner);
    }

    /** QCA item-storage must not infer remote capacity from the owner's backing container. */
    static int authorizedStorageEnderSize(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        if (viewer == null || menu == null || owner == null || viewer.containerMenu != menu) return 0;
        int actual = actualRemoteEnderSize(owner);
        if (owner == viewer) return actual;
        if (!owner.isAlive()) return 0;
        //#if MC >= 12111
        //$$ if (viewer.level().getServer() == null
        //$$         || viewer.level().getServer().getPlayerList().getPlayer(owner.getUUID()) != owner) return 0;
        //#endif
        Session active = get(menu);
        // A non-Ender QCA screen may remain open, but must not keep strict Ender elevation.
        if (active != null && !active.sourceRef.enderAuthorityValid()) return 0;
        int allowed = active == null ? providerAuthorizedEnderSize(viewer, menu, owner)
                : active.player == viewer && active.inventoryOwner == owner && active.valid()
                ? active.sourceRef.authorizedEnderSize : 0;
        return (allowed == 27 || allowed == 54) && actual >= allowed ? allowed : 0;
    }

    static int actualRemoteEnderSize(ServerPlayer owner) {
        if (owner == null) return 0;
        int size = owner.getEnderChestInventory().getContainerSize();
        return size == 27 || size == 54 ? size : 0;
    }

    static int remoteEnderVisibleSize(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        if (viewer == null || menu == null || owner == null || owner == viewer) return 0;
        Container canonical = owner.getEnderChestInventory();
        int canonicalSize = canonical.getContainerSize();
        if (canonicalSize != 27 && canonicalSize != 54) return 0;
        boolean[] visible = new boolean[canonicalSize];
        Inventory viewerInventory = inventory(viewer);
        int count = 0;
        for (Slot slot : menu.slots) {
            if (slot.container == viewerInventory || canonicalContainer(slot.container) != canonical) continue;
            int providerSlot = containerSlot(slot);
            if (providerSlot < 0) return 0;
            if (slot.container instanceof DdsDirectSlotAccess
                    && !((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(providerSlot)) continue;
            int canonicalSlot;
            if (slot.container == canonical) canonicalSlot = providerSlot;
            else if (slot.container instanceof DdsCanonicalSlotMapping)
                canonicalSlot = ((DdsCanonicalSlotMapping) slot.container).ddsCanonicalSlot(providerSlot);
            else continue;
            if (canonicalSlot < 0 || canonicalSlot >= canonicalSize) return 0;
            if (!visible[canonicalSlot]) { visible[canonicalSlot] = true; count++; }
        }
        if (count != 27 && count != 54) return 0;
        for (int i = 0; i < canonicalSize; i++)
            if (visible[i] != (i < count)) return 0;
        return count;
    }

    static ServerPlayer ownerFor(ServerPlayer viewer, AbstractContainerMenu menu, Container container) {
        if (container == inventory(viewer)) return viewer;
        ServerPlayer owner = ownerOf(container);
        if (owner == null) owner = ownerOf(menu);
        //#if MC >= 11404 && MC <= 260300
        // Unknown slots of a known remote-player menu must not inherit viewer ownership.
        if (owner == null && (remoteOwnerForMenu(viewer, menu) != null || isPlayerInventoryProviderMenu(menu)))
            return null;
        //#endif
        return owner != null ? owner : viewer;
    }
    //#if MC >= 11404 && MC <= 260300
    private static boolean isPlayerInventoryProviderMenu(AbstractContainerMenu menu) {
        if (menu == null) return false;
        String type = menu.getClass().getName();
        return type.equals("dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu")
                || type.contains(".carpetorgaddition.")
                && (type.endsWith(".PlayerInventoryScreenHandler")
                    || type.endsWith(".WithButtonPlayerInventoryScreenHandler")
                    || type.endsWith(".PlayerEnderChestScreenHandler"));
    }
    //#endif
    private static ServerPlayer ownerOf(Object value) {
        if (value instanceof DdsInventoryOwnerView) return ((DdsInventoryOwnerView) value).ddsInventoryOwner();
        return QuickContainerAccessOrgCompat.owner(value);
    }
    static ServerPlayer remoteOwnerForMenu(ServerPlayer viewer, AbstractContainerMenu menu) {
        if (viewer == null || menu == null) return null;
        ServerPlayer owner = ownerOf(menu);
        if (owner != null && owner != viewer) return owner;
        Inventory viewerInventory = inventory(viewer);
        for (Slot slot : menu.slots) {
            if (slot.container == viewerInventory) continue;
            owner = ownerOf(slot.container);
            if (owner != null && owner != viewer) return owner;
        }
        return null;
    }
    static boolean isRemoteEnderMenu(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        if (viewer == null || menu == null || owner == null || owner == viewer) return false;
        String menuName = menu.getClass().getName();
        if (menuName.endsWith(".PlayerEnderChestScreenHandler") && QuickContainerAccessOrgCompat.owner(menu) == owner) return true;
        Inventory viewerInventory = inventory(viewer);
        Container canonicalEnder = owner.getEnderChestInventory();
        for (Slot slot : menu.slots) {
            if (slot.container == viewerInventory || ownerOf(slot.container) != owner) continue;
            if (canonicalContainer(slot.container) == canonicalEnder) return true;
        }
        return false;
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
        return directSlotRejection(player, slot) == null;
    }

    /** The same slot gate powers both the operation and trace diagnostics. */
    static String directSlotRejection(ServerPlayer player, Slot slot) {
        if (slot == null) return "missing_slot";
        if (!slot.mayPickup(player)) return "pickup_denied";
        //#if MC >= 12111
        //$$ if (!QuickContainerAccessProviderSlots.allows(player.containerMenu, slot, player)) return "provider_slot_denied";
        //#endif
        int index = containerSlot(slot);
        if (slot.container instanceof DdsDirectSlotAccess
                && !((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(index))
            return "direct_slot_denied";
        ItemStack stack = slot.getItem();
        return stack.isEmpty() || slot.mayPlace(stack) ? null : "placement_denied";
    }
    static boolean isLeasedAnywhere(ItemStack stack) {
        return stack != null && !stack.isEmpty() && SOURCE_LEASES.containsKey(stack);
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

    /** Recheck the retained Provider grant without requiring its menu to remain current. */
    private static boolean remoteEnderProviderValid(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
        if (menu == null) return false;
        try {
            //#if MC >= 12111
            //$$ if (viewer.level().getServer() == null
            //$$         || viewer.level().getServer().getPlayerList().getPlayer(owner.getUUID()) != owner) return false;
            //#elseif MC >= 12001
            //$$ if (viewer.serverLevel().getServer() == null
            //$$         || viewer.serverLevel().getServer().getPlayerList().getPlayer(owner.getUUID()) != owner) return false;
            //#else
            if (viewer.level.getServer() == null
                    || viewer.level.getServer().getPlayerList().getPlayer(owner.getUUID()) != owner) return false;
            //#endif
            if (!menu.stillValid(viewer)) return false;
            //#if MC >= 11904
            //$$ for (Slot slot : menu.slots) {
            //$$     if (ownerOf(slot.container) == owner
            //$$             && slot.container.getClass().getName().endsWith(".PlayerEnderChestContainer")
            //$$             && !DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()) return false;
            //$$ }
            //#endif
            return true;
        } catch (RuntimeException ignored) { return false; }
    }

    static final class SourceRef {
        final ServerPlayer player, inventoryOwner;
        final SourceRef parent;
        final PathNode parentPath;
        final Container container;
        final int slot;
        final AbstractContainerMenu guardMenu;
        final DdsEnderMenuFactory enderMenuFactory;
        final int authorizedEnderSize;
        final boolean extensionBackedEnder;
        private boolean enderGrantRevoked;
        private final boolean remoteEnderRoot;
        private final Object viewerConnection;
        private final Container remoteEnderContainer;
        private final int remoteEnderCapacity;
        private ItemStack expectedIdentity;

        private SourceRef(ServerPlayer player, ServerPlayer inventoryOwner, SourceRef parent, PathNode parentPath,
                          Container container, int slot, ItemStack expected, AbstractContainerMenu guardMenu,
                          DdsEnderMenuFactory enderMenuFactory, boolean remoteEnderRoot,
                          int authorizedEnderSize, boolean extensionBackedEnder) {
            this.remoteEnderRoot = remoteEnderRoot;
            this.viewerConnection = remoteEnderRoot ? player.connection : null;
            this.remoteEnderContainer = remoteEnderRoot ? inventoryOwner.getEnderChestInventory() : null;
            this.remoteEnderCapacity = remoteEnderRoot ? remoteEnderContainer.getContainerSize() : 0;
            this.authorizedEnderSize = authorizedEnderSize;
            this.extensionBackedEnder = extensionBackedEnder;
            this.player = player; this.inventoryOwner = inventoryOwner; this.parent = parent; this.parentPath = parentPath;
            this.container = container; this.slot = slot; this.expectedIdentity = expected; this.guardMenu = guardMenu;
            this.enderMenuFactory = enderMenuFactory;
        }
        static SourceRef guard(ServerPlayer player, ServerPlayer owner, AbstractContainerMenu menu, PathNode parentPath,
                               DdsEnderMenuFactory factory, int authorizedEnderSize,
                               boolean extensionBackedEnder) {
            return new SourceRef(player, owner, null, parentPath, null, -1, ItemStack.EMPTY, menu, factory, false,
                    authorizedEnderSize, extensionBackedEnder);
        }
        static SourceRef item(ServerPlayer player, ServerPlayer owner, SourceRef parent, PathNode parentPath,
                              Container container, int slot, ItemStack expected, DdsEnderMenuFactory factory,
                              int authorizedEnderSize, boolean extensionBackedEnder) {
            return new SourceRef(player, owner, parent, parentPath, container, slot, expected, null, factory, false,
                    authorizedEnderSize, extensionBackedEnder);
        }
        static SourceRef remoteEnderGuard(ServerPlayer viewer, ServerPlayer owner, AbstractContainerMenu menu,
                                          int authorizedEnderSize) {
            DdsEnderMenuFactory factory = enderMenuFactory(menu);
            //#if MC >= 12111
            //$$ if (factory == null && authorizedEnderSize == actualRemoteEnderSize(owner))
            //$$     factory = DdsPlayerInventoryCanonicalEnderMenu.captureFactory(viewer, menu, owner);
            //#endif
            return new SourceRef(viewer, owner, null, null, null, -1, ItemStack.EMPTY, menu, factory, true,
                    authorizedEnderSize, true);
        }
        boolean remoteEnderSource() {
            SourceRef root = this;
            while (root.parent != null) root = root.parent;
            return root.remoteEnderRoot;
        }
        boolean enderAuthorityValid() {
            if (extensionBackedEnder && !PlayerInventoryExtension.enabled()) enderGrantRevoked = true;
            if (remoteEnderRoot && (player.connection != viewerConnection
                    || inventoryOwner.getEnderChestInventory() != remoteEnderContainer
                    || remoteEnderContainer.getContainerSize() != remoteEnderCapacity
                    || !remoteEnderProviderValid(player, guardMenu, inventoryOwner))) enderGrantRevoked = true;
            return !enderGrantRevoked && (parent == null || parent.enderAuthorityValid());
        }
        boolean hasItemSource() { return container != null; }
        ItemStack get() { return hasItemSource() && hasValidContainerSlot() ? container.getItem(slot) : ItemStack.EMPTY; }
        boolean valid() {
            if (remoteEnderSource() && !enderAuthorityValid()) return false;
            if (parent != null && !parent.valid() || inventoryOwner == null || !inventoryOwner.isAlive()) return false;
            //#if MC >= 12111
            //$$ if (inventoryOwner != player && (player.level().getServer() == null
            //$$         || player.level().getServer().getPlayerList().getPlayer(inventoryOwner.getUUID()) != inventoryOwner))
            //$$     return false;
            //#endif
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
        boolean remoteOwnerEnderPath() {
            PathNode root = this;
            while (root.parent != null) root = root.parent;
            return root.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST && root.sourceRef != null
                    && !root.sourceRef.hasItemSource() && root.sourceRef.inventoryOwner instanceof EntityPlayerMPFake;
        }
        boolean valid(ServerPlayer viewer) {
            if (parent != null && !parent.valid(viewer) || sourceRef == null || !sourceRef.valid()) return false;
            if (kind == QuickContainerAccessItems.MenuKind.SHULKER)
                return openedContainer != null && openedContainer.getContainerSize() == openedSize
                        && QuickContainerAccessStorage.shulkerSize() == openedSize;
            if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
                if (!sourceRef.enderAuthorityValid()) return false;
                ServerPlayer owner = sourceRef.inventoryOwner;
                return owner != null && openedContainer == owner.getEnderChestInventory()
                        && (openedSize == 27 || openedSize == 54)
                        && (owner == viewer || sourceRef.authorizedEnderSize >= openedSize)
                        && openedContainer.getContainerSize() >= openedSize;
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
            return (sourceRef.remoteEnderSource()
                    ? PlayerInventoryExtension.enabled()
                    : QuickContainerAccess.enabled())
                    && !(player instanceof EntityPlayerMPFake) && player.isAlive() && path.valid(player);
        }
    }
    //#endif
}
