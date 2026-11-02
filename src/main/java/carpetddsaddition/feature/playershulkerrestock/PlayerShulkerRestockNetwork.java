/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import carpetddsaddition.feature.playershulkerrestock.compat.PlayerShulkerRestockNetworkCompat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Stable feature facade; Minecraft-version-specific network code stays in compat. */
public final class PlayerShulkerRestockNetwork {
    private PlayerShulkerRestockNetwork() {}
    public static void registerPackets() { PlayerShulkerRestockNetworkCompat.registerPackets(); }
    public static void reset() { PlayerShulkerRestockNetworkCompat.reset(); }
    public static void onRuleChanged(MinecraftServer server) { PlayerShulkerRestockNetworkCompat.onRuleChanged(server); }
    public static boolean allows(ServerPlayer player, ItemStack stack) {
        return PlayerShulkerRestockNetworkCompat.allows(player, stack);
    }
    public static int threshold(ServerPlayer player) { return PlayerShulkerRestockNetworkCompat.threshold(player); }
}
