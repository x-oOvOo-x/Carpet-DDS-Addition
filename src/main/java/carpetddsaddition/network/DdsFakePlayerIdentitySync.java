/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.network;

import net.minecraft.server.level.ServerPlayer;
//#if MC >= 11404 && MC <= 260300
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
//#endif

/** Optional protocol capability: all online Carpet fakes, independent of inventory rules. */
public final class DdsFakePlayerIdentitySync {
    public static final String SNAPSHOT_S2C = "fake_player_identity_s2c";
    private DdsFakePlayerIdentitySync() {}

    //#if MC >= 11404 && MC <= 260300
    public static void registerPackets() { DdsPacketRegistry.registerS2C(SNAPSHOT_S2C, false); }

    public static void sendSnapshot(ServerPlayer viewer, MinecraftServer server) {
        if (viewer == null || server == null || !DdsServerNetwork.doesClientSupportS2C(viewer, SNAPSHOT_S2C)) return;
        java.util.StringJoiner uuids = new java.util.StringJoiner(",");
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            if (player instanceof EntityPlayerMPFake) uuids.add(player.getUUID().toString());
        CompoundTag data = new CompoundTag();
        data.putInt("schema", 1);
        data.putString("kind", "snapshot");
        data.putString("uuids", uuids.toString());
        DdsServerNetwork.send(viewer, SNAPSHOT_S2C, data);
    }

    public static void onPlayerJoined(ServerPlayer player) { sendChange(player, true); }
    public static void onPlayerLeft(ServerPlayer player) { sendChange(player, false); }

    private static void sendChange(ServerPlayer player, boolean joined) {
        if (!(player instanceof EntityPlayerMPFake)) return;
        MinecraftServer server = carpet.CarpetServer.minecraft_server;
        if (server == null) return;
        CompoundTag data = new CompoundTag();
        data.putInt("schema", 1);
        data.putString("kind", joined ? "add" : "remove");
        data.putString("uuid", player.getUUID().toString());
        for (ServerPlayer viewer : server.getPlayerList().getPlayers())
            if (!(viewer instanceof EntityPlayerMPFake)) DdsServerNetwork.send(viewer, SNAPSHOT_S2C, data);
    }
    //#else
    //$$ public static void registerPackets() { DdsPacketRegistry.registerS2C(SNAPSHOT_S2C, false); }
    //$$ public static void onPlayerJoined(ServerPlayer player) {}
    //$$ public static void onPlayerLeft(ServerPlayer player) {}
    //#endif
}
