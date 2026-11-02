/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock.compat;

import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestock;

import carpetddsaddition.network.DdsPacketRegistry;
import carpetddsaddition.network.DdsServerNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.*;
//#if MC >= 11404 && MC <= 260300
//#if MC >= 11903
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#else
import net.minecraft.core.Registry;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
import net.minecraft.resources.ResourceLocation;
//#endif
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#endif

/** Self-only, connection-bound client preferences. Never accepts client inventory movements. */
public final class PlayerShulkerRestockNetworkCompat {
    public static final String PREFERENCES_C2S = "shulker_restock_preferences_c2s";
    public static final String STATUS_S2C = "shulker_restock_status_s2c";
    private static final int MAX_IDS = 128, MAX_ID_LENGTH = 128, MAX_ENCODED_LENGTH = 8192;
    private static final long MIN_UPDATE_NANOS = 250_000_000L;
    //#if MC >= 11404 && MC <= 260300
    private static final Map<ServerGamePacketListenerImpl, Preferences> ACTIVE = new WeakHashMap<>();
    private static final Map<ServerGamePacketListenerImpl, Long> LAST_UPDATE = new WeakHashMap<>();
    //#endif

    private PlayerShulkerRestockNetworkCompat() {}

    public static void registerPackets() {
        //#if MC >= 11404 && MC <= 260300
        DdsPacketRegistry.registerC2S(PREFERENCES_C2S, PlayerShulkerRestockNetworkCompat::receive,
                true, false, false);
        DdsPacketRegistry.registerS2C(STATUS_S2C, false);
        //#endif
    }

    public static void reset() {
        //#if MC >= 11404 && MC <= 260300
        ACTIVE.clear();
        LAST_UPDATE.clear();
        //#endif
    }

    public static void onRuleChanged(MinecraftServer server) {
        reset();
        //#if MC >= 11404 && MC <= 260300
        if (server != null) for (ServerPlayer player : server.getPlayerList().getPlayers())
            status(player, 0, false);
        //#endif
    }

    public static boolean allows(ServerPlayer player, ItemStack item) {
        //#if MC >= 11404 && MC <= 260300
        PlayerShulkerRestock.Mode mode = PlayerShulkerRestock.mode();
        if (mode == PlayerShulkerRestock.Mode.ALWAYS) return true;
        if (mode != PlayerShulkerRestock.Mode.CLIENT || player == null || player.connection == null) return false;
        Preferences prefs = ACTIVE.get(player.connection);
        if (prefs == null || !prefs.enabled || !DdsServerNetwork.doesClientSupport(player, PREFERENCES_C2S)
                || !DdsServerNetwork.doesClientSupportS2C(player, STATUS_S2C)) return false;
        String id = itemId(item);
        return "ALL".equals(prefs.filter) || "WHITELIST".equals(prefs.filter) && prefs.ids.contains(id)
                || "BLACKLIST".equals(prefs.filter) && !prefs.ids.contains(id);
        //#else
        //$$ return false;
        //#endif
    }

    public static int threshold(ServerPlayer player) {
        //#if MC >= 11404 && MC <= 260300
        if (PlayerShulkerRestock.mode() == PlayerShulkerRestock.Mode.CLIENT
                && player != null && player.connection != null) {
            Preferences prefs = ACTIVE.get(player.connection);
            if (prefs != null && prefs.enabled && DdsServerNetwork.doesClientSupport(player, PREFERENCES_C2S)
                    && DdsServerNetwork.doesClientSupportS2C(player, STATUS_S2C)) return prefs.threshold;
        }
        //#endif
        return 0;
    }

    //#if MC >= 11404 && MC <= 260300
    private static void receive(ServerPlayer player, CompoundTag data) {
        if (player == null || player.connection == null) return;
        int requestId = intOr(data, "request_id", 0);
        long now = System.nanoTime();
        Long last = LAST_UPDATE.put(player.connection, now);
        if (last != null && now - last < MIN_UPDATE_NANOS) {
            ACTIVE.remove(player.connection);
            status(player, requestId, false);
            return;
        }
        Preferences parsed = parse(data);
        // An accepted preference is not the same as a valid payload on an OFF/ALWAYS server.
        boolean authorized = parsed != null && PlayerShulkerRestock.mode() == PlayerShulkerRestock.Mode.CLIENT
                && DdsServerNetwork.doesClientSupport(player, PREFERENCES_C2S)
                && DdsServerNetwork.doesClientSupportS2C(player, STATUS_S2C);
        if (!authorized || !parsed.enabled) ACTIVE.remove(player.connection);
        else ACTIVE.put(player.connection, parsed);
        status(player, requestId, authorized);
    }

    private static Preferences parse(CompoundTag data) {
        if (data.size() > 5) return null;
        int enabled = intOr(data, "enabled", -1), threshold = intOr(data, "threshold", -1);
        String filter = stringOr(data, "filter", ""), encoded = stringOr(data, "ids", "");
        if ((enabled != 0 && enabled != 1) || threshold < 0 || threshold > 63
                || (!"ALL".equals(filter) && !"WHITELIST".equals(filter) && !"BLACKLIST".equals(filter))
                || encoded.length() > MAX_ENCODED_LENGTH) return null;
        String[] items = encoded.isEmpty() ? new String[0] : encoded.split(",", -1);
        if (items.length > MAX_IDS) return null;
        Set<String> ids = new LinkedHashSet<>();
        for (String id : items) {
            if (id.length() > MAX_ID_LENGTH || !id.matches("[a-z0-9._-]+:[a-z0-9/._-]+")) return null;
            if (!knownItem(id)) return null;
            ids.add(id);
        }
        return new Preferences(enabled == 1, filter, Collections.unmodifiableSet(ids), threshold);
    }

    private static void status(ServerPlayer player, int requestId, boolean accepted) {
        CompoundTag response = new CompoundTag();
        PlayerShulkerRestock.Mode mode = PlayerShulkerRestock.mode();
        response.putString("server_mode", mode.name().toLowerCase(Locale.ROOT));
        response.putInt("request_id", requestId);
        response.putInt("accepted", accepted ? 1 : 0);
        Preferences p = player == null || player.connection == null ? null : ACTIVE.get(player.connection);
        response.putInt("effective", mode == PlayerShulkerRestock.Mode.ALWAYS || mode == PlayerShulkerRestock.Mode.CLIENT
                && p != null && p.enabled && DdsServerNetwork.doesClientSupport(player, PREFERENCES_C2S)
                && DdsServerNetwork.doesClientSupportS2C(player, STATUS_S2C) ? 1 : 0);
        DdsServerNetwork.send(player, STATUS_S2C, response);
    }

    private static String itemId(ItemStack item) {
    //#if MC >= 11903
    //$$     return BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
    //#else
        return Registry.ITEM.getKey(item.getItem()).toString();
    //#endif
    }

    private static boolean knownItem(String id) {
        int colon = id.indexOf(':');
        String namespace = id.substring(0, colon), path = id.substring(colon + 1);
    //#if MC >= 12111
    //$$     return BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(namespace, path));
    //#elseif MC >= 12101
    //$$     return BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath(namespace, path));
    //#elseif MC >= 11903
    //$$     return BuiltInRegistries.ITEM.containsKey(new ResourceLocation(namespace, path));
    //#else
        return Registry.ITEM.containsKey(new ResourceLocation(namespace, path));
    //#endif
    }

    private static int intOr(CompoundTag data, String key, int fallback) {
    //#if MC >= 12105
    //$$     return data.getIntOr(key, fallback);
    //#else
        return data.contains(key) ? data.getInt(key) : fallback;
    //#endif
    }

    private static String stringOr(CompoundTag data, String key, String fallback) {
    //#if MC >= 12105
    //$$     return data.getStringOr(key, fallback);
    //#else
        return data.contains(key) ? data.getString(key) : fallback;
    //#endif
    }

    private static final class Preferences {
        private final boolean enabled;
        private final String filter;
        private final Set<String> ids;
        private final int threshold;
        private Preferences(boolean enabled, String filter, Set<String> ids, int threshold) {
            this.enabled = enabled; this.filter = filter; this.ids = ids; this.threshold = threshold;
        }
    }
    //#endif
}
