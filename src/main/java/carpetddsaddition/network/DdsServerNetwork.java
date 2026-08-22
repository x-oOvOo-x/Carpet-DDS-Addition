/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.CarpetDDSAddition;
import carpetddsaddition.generated.DDSRules;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//#else
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//#endif
//#endif

/** Server-side DDS protocol dispatcher. */
public final class DdsServerNetwork {
    //#if MC >= 11404 && MC <= 260200
    private static final Map<ServerGamePacketListenerImpl, ClientState> CLIENTS = new WeakHashMap<>();
    private static WeakReference<MinecraftServer> boundServer = new WeakReference<>(null);
    private static boolean observedProtocolEnabled;
    //#endif

    private DdsServerNetwork() {}

    //#if MC >= 11404 && MC <= 260200
    public static boolean doesClientSupport(ServerPlayer player, String packetId) {
        if (!DDSRules.ddsNetworkProtocol() || player == null || player.connection == null) return false;
        ClientState state = CLIENTS.get(player.connection);
        return state != null && DdsServerRoutePolicy.isProtocolCompatible(state.protocolVersion) && state.supportedC2S.contains(packetId);
    }

    public static void syncProtocolRuleState(MinecraftServer server) {
        if (server == null) return;
        bindServer(server);
        boolean enabled = DDSRules.ddsNetworkProtocol();
        if (enabled == observedProtocolEnabled) return;
        observedProtocolEnabled = enabled;
        // Copy weak keys before sending so stale-key expunging cannot perturb iteration.
        for (ServerGamePacketListenerImpl listener : new ArrayList<>(CLIENTS.keySet())) {
            if (listener != null) {
                sendHello(listener, enabled);
            }
        }
    }

    public static void resetRuntimeState() {
        CLIENTS.clear();
        boundServer = new WeakReference<>(null);
        observedProtocolEnabled = false;
    }

    public static void handlePayload(ServerGamePacketListenerImpl listener, DdsProtocol.Payload payload) {
        MinecraftServer server =
    //#if MC >= 12001
    //$$         listener.player.serverLevel()
    //#else
                listener.player.level
    //#endif
                        .getServer();
        server.execute(() -> {
            bindServer(server);
            dispatchEnabledOnServerThread(listener, payload);
        });
    }

    public static void handlePayloadWhileDisabled(ServerGamePacketListenerImpl listener, DdsProtocol.Payload payload) {
        MinecraftServer server =
    //#if MC >= 12001
    //$$         listener.player.serverLevel()
    //#else
                listener.player.level
    //#endif
                        .getServer();
        server.execute(() -> {
            bindServer(server);
            dispatchDisabledOnServerThread(listener, payload);
        });
    }

    private static void bindServer(MinecraftServer server) {
        if (boundServer.get() == server) return;
        // Integrated servers may be replaced in the same JVM; never carry session state across worlds.
        boundServer = new WeakReference<>(server);
        CLIENTS.clear();
        observedProtocolEnabled = DDSRules.ddsNetworkProtocol();
    }

    private static void dispatchEnabledOnServerThread(ServerGamePacketListenerImpl listener, DdsProtocol.Payload payload) {
        String packetId = payload.packetId();
        if (DdsProtocol.HELLO_C2S.equals(packetId)) {
            ClientState controlState = clientState(listener);
            if (!controlState.allowControlTraffic()) return;
            handleHello(listener, payload.data(), true, controlState);
            return;
        }
        ClientState state = CLIENTS.get(listener);
        if (state == null || !DdsServerRoutePolicy.isProtocolCompatible(state.protocolVersion) || !state.allowRequest()) return;
        DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(packetId);
        if (!DdsServerRoutePolicy.canDispatchEnabled(packet, state.supportedC2S)) return;
        packet.handle(listener.player, payload.data());
    }

    private static void dispatchDisabledOnServerThread(ServerGamePacketListenerImpl listener, DdsProtocol.Payload payload) {
        String packetId = payload.packetId();
        if (DdsProtocol.HELLO_C2S.equals(packetId)) {
            ClientState controlState = clientState(listener);
            if (!controlState.allowControlTraffic()) return;
            handleHello(listener, payload.data(), false, controlState);
            return;
        }
        ClientState previous = CLIENTS.get(listener);
        DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(packetId);
        // Generic form of the historical QCA one-packet disabled grace.
        if (previous != null && DdsServerRoutePolicy.isProtocolCompatible(previous.protocolVersion)
                && DdsServerRoutePolicy.canDispatchDisabledGrace(packet, previous.supportedC2S) && previous.allowRequest())
            packet.handle(listener.player, payload.data());
        // Preserve fail-closed HELLO feedback, but bound client-triggered control traffic.
        ClientState controlState = previous != null ? previous : clientState(listener);
        if (controlState.allowControlTraffic()) sendHello(listener, false);
    }

    /** Returns connection-scoped state. Re-handshake updates capabilities without resetting either limiter. */
    private static ClientState clientState(ServerGamePacketListenerImpl listener) {
        ClientState state = CLIENTS.get(listener);
        if (state == null) {
            state = new ClientState();
            CLIENTS.put(listener, state);
        }
        return state;
    }

    private static void handleHello(ServerGamePacketListenerImpl listener, CompoundTag data, boolean enabled, ClientState state) {
    //#if MC >= 12105
    //$$ int protocol = data.getIntOr("protocol", 0);
    //$$ String supportedS2CEncoded = data.getStringOr("supported_s2c", "");
    //$$ String supportedC2SEncoded = data.getStringOr("supported_c2s", "");
    //#else
        int protocol = data.getInt("protocol");
        String supportedS2CEncoded = data.getString("supported_s2c");
        String supportedC2SEncoded = data.contains("supported_c2s") ? data.getString("supported_c2s") : "";
    //#endif
        Set<String> supportedS2C = DdsProtocol.decodeKnownIds(supportedS2CEncoded, DdsPacketRegistry.localS2CPackets());
        Set<String> supportedC2S = DdsProtocol.decodeKnownIds(supportedC2SEncoded, DdsPacketRegistry.localC2SPackets());
        state.updateHandshake(protocol, supportedS2C, supportedC2S);
        sendHello(listener, enabled);
    }

    private static void sendHello(ServerGamePacketListenerImpl listener, boolean enabled) {
        CompoundTag reply = new CompoundTag();
        reply.putInt("protocol", DdsProtocol.VERSION);
        reply.putInt("protocol_enabled", enabled ? 1 : 0);
        reply.putString("mod_version", CarpetDDSAddition.getVersion());
        reply.putString("supported_c2s", enabled ? DdsProtocol.encodeIds(DdsPacketRegistry.localC2SPackets()) : "");
    //#if MC >= 12002
    //$$ listener.send(new ClientboundCustomPayloadPacket(new DdsProtocol.Payload(DdsProtocol.HELLO_S2C, reply)));
    //#else
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        new DdsProtocol.Payload(DdsProtocol.HELLO_S2C, reply).write(buf);
        listener.send(new ClientboundCustomPayloadPacket(DdsProtocol.CHANNEL, buf));
    //#endif
    }

    private static final class ClientState {
        private static final int MAX_REQUESTS_PER_SECOND = 160, MAX_CONTROL_REQUESTS_PER_SECOND = 8;
        private int protocolVersion;
        @SuppressWarnings("unused")
        private Set<String> supportedS2C = Collections.emptySet();
        private Set<String> supportedC2S = Collections.emptySet();
        private final FixedWindowLimiter requestLimiter = new FixedWindowLimiter(MAX_REQUESTS_PER_SECOND);
        private final FixedWindowLimiter controlLimiter = new FixedWindowLimiter(MAX_CONTROL_REQUESTS_PER_SECOND);

        private void updateHandshake(int protocolVersion, Set<String> supportedS2C, Set<String> supportedC2S) {
            this.protocolVersion = protocolVersion;
            this.supportedS2C = immutableCopy(supportedS2C);
            this.supportedC2S = immutableCopy(supportedC2S);
        }
        private static Set<String> immutableCopy(Set<String> values) {
            return values == null || values.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(values));
        }
        private boolean allowRequest() { return requestLimiter.allow(); }
        private boolean allowControlTraffic() { return controlLimiter.allow(); }
    }

    /** Small connection-local fixed-window limiter; all access occurs on the server thread. */
    private static final class FixedWindowLimiter {
        private static final long WINDOW_NANOS = 1_000_000_000L;
        private final int maxRequests;
        private long windowStartNanos = System.nanoTime();
        private int requestsInWindow;

        private FixedWindowLimiter(int maxRequests) {
            if (maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be positive");
            this.maxRequests = maxRequests;
        }
        private boolean allow() {
            long now = System.nanoTime();
            if (now - windowStartNanos >= WINDOW_NANOS) {
                windowStartNanos = now;
                requestsInWindow = 0;
            }
            return ++requestsInWindow <= maxRequests;
        }
    }
    //#endif
}
