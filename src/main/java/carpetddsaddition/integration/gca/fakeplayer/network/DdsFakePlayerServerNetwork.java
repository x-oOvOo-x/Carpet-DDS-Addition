/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerActions;
//$$ import carpetddsaddition.network.DdsPacketRegistry;
//$$ import carpetddsaddition.network.DdsServerNetwork;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$
//$$ public final class DdsFakePlayerServerNetwork {
//$$     private DdsFakePlayerServerNetwork() {}
//$$     public static void registerPackets() {
//$$         DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.ACTION_C2S, DdsFakePlayerActions::handleRequest, true, false);
//$$         DdsPacketRegistry.registerS2C(DdsFakePlayerPackets.OPEN_MENU_S2C);
//$$     }
//$$     public static boolean supportsEnhancedMenu(ServerPlayer viewer) {
//$$         return DdsServerNetwork.doesClientSupport(viewer, DdsFakePlayerPackets.ACTION_C2S)
//$$                 && DdsServerNetwork.doesClientSupportS2C(viewer, DdsFakePlayerPackets.OPEN_MENU_S2C);
//$$     }
//$$     public static boolean sendOpenMenu(ServerPlayer viewer, int containerId, int layout) {
//$$         if (containerId < 0 || !DdsFakePlayerPackets.isMenuLayout(layout)) return false;
//$$         CompoundTag data = new CompoundTag();
//$$         data.putInt("container_id", containerId);
//$$         data.putInt("layout", layout);
//$$         return DdsServerNetwork.send(viewer, DdsFakePlayerPackets.OPEN_MENU_S2C, data);
//$$     }
//$$ }
//#else
public final class DdsFakePlayerServerNetwork {
    private DdsFakePlayerServerNetwork() {}
    public static void registerPackets() {}
}
//#endif
