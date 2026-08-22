/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerAction;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.network.DdsClientNetwork;
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

/** Client network slice for the enhanced GCA fake-player controls. */
public final class DdsFakePlayerClientNetwork {
    private DdsFakePlayerClientNetwork() {}

    public static boolean canUseActions() {
        //#if MC >= 11902 && MC <= 260200
        //$$ return DdsClientNetwork.canSend(DdsFakePlayerPackets.ACTION_C2S);
        //#else
        return false;
        //#endif
    }

    public static boolean sendAction(DdsFakePlayerAction action, int value) {
        //#if MC >= 11902 && MC <= 260200
        //$$ if (!canUseActions() || action == null) return false;
        //$$ CompoundTag data = new CompoundTag();
        //$$ data.putString("action", action.wireName());
        //$$ data.putInt("value", value);
        //$$ return DdsClientNetwork.send(DdsFakePlayerPackets.ACTION_C2S, data);
        //#else
        return false;
        //#endif
    }
}
