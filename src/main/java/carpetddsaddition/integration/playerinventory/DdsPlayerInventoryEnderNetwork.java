/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.playerinventory;

import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
import carpetddsaddition.network.DdsPacketRegistry;

//#if MC >= 12111 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsReopenableMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import carpetddsaddition.network.DdsServerNetwork;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.SimpleMenuProvider;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.ChestMenu;
//$$ import net.minecraft.world.item.ItemStack;
//#if MC >= 260300
//$$ import net.minecraft.util.Prediction;
//#endif
//$$ import java.util.UUID;
//#endif

/** Strict structural RPCs owned by PlayerInventoryExtension, independent from QCA transport/session state. */
public final class DdsPlayerInventoryEnderNetwork {
    private static final boolean TRACE = Boolean.getBoolean("dds.playerinventory.trace");
    private static final org.apache.logging.log4j.Logger TRACE_LOG =
            org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.cda");
    private static final DdsPlayerInventoryStructureRequestGuard.Sequence REQUESTS =
            new DdsPlayerInventoryStructureRequestGuard.Sequence();

    private DdsPlayerInventoryEnderNetwork() {}

    public static void registerPackets() {
        //#if MC >= 12111 && MC <= 260300
        //$$ DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.CANONICAL_ENDER_C2S,
        //$$         DdsPlayerInventoryEnderNetwork::handleCanonicalEnder, true, false, false,
        //$$         player -> PlayerInventoryExtension.enabled());
        //$$ DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.RETURN_PARENT_C2S,
        //$$         DdsPlayerInventoryEnderNetwork::handleReturnParent, true, false, false,
        //$$         player -> PlayerInventoryExtension.enabled());
        //$$ DdsPacketRegistry.registerS2C(DdsFakePlayerPackets.STRUCTURE_RESULT_S2C, false);
        //#endif
    }

    //#if MC >= 12111 && MC <= 260300
    //$$ private static void handleCanonicalEnder(ServerPlayer viewer, CompoundTag data) {
    //$$     Request request = Request.parse(data);
    //$$     if (TRACE) TRACE_LOG.info("canonical_rx source="
    //$$             + (viewer == null || viewer.containerMenu == null ? "none" : viewer.containerMenu.getClass().getSimpleName())
    //$$             + " source_id=" + (viewer == null || viewer.containerMenu == null ? -1 : viewer.containerMenu.containerId)
    //$$             + " requested_id=" + (request == null ? -1 : request.containerId)
    //$$             + " generation=" + (request == null ? -1L : request.sessionGeneration));
    //$$     if (!begin(viewer, request)) return;
    //$$     DdsPlayerInventorySession session = DdsPlayerInventorySessions.current(
    //$$             viewer, request.containerId, request.targetUuid, request.sessionGeneration);
    //$$     if (session == null) { rejectTransition(viewer, request, "stale_session"); return; }
    //$$     if (viewer.isSpectator() || session.inventoryKind() != DdsPlayerInventorySession.InventoryKind.ENDER_CHEST) {
    //$$         rejectTransition(viewer, request, "permission_denied"); return;
    //$$     }
    //$$     if (session.targetType() == DdsPlayerInventorySession.TargetType.OFFLINE_PLAYER) {
    //$$         rejectTransition(viewer, request, "unsupported_target"); return;
    //$$     }
    //$$     if (session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_FAKE
    //$$             && session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_PLAYER) {
    //$$         rejectTransition(viewer, request, "invalid_target"); return;
    //$$     }
    //$$     ServerPlayer target = session.liveTarget();
    //$$     if (target == null) { rejectTransition(viewer, request, "invalid_target"); return; }
    //$$     Container canonical = session.canonicalContainer();
    //$$     if (!target.isAlive() || canonical == null || canonical != target.getEnderChestInventory()) {
    //$$         rejectTransition(viewer, request, "invalid_target"); return;
    //$$     }
    //$$     int size = canonical.getContainerSize();
    //$$     if (!DdsPlayerInventoryStructureRequestGuard.canonicalEnderCapacityMatches(
    //$$             size, session.canonicalSize())) {
    //$$         rejectTransition(viewer, request, "invalid_capacity"); return;
    //$$     }
    //$$     AbstractContainerMenu source = viewer.containerMenu;
    //$$     if (source instanceof DdsPlayerInventoryCanonicalEnderMenu) {
    //$$         rejectTransition(viewer, request, "busy"); return;
    //$$     }
    //$$     DdsPlayerInventoryCanonicalEnderMenu.ProviderSlotPolicy[] policies =
    //$$             DdsPlayerInventoryCanonicalEnderMenu.captureProviderSlotPolicy(
    //$$                     source, viewer, target, session.providerId(), canonical);
    //$$     if (policies == null) { rejectTransition(viewer, request, "permission_denied"); return; }
    //$$     DdsReopenableMenu root = DdsPlayerInventoryRootReturns.peek(viewer, session);
    //$$     ItemStack carried = source.getCarried().copy();
    //$$     source.setCarried(ItemStack.EMPTY);
    //$$     boolean opened;
    //$$     try {
    //$$         opened = viewer.openMenu(new SimpleMenuProvider((id, inventory, player) -> {
    //$$             DdsPlayerInventoryCanonicalEnderMenu menu = DdsPlayerInventoryCanonicalEnderMenu.create(
    //$$                     id, inventory, viewer, target, session.providerId(), source, policies, root);
    //$$             if (menu != null) DdsFakePlayerServerNetwork.sendContextFromMenu(viewer, menu);
    //$$             return menu;
    //$$         }, DdsPlayerInventoryCanonicalEnderMenu.providerTitle(session.providerId(), target))).isPresent();
    //$$     } catch (RuntimeException ignored) { opened = false; }
    //$$     if (!opened) {
    //$$         restoreCarried(viewer, source, carried);
    //$$         rejectTransition(viewer, request, "open_failed");
    //$$         return;
    //$$     }
    //$$     viewer.containerMenu.setCarried(carried);
    //$$     viewer.containerMenu.sendAllDataToRemote();
    //$$     if (root != null) DdsPlayerInventoryRootReturns.consume(viewer, root);
    //$$     viewer.resetLastActionTime();
    //$$     accept(viewer, request);
    //$$ }
    //$$
    //$$ private static void handleReturnParent(ServerPlayer viewer, CompoundTag data) {
    //$$     Request request = Request.parse(data);
    //$$     if (!begin(viewer, request)) return;
    //$$     DdsPlayerInventorySession session = DdsPlayerInventorySessions.current(
    //$$             viewer, request.containerId, request.targetUuid, request.sessionGeneration);
    //$$     if (session == null) { reject(viewer, request, "stale_session"); return; }
    //$$     if (viewer.isSpectator() || session.inventoryKind() != DdsPlayerInventorySession.InventoryKind.ENDER_CHEST
    //$$             || !(viewer.containerMenu instanceof DdsPlayerInventoryCanonicalEnderMenu)) {
    //$$         reject(viewer, request, "permission_denied"); return;
    //$$     }
    //$$     DdsReopenableMenu root = session.rootReturn();
    //$$     if (root == null || !root.ddsCanReopen(viewer)) {
    //$$         reject(viewer, request, "permission_denied"); return;
    //$$     }
    //$$     AbstractContainerMenu source = viewer.containerMenu;
    //$$     ItemStack carried = source.getCarried().copy();
    //$$     source.setCarried(ItemStack.EMPTY);
    //$$     boolean opened;
    //$$     try { opened = root.ddsReopen(viewer); }
    //$$     catch (RuntimeException ignored) { opened = false; }
    //$$     if (!opened) {
    //$$         restoreCarried(viewer, source, carried);
    //$$         reject(viewer, request, "open_failed");
    //$$         return;
    //$$     }
    //$$     viewer.containerMenu.setCarried(carried);
    //$$     viewer.containerMenu.sendAllDataToRemote();
    //$$     viewer.resetLastActionTime();
    //$$     accept(viewer, request);
    //$$ }
    //$$
    //$$ private static boolean begin(ServerPlayer viewer, Request request) {
    //$$     if (viewer == null || viewer.connection == null || request == null) return false;
    //$$     if (!strictNegotiated(viewer)) { reject(viewer, request, "permission_denied"); return false; }
    //$$     if (!REQUESTS.accept(viewer.connection, request.requestId)) {
    //$$         reject(viewer, request, "stale_session"); return false;
    //$$     }
    //$$     return true;
    //$$ }
    //$$
    //$$ private static boolean strictNegotiated(ServerPlayer viewer) {
    //$$     return DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C)
    //$$             && DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.STRUCTURE_RESULT_S2C);
    //$$ }
    //$$
    //$$ private static void restoreCarried(ServerPlayer viewer, AbstractContainerMenu source, ItemStack carried) {
    //$$     if (carried == null || carried.isEmpty()) return;
    //$$     if (viewer.containerMenu == source) {
    //$$         source.setCarried(carried);
    //$$         source.sendAllDataToRemote();
    //$$         return;
    //$$     }
    //$$     ItemStack remaining = carried.copy();
    //$$     viewer.getInventory().add(remaining);
    //#if MC >= 260300
    //$$     if (!remaining.isEmpty()) viewer.drop(remaining, false, Prediction.SERVER_ONLY);
    //#else
    //$$     if (!remaining.isEmpty()) viewer.drop(remaining, false, true);
    //#endif
    //$$ }
    //$$
    //$$ private static void accept(ServerPlayer viewer, Request request) { result(viewer, request, "accepted", ""); }
    //$$ private static void rejectTransition(ServerPlayer viewer, Request request, String reason) {
    //$$     if (request != null) DdsPlayerInventoryRootReturns.reject(viewer, request.sessionGeneration);
    //$$     reject(viewer, request, reason);
    //$$ }
    //$$ private static void reject(ServerPlayer viewer, Request request, String reason) {
    //$$     result(viewer, request, "rejected", reason);
    //$$ }
    //$$ private static void result(ServerPlayer viewer, Request request, String status, String reason) {
    //$$     if (TRACE) TRACE_LOG.info("structure_result status=" + status + " reason=" + reason
    //$$             + " requested_id=" + (request == null ? -1 : request.containerId)
    //$$             + " generation=" + (request == null ? -1L : request.sessionGeneration)
    //$$             + " current_menu=" + (viewer == null || viewer.containerMenu == null ? "none"
    //$$                     : viewer.containerMenu.getClass().getSimpleName()));
    //$$     if (viewer == null || request == null
    //$$             || !DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.STRUCTURE_RESULT_S2C)) return;
    //$$     CompoundTag reply = new CompoundTag();
    //$$     reply.putInt("container_id", request.containerId);
    //$$     reply.putString("target_uuid", request.targetUuid == null ? "" : request.targetUuid.toString());
    //$$     reply.putLong("session_generation", request.sessionGeneration);
    //$$     reply.putLong("request_id", request.requestId);
    //$$     reply.putString("status", status);
    //$$     reply.putString("reason", reason);
    //$$     DdsServerNetwork.send(viewer, DdsFakePlayerPackets.STRUCTURE_RESULT_S2C, reply);
    //$$ }
    //$$
    //$$ public static void onDisconnect(ServerPlayer viewer) {
    //$$     if (viewer == null) return;
    //$$     REQUESTS.clear(viewer.connection);
    //$$     DdsPlayerInventorySessions.clearViewer(viewer);
    //$$ }
    //$$
    //$$ public static void clearRuntimeState() {
    //$$     REQUESTS.clear();
    //$$     DdsPlayerInventoryRootReturns.clear();
    //$$ }
    //$$
    //$$ private static final class Request {
    //$$     private final int containerId;
    //$$     private final UUID targetUuid;
    //$$     private final long sessionGeneration, requestId;
    //$$     private Request(int containerId, UUID targetUuid, long sessionGeneration, long requestId) {
    //$$         this.containerId = containerId;
    //$$         this.targetUuid = targetUuid;
    //$$         this.sessionGeneration = sessionGeneration;
    //$$         this.requestId = requestId;
    //$$     }
    //$$     private static Request parse(CompoundTag data) {
    //$$         if (data == null) return null;
    //$$         int containerId = data.getIntOr("container_id", -1);
    //$$         String rawTarget = data.getStringOr("target_uuid", "");
    //$$         long generation = data.getLongOr("session_generation", -1L);
    //$$         long requestId = data.getLongOr("request_id", -1L);
    //$$         UUID target;
    //$$         try { target = UUID.fromString(rawTarget); }
    //$$         catch (IllegalArgumentException ignored) { return null; }
    //$$         if (!DdsPlayerInventoryStructureRequestGuard.requestFieldsValid(
    //$$                 containerId, target, generation, requestId)) return null;
    //$$         return new Request(containerId, target, generation, requestId);
    //$$     }
    //$$ }
    //#else
    public static void onDisconnect(Object viewer) {}
    public static void clearRuntimeState() { REQUESTS.clear(); }
    //#endif
}
