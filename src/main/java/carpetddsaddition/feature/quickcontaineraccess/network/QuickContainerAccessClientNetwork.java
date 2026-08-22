/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

import carpetddsaddition.network.DdsClientNetwork;
import net.minecraft.nbt.CompoundTag;

/** Client network slice for quickContainerAccess. */
public final class QuickContainerAccessClientNetwork {
    private QuickContainerAccessClientNetwork() {}
    public static boolean canUseStorageClick() { return DdsClientNetwork.canSend(QuickContainerAccessPackets.STORAGE_CLICK_C2S); }
    public static boolean sendStorageClick(int containerId, int slotId) {
        if (!canUseStorageClick()) return false;
        CompoundTag data = new CompoundTag();
        data.putInt("container_id", containerId);
        data.putInt("slot_id", slotId);
        return DdsClientNetwork.send(QuickContainerAccessPackets.STORAGE_CLICK_C2S, data);
    }
}
