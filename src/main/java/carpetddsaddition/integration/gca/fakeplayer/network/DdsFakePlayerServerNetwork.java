/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerActions;
//$$ import carpetddsaddition.network.DdsPacketRegistry;
//$$
//$$ public final class DdsFakePlayerServerNetwork {
//$$     private DdsFakePlayerServerNetwork() {}
//$$     public static void registerPackets() { DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.ACTION_C2S, DdsFakePlayerActions::handleRequest, true, false); }
//$$ }
//#else
public final class DdsFakePlayerServerNetwork {
    private DdsFakePlayerServerNetwork() {}
    public static void registerPackets() {}
}
//#endif
