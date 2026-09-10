/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.client;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerMenuOpenState;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
//$$ import carpetddsaddition.network.DdsClientNetwork;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.multiplayer.ClientPacketListener;
//$$ import net.minecraft.nbt.CompoundTag;
//$$
//$$ public final class DdsFakePlayerClientNetwork {
//$$     private static final DdsFakePlayerMenuOpenState<ClientPacketListener> OPEN_STATE =
//$$             new DdsFakePlayerMenuOpenState<>(5_000_000_000L);
//$$     private static boolean initialized;
//$$     private DdsFakePlayerClientNetwork() {}
//$$
//$$     public static synchronized void initialize() {
//$$         if (initialized) return;
//$$         DdsClientNetwork.registerOrderedS2CHandler(DdsFakePlayerPackets.OPEN_MENU_S2C, DdsFakePlayerClientNetwork::handleOpenMenu);
//$$         initialized = true;
//$$     }
//$$
//$$     private static void handleOpenMenu(ClientPacketListener connection, CompoundTag data) {
//#if MC >= 12105
//$$         int containerId = data.getIntOr("container_id", -1);
//$$         int layout = data.getIntOr("layout", DdsFakePlayerPackets.MENU_NONE);
//#else
//$$         int containerId = data.getInt("container_id");
//$$         int layout = data.getInt("layout");
//#endif
//$$         OPEN_STATE.expect(connection, containerId, layout, System.nanoTime());
//$$     }
//$$
//$$     public static int consumeOpenMenu(int containerId) {
//$$         return OPEN_STATE.consume(Minecraft.getInstance().getConnection(), containerId, System.nanoTime());
//$$     }
//$$ }
//#else
public final class DdsFakePlayerClientNetwork {
    private DdsFakePlayerClientNetwork() {}
    public static void initialize() {}
}
//#endif
