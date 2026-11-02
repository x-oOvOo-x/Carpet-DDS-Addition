/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260300
//#if MC >= 11904
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//#endif
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Server-only, menu-scoped provenance of an Ender item actually picked up onto
 * the cursor. An Ender item carries no owner NBT; target-slot ownership cannot
 * authorize access to the carried item's remote canonical inventory.
 */
final class QuickContainerAccessCarriedEnderOrigins {
    private static final Map<ServerPlayer, Pickup> PENDING = new IdentityHashMap<>();
    private static final Map<ServerPlayer, Origin> ORIGINS = new IdentityHashMap<>();

    private QuickContainerAccessCarriedEnderOrigins() {}

    static void beforeVanillaClick(ServerPlayer viewer, AbstractContainerMenu menu, int slotId,
                                   int button, Object clickType) {
        PENDING.remove(viewer);
        prune(viewer);
        if (viewer.containerMenu != menu || !menu.stillValid(viewer)
                || QuickContainerAccessCompat.isOrgOfflineMenu(menu)
                || slotId < 0 || slotId >= menu.slots.size()) return;
        String type = QuickContainerAccessSession.enumName(clickType);
        boolean pickup = "PICKUP".equals(type) && button == 0;
        // GCA's reserved cursor scratch swap is also a real server-side pickup.
        boolean scratch = "SWAP".equals(type) && button == 9
                && "dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu"
                        .equals(menu.getClass().getName());
        if (!pickup && !scratch) return;
        Slot slot = menu.getSlot(slotId);
        ItemStack source = slot.getItem();
        ItemStack previouslyCarried = QuickContainerAccessSession.carried(menu, viewer);
        if (!QuickContainerAccessSession.safeDirectSlot(viewer, slot)
                || !isEnder(source)
                || !previouslyCarried.isEmpty() && !slot.mayPlace(previouslyCarried)) return;
        ServerPlayer owner = QuickContainerAccessSession.ownerFor(viewer, menu, slot.container);
        if (owner == viewer && slot.container != QuickContainerAccessSession.inventory(viewer)
                && QuickContainerAccessSession.remoteOwnerForMenu(viewer, menu) != null) return;
        int capacity = QuickContainerAccessSession.authorizedStorageEnderSize(viewer, menu, owner);
        if (capacity <= 0) return;
        Object session = owner == viewer ? null : currentSession(viewer, menu, owner);
        PENDING.put(viewer, new Pickup(menu, slot, source, source.copy(),
                previouslyCarried, owner, capacity, session));
    }

    static void afterVanillaClick(ServerPlayer viewer) {
        Pickup pending = PENDING.remove(viewer);
        if (pending != null && viewer.containerMenu == pending.menu) {
            ItemStack carried = QuickContainerAccessSession.carried(pending.menu, viewer);
            // No origin grant for a merge, an unchanged cursor or a rejected swap.
            if (isEnder(carried) && carried != pending.previouslyCarried
                    && QuickContainerAccessItems.sameItemAndData(carried, pending.snapshot)
                    && pending.slot.getItem() != pending.source) {
                ORIGINS.put(viewer, new Origin(pending.menu, carried, pending.owner,
                        pending.capacity, pending.session));
                return;
            }
        }
        prune(viewer);
    }

    /** null means unproven or revoked: never silently fall back to the viewer's Ender. */
    static ServerPlayer resolve(ServerPlayer viewer, AbstractContainerMenu menu, ItemStack carried) {
        // The viewer's own inventory menu is authoritative for their own Ender.
        if (menu == viewer.inventoryMenu && viewer.containerMenu == menu) return viewer;
        Origin origin = ORIGINS.get(viewer);
        if (origin == null || origin.menu != menu || viewer.containerMenu != menu
                || origin.carried != carried || !isEnder(carried)
                || !menu.stillValid(viewer) || QuickContainerAccessCompat.isOrgOfflineMenu(menu)) {
            ORIGINS.remove(viewer);
            return null;
        }
        ServerPlayer owner = origin.owner;
        if (owner != viewer && (!owner.isAlive() || !isCurrentLiveOwner(viewer, owner)
                || origin.session != null && currentSession(viewer, menu, owner) != origin.session)) {
            ORIGINS.remove(viewer);
            return null;
        }
        int allowed = QuickContainerAccessSession.authorizedStorageEnderSize(viewer, menu, owner);
        if (allowed <= 0 || allowed < origin.capacity) {
            ORIGINS.remove(viewer);
            return null;
        }
        return owner;
    }

    static void forgetMenu(AbstractContainerMenu menu, net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof ServerPlayer)) return;
        ServerPlayer viewer = (ServerPlayer) player;
        Pickup pending = PENDING.get(viewer);
        if (pending != null && pending.menu == menu) PENDING.remove(viewer);
        Origin origin = ORIGINS.get(viewer);
        if (origin != null && origin.menu == menu) ORIGINS.remove(viewer);
    }

    static void clearAll() { PENDING.clear(); ORIGINS.clear(); }

    private static void prune(ServerPlayer viewer) {
        Origin origin = ORIGINS.get(viewer);
        if (origin != null && (viewer.containerMenu != origin.menu
                || QuickContainerAccessSession.carried(origin.menu, viewer) != origin.carried
                || !isEnder(origin.carried)))
            ORIGINS.remove(viewer);
    }

    private static boolean isEnder(ItemStack stack) {
        return stack != null && stack.getCount() == 1
                && QuickContainerAccessItems.isItem(stack, Items.ENDER_CHEST);
    }

    private static Object currentSession(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner) {
//#if MC >= 11904
//$$         return DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID());
//#else
        return null;
//#endif
    }

    private static boolean isCurrentLiveOwner(ServerPlayer viewer, ServerPlayer owner) {
//#if MC >= 12111
//$$         return viewer.level().getServer() != null
//$$                 && viewer.level().getServer().getPlayerList().getPlayer(owner.getUUID()) == owner;
//#elseif MC >= 12001
//$$         return viewer.serverLevel().getServer() != null
//$$                 && viewer.serverLevel().getServer().getPlayerList().getPlayer(owner.getUUID()) == owner;
//#else
        return viewer.level.getServer() != null
                && viewer.level.getServer().getPlayerList().getPlayer(owner.getUUID()) == owner;
//#endif
    }

    private static final class Pickup {
        final AbstractContainerMenu menu;
        final Slot slot;
        final ItemStack source;
        final ItemStack snapshot;
        final ItemStack previouslyCarried;
        final ServerPlayer owner;
        final int capacity;
        final Object session;

        Pickup(AbstractContainerMenu menu, Slot slot, ItemStack source, ItemStack snapshot,
               ItemStack previouslyCarried, ServerPlayer owner,
               int capacity, Object session) {
            this.menu = menu; this.slot = slot; this.source = source;
            this.snapshot = snapshot; this.previouslyCarried = previouslyCarried;
            this.owner = owner; this.capacity = capacity; this.session = session;
        }
    }

    private static final class Origin {
        final AbstractContainerMenu menu;
        final ItemStack carried;
        final ServerPlayer owner;
        final int capacity;
        final Object session;

        Origin(AbstractContainerMenu menu, ItemStack carried, ServerPlayer owner,
               int capacity, Object session) {
            this.menu = menu; this.carried = carried; this.owner = owner;
            this.capacity = capacity; this.session = session;
        }
    }
}
//#else
//$$ final class QuickContainerAccessCarriedEnderOrigins {
    //$$ private QuickContainerAccessCarriedEnderOrigins() {}
//$$ }
//#endif
