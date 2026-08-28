/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
import carpetddsaddition.core.inventory.DdsReopenableMenu;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessServerNetwork;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
//#endif

/** QCA back navigation is limited to Shulker -> Ender Chest and Shulker -> explicit reopenable roots. */
final class QuickContainerAccessNavigation {
    private QuickContainerAccessNavigation() {}

    //#if MC >= 11404 && MC <= 260200
    private static final Map<QuickContainerAccessSession.SourceRef, DdsReopenableMenu> ROOTS = new WeakHashMap<>();

    static void attachSupportedRoot(QuickContainerAccessSession.SourceRef ref, AbstractContainerMenu menu,
                                    Slot clicked, ServerPlayer player) {
        if (ref != null && clicked != null && player != null && clicked.container != QuickContainerAccessSession.inventory(player)
                && menu instanceof DdsReopenableMenu) ROOTS.put(ref, (DdsReopenableMenu) menu);
    }

    static boolean ownersPresent(QuickContainerAccessSession.Session session) {
        if (session == null || !present(session.player) || !present(session.inventoryOwner)) return false;
        ServerPlayer owner = session.inventoryOwner, viewer = session.player;
        return owner == viewer || sameLevel(owner, viewer) && viewer.distanceToSqr(owner) <= 64.0D;
    }
    static boolean canNavigateBack(QuickContainerAccessSession.Session session) {
        if (session == null || !ownersPresent(session) || !session.valid()
                || session.kind != QuickContainerAccessItems.MenuKind.SHULKER) return false;
        QuickContainerAccessSession.PathNode parent = session.path.parent;
        if (parent != null && parent.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) return true;
        DdsReopenableMenu root = ROOTS.get(session.sourceRef);
        return root != null && root.ddsCanReopen(session.player);
    }

    static boolean navigateBack(ServerPlayer player, AbstractContainerMenu menu) {
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (player == null || session == null || session.player != player || !canNavigateBack(session)) return false;
        QuickContainerAccessSession.PathNode parent = session.path.parent;
        if (parent != null && parent.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST)
            return QuickContainerAccessMenus.navigateBack(player, menu, parent);
        DdsReopenableMenu root = ROOTS.get(session.sourceRef); if (root == null || !root.ddsCanReopen(player)) return false;
        if (menu != player.inventoryMenu) player.doCloseContainer();
        boolean opened = root.ddsReopen(player);
        QuickContainerAccessServerNetwork.sendNavigationState(player, false, false, !opened);
        return opened;
    }

    static void clear() { ROOTS.clear(); }
    private static boolean present(ServerPlayer player) {
        if (player == null || !player.isAlive()) return false;
        MinecraftServer server =
        //#if MC >= 12001
        //$$ player.serverLevel().getServer();
        //#else
        player.level.getServer();
        //#endif
        return server != null && server.getPlayerList().getPlayer(player.getUUID()) == player;
    }
    private static boolean sameLevel(ServerPlayer first, ServerPlayer second) {
        //#if MC >= 12000
        //$$ return first.serverLevel() == second.serverLevel();
        //#else
        return first.level == second.level;
        //#endif
    }
    //#endif
}
