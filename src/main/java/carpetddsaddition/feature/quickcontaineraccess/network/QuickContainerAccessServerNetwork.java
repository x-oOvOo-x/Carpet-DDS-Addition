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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public final class QuickContainerAccessServerNetwork {
    private QuickContainerAccessServerNetwork() {}

    public static void registerPackets() {
        // v1: QCA predates supported_c2s advertisement; one in-flight request may finish after disable.
        DdsPacketRegistry.registerC2S(QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessServerNetwork::handleStorageClick, false, true);
    }

    private static void handleStorageClick(ServerPlayer player, CompoundTag data) {
        if (player.isSpectator()) return;
        //#if MC >= 12105
        //$$ int containerId = data.getIntOr("container_id", -1), slotId = data.getIntOr("slot_id", -1);
        //#else
        int containerId = data.contains("container_id") ? data.getInt("container_id") : -1;
        int slotId = data.contains("slot_id") ? data.getInt("slot_id") : -1;
        //#endif
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != containerId || slotId < 0 || slotId >= menu.slots.size()) return;
        Slot slot = menu.getSlot(slotId);
        // Dedicated storage-click input is restricted to the real player's inventory.
        if (slot.container
                //#if MC >= 11701
                //$$ != player.getInventory()) return;
                //#else
                != player.inventory) return;
                //#endif
        if (!QuickContainerAccessItems.isStorageHostCandidate(
                //#if MC >= 11701
                //$$ menu.getCarried()
                //#else
                player.inventory.getCarried()
                //#endif
        )) {
            syncCurrentMenu(player);
            return;
        }
        boolean handled = QuickContainerAccessCompat.handleInventoryClick(menu, slotId, 1, "PICKUP", player);
        if (handled) player.resetLastActionTime();
        syncCurrentMenu(player);
    }

    private static void syncCurrentMenu(ServerPlayer player) {
        //#if MC >= 11701
        //$$ player.containerMenu.sendAllDataToRemote();
        //#else
        player.refreshContainer(player.containerMenu);
        player.broadcastCarriedItem();
        //#endif
    }
}
