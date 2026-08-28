/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import carpetddsaddition.network.DdsPacketRegistry;
import carpetddsaddition.network.DdsServerNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class QuickContainerAccessServerNetwork {
    private QuickContainerAccessServerNetwork() {}

    public static void registerPackets() {
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessServerNetwork::handleOpenSlot, false, true);
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessServerNetwork::handleOpenInventorySlot, false, true);
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessServerNetwork::handleStorageClick, false, true);
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.CREATIVE_STORAGE_C2S,
                QuickContainerAccessServerNetwork::handleCreativeStorage, true, false);
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                QuickContainerAccessServerNetwork::handleNavigateBack, false, true);
        DdsPacketRegistry.registerS2C(QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C);
        DdsPacketRegistry.registerS2C(QuickContainerAccessPackets.NAVIGATION_STATE_S2C);
    }

    private static void handleOpenSlot(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator()) return;
        int[] request = request(data); AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != request[0] || !QuickContainerAccessCompat.canUseDedicatedOpenClick(menu, request[1], player)) return;
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
        if (menu.containerId != request[0] || !QuickContainerAccessCompat.canUseDedicatedStorageClick(menu, request[1], player)) return;
        if (!QuickContainerAccessItems.isStorageHostCandidate(
                //#if MC >= 11701
                //$$ menu.getCarried()
                //#else
                player.inventory.getCarried()
                //#endif
        )) { syncCurrentMenu(player); return; }
        if (QuickContainerAccessCompat.handleInventoryClick(menu, request[1], 1, "PICKUP", player)) player.resetLastActionTime();
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

    private static void handleNavigateBack(ServerPlayer player, CompoundTag data) {
        int containerId = intValue(data, "container_id");
        if (containerId < 0 || player.isSpectator()) { sendNavigationState(player, false, false, true); return; }
        if (QuickContainerAccessCompat.navigateBack(player, containerId)) player.resetLastActionTime();
        else sendNavigationState(player, false, false, true);
    }

    public static void sendNavigationState(ServerPlayer player, boolean canBack, boolean openPlayerInventory, boolean rejected) {
        if (player == null || !DdsServerNetwork.doesClientSupportS2C(player, QuickContainerAccessPackets.NAVIGATION_STATE_S2C)) return;
        CompoundTag data = new CompoundTag();
        data.putInt("container_id", player.containerMenu.containerId);
        data.putInt("can_back", canBack ? 1 : 0);
        data.putInt("open_player_inventory", openPlayerInventory ? 1 : 0);
        data.putInt("rejected", rejected ? 1 : 0);
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
