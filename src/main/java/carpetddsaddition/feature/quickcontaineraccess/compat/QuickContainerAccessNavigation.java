/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260300
import carpetddsaddition.core.inventory.DdsReopenableMenu;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessServerNetwork;
import carpetddsaddition.integration.playerinventory.DdsPlayerInventoryRootReturns;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
//#endif

/** QCA back navigation follows only an actual captured parent/root; direct remote Ender opens have none. */
final class QuickContainerAccessNavigation {
    private QuickContainerAccessNavigation() {}

    //#if MC >= 11404 && MC <= 260300
    private static final Map<QuickContainerAccessSession.SourceRef, DdsReopenableMenu> ROOTS = new WeakHashMap<>();
    private static final boolean TRACE = Boolean.getBoolean("dds.playerinventory.trace");
    private static final org.apache.logging.log4j.Logger TRACE_LOG =
            org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.qca");

    static void attachSupportedRoot(QuickContainerAccessSession.SourceRef ref, AbstractContainerMenu menu,
                                    Slot clicked, ServerPlayer player, QuickContainerAccessItems.MenuKind kind) {
        if (ref == null || menu == null || clicked == null || player == null
                || clicked.container == QuickContainerAccessSession.inventory(player)) return;
        DdsReopenableMenu root = menu instanceof DdsReopenableMenu ? (DdsReopenableMenu) menu
                : QuickContainerAccessReopenCompat.forInventory(player, menu, ref.inventoryOwner, clicked.container);
        if (TRACE) TRACE_LOG.info("qca_parent_capture source_id=" + menu.containerId
                + " menu_type=" + menu.getClass().getName() + " kind=" + kind
                + " owner_is_viewer=" + (ref.inventoryOwner == player)
                + " source_type=" + clicked.container.getClass().getName()
                + " captured=" + (root != null));
        if (root != null) {
            ROOTS.put(ref, root);
            if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST)
                DdsPlayerInventoryRootReturns.capture(player, menu, ref.inventoryOwner, root);
        }
    }

    static boolean ownersPresent(QuickContainerAccessSession.Session session) {
        if (session == null || !present(session.player)) return false;
        ServerPlayer owner = session.inventoryOwner, viewer = session.player;
        DdsReopenableMenu root = ROOTS.get(session.sourceRef);
        if (root != null) return root.ddsCanReopen(viewer);
        if (!present(owner)) return false;
        if (owner == viewer || session.path.remoteOwnerEnderPath()) return true;
        return sameLevel(owner, viewer) && viewer.distanceToSqr(owner) <= 64.0D;
    }
    static boolean canNavigateBack(QuickContainerAccessSession.Session session) {
        if (session == null) {
            if (TRACE) TRACE_LOG.info("qca_can_back session_missing");
            return false;
        }
        boolean ownersReady = ownersPresent(session);
        boolean valid = ownersReady && session.valid();
        if (!valid) {
            if (TRACE) TRACE_LOG.info("qca_can_back id=" + session.menu.containerId
                    + " kind=" + session.kind + " owners_present=" + ownersReady
                    + " valid=" + valid + " result=false");
            return false;
        }
        DdsReopenableMenu root = ROOTS.get(session.sourceRef);
        if (TRACE) TRACE_LOG.info("qca_can_back id=" + session.menu.containerId
                + " kind=" + session.kind + " root_captured=" + (root != null)
                + " parent_ender=" + (session.path.parent != null
                && session.path.parent.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST));
        if (session.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST)
            return root != null && root.ddsCanReopen(session.player);
        if (session.kind != QuickContainerAccessItems.MenuKind.SHULKER) return false;
        if (root != null && root.ddsCanReopen(session.player)) return true;
        QuickContainerAccessSession.PathNode parent = session.path.parent;
        return parent != null && parent.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST;
    }

    static boolean navigateBack(ServerPlayer player, AbstractContainerMenu menu) {
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (player == null || session == null || session.player != player || !canNavigateBack(session)) {
            if (TRACE) TRACE_LOG.info("qca_back_denied id=" + (menu == null ? -1 : menu.containerId)
                    + " session_present=" + (session != null));
            return false;
        }
        DdsReopenableMenu root = ROOTS.get(session.sourceRef);
        if (root != null && root.ddsCanReopen(player)) {
            // Keep the authorized QCA child current through the reopener's final
            // permission check. ServerPlayer.openMenu closes the old Menu itself.
            boolean opened = root.ddsReopen(player);
            if (TRACE) TRACE_LOG.info("qca_back_reopen id=" + menu.containerId
                    + " opened=" + opened + " current_id=" + player.containerMenu.containerId);
            QuickContainerAccessServerNetwork.sendNavigationState(player, false, false, !opened);
            return opened;
        }
        QuickContainerAccessSession.PathNode parent = session.path.parent;
        return parent != null && parent.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && QuickContainerAccessMenus.navigateBack(player, menu, parent);
    }

    static void clear() { ROOTS.clear(); }
    private static boolean present(ServerPlayer player) {
        if (player == null || !player.isAlive()) return false;
        MinecraftServer server =
        //#if MC >= 12106
        //$$ player.level().getServer();
        //#elseif MC >= 12001
        //$$ player.serverLevel().getServer();
        //#else
        player.level.getServer();
        //#endif
        return server != null && server.getPlayerList().getPlayer(player.getUUID()) == player;
    }
    private static boolean sameLevel(ServerPlayer first, ServerPlayer second) {
        //#if MC >= 12106
        //$$ return first.level() == second.level();
        //#elseif MC >= 12000
        //$$ return first.serverLevel() == second.serverLevel();
        //#else
        return first.level == second.level;
        //#endif
    }
    //#endif
}
