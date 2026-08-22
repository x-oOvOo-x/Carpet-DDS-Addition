/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/** DDS feature packet registry and routing metadata. */
public final class DdsPacketRegistry {
    private static final Map<String, C2SPacket> C2S = new LinkedHashMap<>();
    private static Set<String> localC2S = Collections.emptySet();
    private DdsPacketRegistry() {}

    public static synchronized void registerC2S(String packetId, C2SHandler handler,
                                                 boolean requireClientAdvertisement, boolean allowDisabledGrace) {
        // Protocol v1 advertises packet ids as a comma-delimited string.
        if (packetId == null || packetId.isEmpty() || !packetId.equals(packetId.trim()) || packetId.indexOf(',') >= 0 || handler == null)
            throw new IllegalArgumentException("Invalid DDS C2S registration: " + packetId);
        if (DdsProtocol.HELLO_C2S.equals(packetId) || DdsProtocol.HELLO_S2C.equals(packetId))
            throw new IllegalArgumentException("DDS HELLO ids are transport-reserved: " + packetId);
        C2SPacket previous = C2S.putIfAbsent(packetId,
                new C2SPacket(packetId, handler, requireClientAdvertisement, allowDisabledGrace));
        if (previous != null) throw new IllegalStateException("Duplicate DDS C2S packet id: " + packetId);
        localC2S = Collections.unmodifiableSet(new LinkedHashSet<>(C2S.keySet()));
    }

    static synchronized C2SPacket getC2S(String packetId) { return C2S.get(packetId); }

    /** Local C2S feature capabilities in deterministic registration order. */
    public static synchronized Set<String> localC2SPackets() { return localC2S; }

    /** Protocol v1 currently has no feature-owned S2C packets. */
    public static Set<String> localS2CPackets() { return Collections.emptySet(); }

    @FunctionalInterface
    public interface C2SHandler { void handle(ServerPlayer player, CompoundTag data); }

    static final class C2SPacket {
        private final String packetId;
        private final C2SHandler handler;
        private final boolean requireClientAdvertisement, allowDisabledGrace;

        private C2SPacket(String packetId, C2SHandler handler, boolean requireClientAdvertisement, boolean allowDisabledGrace) {
            this.packetId = packetId;
            this.handler = handler;
            this.requireClientAdvertisement = requireClientAdvertisement;
            this.allowDisabledGrace = allowDisabledGrace;
        }

        String packetId() { return packetId; }
        boolean requireClientAdvertisement() { return requireClientAdvertisement; }
        boolean allowDisabledGrace() { return allowDisabledGrace; }
        void handle(ServerPlayer player, CompoundTag data) { handler.handle(player, data); }
    }
}
