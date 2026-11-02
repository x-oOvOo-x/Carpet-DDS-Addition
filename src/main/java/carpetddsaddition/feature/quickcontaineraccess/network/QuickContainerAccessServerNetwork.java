/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import carpetddsaddition.network.DdsPacketRegistry;
import carpetddsaddition.network.DdsServerNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class QuickContainerAccessServerNetwork {
    private static final boolean TRACE = Boolean.getBoolean("dds.playerinventory.trace");
    private static final boolean DRAG_TRACE = Boolean.getBoolean("dds.qca.drag.trace");
    private static final org.apache.logging.log4j.Logger TRACE_LOG =
            org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.qca");
    private QuickContainerAccessServerNetwork() {}

    public static void registerPackets() {
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessServerNetwork::handleOpenSlot, false, true, true, player -> QuickContainerAccess.enabled());
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessServerNetwork::handleOpenInventorySlot, false, true, true, player -> QuickContainerAccess.enabled());
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessServerNetwork::handleStorageClick, false, true, true, player -> QuickContainerAccess.enabled());
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.CREATIVE_STORAGE_C2S,
                QuickContainerAccessServerNetwork::handleCreativeStorage, true, false, true, player -> QuickContainerAccess.enabled());
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                QuickContainerAccessServerNetwork::handleNavigateBack, false, true, true,
                player -> PlayerInventoryExtension.enabled() || QuickContainerAccess.enabled());
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S,
                QuickContainerAccessServerNetwork::handleOpenOwnerEnder, false, true, false,
                player -> PlayerInventoryExtension.enabled());
        DdsPacketRegistry.registerS2C(QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C);
        DdsPacketRegistry.registerS2C(QuickContainerAccessPackets.NAVIGATION_STATE_S2C);
    }

    private static void handleOpenSlot(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator()) return;
        int[] request = request(data); AbstractContainerMenu menu = player.containerMenu;
        String denial = menu.containerId != request[0] ? "stale_container"
                : QuickContainerAccessCompat.dedicatedOpenRejection(menu, request[1], player);
        boolean accepted = denial == null;
        if (TRACE) TRACE_LOG.info("qca_open_request source_id=" + request[0]
                + " current_id=" + menu.containerId + " slot=" + request[1]
                + " menu_type=" + menu.getClass().getName() + " accepted=" + accepted
                + " denial=" + denial + " total_slots=" + menu.slots.size());
        if (!accepted) return;
        if (QuickContainerAccessCompat.handleInventoryClick(menu, request[1], 1, "PICKUP", player)) player.resetLastActionTime();
        syncCurrentMenu(player);
    }

    private static void handleOpenInventorySlot(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator()) return;
        int slot = intValue(data, "inventory_slot");
        if (QuickContainerAccessCompat.openPlayerInventorySlot(player, slot)) player.resetLastActionTime();
    }

    private static void handleStorageClick(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator()) return;
        int[] request = request(data); AbstractContainerMenu menu = player.containerMenu;
        String denial = menu.containerId != request[0] ? "stale_container"
                : QuickContainerAccessCompat.dedicatedStorageRejection(menu, request[1], player);
        boolean eligible = denial == null;
        if (DRAG_TRACE) TRACE_LOG.info("qca_drag_server stage=received menu={} id={} request_id={} slot={} eligible={} denial={} total_slots={}",
                menu.getClass().getSimpleName(), menu.containerId, request[0], request[1],
                eligible, denial, menu.slots.size());
        if (!eligible) return;
        if (!QuickContainerAccessCompat.carriedStorageHostCandidate(menu, player)) {
            if (DRAG_TRACE) TRACE_LOG.info("qca_drag_server stage=ignored slot={} reason=no_carried_host", request[1]);
            syncCurrentMenu(player); return;
        }
        boolean handled = QuickContainerAccessCompat.handleInventoryClick(menu, request[1], 1, "PICKUP", player);
        if (DRAG_TRACE) TRACE_LOG.info("qca_drag_server stage=processed slot={} handled={}", request[1], handled);
        if (handled) player.resetLastActionTime();
        syncCurrentMenu(player);
    }

    private static void handleCreativeStorage(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator() || !player.isCreative()
                || !DdsServerNetwork.doesClientSupportS2C(player, QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C)) return;
        int transaction = intValue(data, "transaction");
        int inventorySlot = intValue(data, "inventory_slot");
        if (transaction < 0) return;
        ItemStack carried = QuickContainerAccessStackCodec.get(data, "carried", player);
        ItemStack result = QuickContainerAccessCompat.handleCreativeStorageClick(player, inventorySlot, carried);
        boolean handled = result != null;
        if (handled) player.resetLastActionTime();
        else result = carried;
        CompoundTag reply = new CompoundTag();
        reply.putInt("transaction", transaction);
        reply.putInt("handled", handled ? 1 : 0);
        QuickContainerAccessStackCodec.put(reply, "carried", player, result);
        DdsServerNetwork.send(player, QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C, reply);
    }

    private static void handleOpenOwnerEnder(ServerPlayer player, CompoundTag data) {
        int containerId = intValue(data, "container_id");
        if (containerId >= 0 && QuickContainerAccessCompat.openRemoteOwnerEnder(player, containerId)) {
            player.resetLastActionTime();
            return;
        }
        sendOwnerEnderRejected(player, containerId);
    }

    private static void sendOwnerEnderRejected(ServerPlayer player, int requestContainerId) {
        if (player == null || !DdsServerNetwork.doesClientSupportS2C(player, QuickContainerAccessPackets.NAVIGATION_STATE_S2C)) return;
        CompoundTag reply = new CompoundTag();
        reply.putInt("container_id", player.containerMenu.containerId);
        reply.putInt("can_back", QuickContainerAccessCompat.canNavigateBack(player.containerMenu, player) ? 1 : 0);
        reply.putInt("open_player_inventory", 0);
        reply.putInt("rejected", 0);
        reply.putInt("owner_ender_rejected", 1);
        reply.putInt("request_container_id", requestContainerId);
        DdsServerNetwork.send(player, QuickContainerAccessPackets.NAVIGATION_STATE_S2C, reply);
    }

    private static void handleNavigateBack(ServerPlayer player, CompoundTag data) {
        int containerId = intValue(data, "container_id");
        if (TRACE) TRACE_LOG.info("qca_back_request id=" + containerId
                + " current_id=" + player.containerMenu.containerId + " spectator=" + player.isSpectator());
        if (containerId < 0 || player.isSpectator()) { sendNavigationState(player, false, false, true); return; }
        boolean opened = QuickContainerAccessCompat.navigateBack(player, containerId);
        if (TRACE) TRACE_LOG.info("qca_back_request_result id=" + containerId
                + " opened=" + opened + " current_id=" + player.containerMenu.containerId);
        if (opened) player.resetLastActionTime();
        else sendNavigationState(player, false, false, true);
    }

    public static void sendNavigationState(ServerPlayer player, boolean canBack, boolean openPlayerInventory, boolean rejected) {
        if (player == null || !DdsServerNetwork.doesClientSupportS2C(player, QuickContainerAccessPackets.NAVIGATION_STATE_S2C)) return;
        CompoundTag data = new CompoundTag();
        data.putInt("container_id", player.containerMenu.containerId);
        data.putInt("can_back", canBack ? 1 : 0);
        data.putInt("open_player_inventory", openPlayerInventory ? 1 : 0);
        data.putInt("rejected", rejected ? 1 : 0);
        if (TRACE) TRACE_LOG.info("qca_navigation_state id=" + player.containerMenu.containerId
                + " can_back=" + canBack + " open_inventory=" + openPlayerInventory
                + " rejected=" + rejected);
        if (!rejected && !openPlayerInventory) {
            ServerPlayer owner = QuickContainerAccessCompat.fakeEnderOwner(player);
            if (owner != null) {
                data.putString("fake_ender_owner_uuid", owner.getUUID().toString());
                data.putString("fake_ender_owner_name", owner.getName().getString());
            }
        }
        DdsServerNetwork.send(player, QuickContainerAccessPackets.NAVIGATION_STATE_S2C, data);
    }

    private static int[] request(CompoundTag data) { return new int[]{intValue(data, "container_id"), intValue(data, "slot_id")}; }
    private static int intValue(CompoundTag data, String key) {
        //#if MC >= 12105
        //$$ return data.getIntOr(key, -1);
        //#else
        return data.contains(key) ? data.getInt(key) : -1;
        //#endif
    }
    private static void syncCurrentMenu(ServerPlayer player) {
        //#if MC >= 11701
        //$$ player.containerMenu.sendAllDataToRemote();
        //#else
        player.refreshContainer(player.containerMenu); player.broadcastCarriedItem();
        //#endif
    }
}
