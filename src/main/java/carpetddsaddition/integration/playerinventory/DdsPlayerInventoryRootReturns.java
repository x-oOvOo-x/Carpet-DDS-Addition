/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.playerinventory;

import carpetddsaddition.core.inventory.DdsReopenableMenu;
import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * One-shot Provider inventory roots captured only by an actual Inventory -> Ender navigation.
 * A direct Provider Ender open never creates an entry here.
 */
public final class DdsPlayerInventoryRootReturns {
    private static final Map<ServerPlayer, Capture> CAPTURES = new WeakHashMap<>();
    private DdsPlayerInventoryRootReturns() {}

    public static void capture(ServerPlayer viewer, AbstractContainerMenu sourceMenu,
                               ServerPlayer target, DdsReopenableMenu root) {
        //#if MC >= 12111 && MC <= 260300
        //$$ if (!PlayerInventoryExtension.enabled() || viewer == null || sourceMenu == null || target == null || root == null
        //$$         || viewer.connection == null || viewer.containerMenu != sourceMenu || !root.ddsCanReopen(viewer)) return;
        //$$ DdsPlayerInventorySession source = DdsPlayerInventorySessions.current(viewer, sourceMenu.containerId, target.getUUID());
        //$$ if (source == null || source.inventoryKind() != DdsPlayerInventorySession.InventoryKind.INVENTORY
        //$$         || source.liveTarget() != target || source.viewerConnection() != viewer.connection) return;
        //$$ CAPTURES.put(viewer, new Capture(viewer.connection, source.providerId(), target, source.generation(), root));
        //#endif
    }

    static DdsReopenableMenu peek(ServerPlayer viewer, DdsPlayerInventorySession enderSession) {
        //#if MC >= 12111 && MC <= 260300
        //$$ Capture capture = CAPTURES.get(viewer);
        //$$ if (capture == null || enderSession == null || viewer == null) return null;
        //$$ boolean rootReopenable = capture.root.ddsCanReopen(viewer);
        //$$ if (!DdsPlayerInventoryStructureRequestGuard.parentMatches(
        //$$         capture.connection, viewer.connection,
        //$$         capture.sourceProvider, enderSession.providerId(),
        //$$         capture.sourceTarget, enderSession.liveTarget(),
        //$$         capture.sourceGeneration, enderSession.generation(), rootReopenable)) return null;
        //$$ return capture.root;
        //#else
        return null;
        //#endif
    }

    static void consume(ServerPlayer viewer, DdsReopenableMenu root) {
        if (viewer == null || root == null) return;
        Capture capture = CAPTURES.get(viewer);
        if (capture != null && capture.root == root) CAPTURES.remove(viewer);
    }

    static void reject(ServerPlayer viewer, long requestGeneration) {
        if (viewer == null) return;
        Capture capture = CAPTURES.get(viewer);
        if (capture != null && DdsPlayerInventoryStructureRequestGuard.nextGenerationMatches(
                capture.sourceGeneration, requestGeneration)) CAPTURES.remove(viewer);
    }

    public static void clear(ServerPlayer viewer) {
        if (viewer != null) CAPTURES.remove(viewer);
    }

    public static void clear() { CAPTURES.clear(); }

    private static final class Capture {
        private final Object connection;
        private final String sourceProvider;
        private final ServerPlayer sourceTarget;
        private final long sourceGeneration;
        private final DdsReopenableMenu root;

        private Capture(Object connection, String sourceProvider, ServerPlayer sourceTarget,
                        long sourceGeneration, DdsReopenableMenu root) {
            this.connection = connection;
            this.sourceProvider = sourceProvider;
            this.sourceTarget = sourceTarget;
            this.sourceGeneration = sourceGeneration;
            this.root = root;
        }
    }
}
