/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.CarpetDDSAddition;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#else
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#endif
//#endif

/** Client-side DDS transport and negotiated protocol session. */
public final class DdsClientNetwork {
    private static final long HELLO_RETRY_NANOS = 1_000_000_000L;
    private static final DdsClientState STATE = new DdsClientState();
    private static final Map<String, Consumer<CompoundTag>> S2C_HANDLERS = new LinkedHashMap<>();
    private static final Map<String, BiConsumer<ClientPacketListener, CompoundTag>> ORDERED_S2C_HANDLERS = new LinkedHashMap<>();
    private DdsClientNetwork() {}

    public static synchronized void registerS2CHandler(String packetId, Consumer<CompoundTag> handler) {
        validateHandler(packetId, handler);
        if (ORDERED_S2C_HANDLERS.containsKey(packetId) || S2C_HANDLERS.putIfAbsent(packetId, handler) != null)
            throw new IllegalStateException("Duplicate DDS S2C client handler: " + packetId);
    }

    /** Registers a lightweight handler that must run in wire order before the next vanilla packet. */
    public static synchronized void registerOrderedS2CHandler(String packetId, BiConsumer<ClientPacketListener, CompoundTag> handler) {
        validateHandler(packetId, handler);
        if (S2C_HANDLERS.containsKey(packetId) || ORDERED_S2C_HANDLERS.putIfAbsent(packetId, handler) != null)
            throw new IllegalStateException("Duplicate DDS S2C client handler: " + packetId);
    }

    private static void validateHandler(String packetId, Object handler) {
        if (!DdsPacketRegistry.localS2CPackets().contains(packetId) || handler == null)
            throw new IllegalArgumentException("Invalid DDS S2C client handler: " + packetId);
    }

    public static void onConnectedToServer() {
        //#if MC >= 11404 && MC <= 260200
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        synchronized (STATE) {
            STATE.resetProtocolState();
            STATE.bindConnection(listener);
        }
        if (listener != null) sendHello();
        //#else
        //$$ STATE.resetProtocolState();
        //#endif
    }

    public static boolean isClientEnhancementActive() { return STATE.isServerProtocolActive(); }
    public static boolean isServerProtocolActive() { return STATE.isServerProtocolActive(); }
    public static boolean doesServerSupport(String packetId) { return STATE.doesServerSupport(packetId); }

    public static boolean canSend(String packetId) {
        //#if MC >= 11404 && MC <= 260200
        if (!ensureCurrentConnection()) return false;
        if (!STATE.isServerProtocolActive()) {
            retryHelloIfNeeded();
            return false;
        }
        return STATE.doesServerSupport(packetId);
        //#else
        //$$ return false;
        //#endif
    }

    public static boolean send(String packetId, CompoundTag data) {
        //#if MC >= 11404 && MC <= 260200
        return canSend(packetId) && sendRaw(packetId, data);
        //#else
        //$$ return false;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260200
    public static void handlePayload(Object source, DdsProtocol.Payload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null || source != connection || STATE.connection() != connection) return;

        String packetId = payload.packetId();
        if (DdsProtocol.HELLO_S2C.equals(packetId)) {
            handleHello(connection, payload.data());
            return;
        }

        BiConsumer<ClientPacketListener, CompoundTag> ordered;
        Consumer<CompoundTag> handler;
        synchronized (DdsClientNetwork.class) {
            ordered = ORDERED_S2C_HANDLERS.get(packetId);
            handler = S2C_HANDLERS.get(packetId);
        }
        if (!STATE.isServerProtocolActive()) return;
        if (ordered != null) {
            ordered.accept(connection, payload.data());
            return;
        }
        if (handler == null) return;

        Runnable dispatch = () -> {
            if (minecraft.getConnection() == connection && STATE.connection() == connection && STATE.isServerProtocolActive())
                handler.accept(payload.data());
        };
        if (minecraft.isSameThread()) dispatch.run();
        else minecraft.execute(dispatch);
    }

    private static void handleHello(ClientPacketListener connection, CompoundTag data) {
        //#if MC >= 12105
        //$$ int protocol = data.getIntOr("protocol", 0);
        //$$ boolean enabled = data.getIntOr("protocol_enabled", 0) != 0;
        //$$ String supported = data.getStringOr("supported_c2s", "");
        //#else
        int protocol = data.getInt("protocol");
        boolean enabled = data.getInt("protocol_enabled") != 0;
        String supported = data.getString("supported_c2s");
        //#endif
        boolean active = protocol == DdsProtocol.VERSION && enabled;
        Set<String> supportedC2S = active
                ? DdsProtocol.decodeKnownIds(supported, DdsPacketRegistry.localC2SPackets()) : null;
        synchronized (STATE) {
            if (Minecraft.getInstance().getConnection() != connection || STATE.connection() != connection) return;
            STATE.updateHandshake(active, supportedC2S);
        }
    }

    private static boolean ensureCurrentConnection() {
        ClientPacketListener current = Minecraft.getInstance().getConnection();
        synchronized (STATE) {
            ClientPacketListener previous = STATE.connection();
            if (current == null) {
                if (previous != null || STATE.isServerProtocolActive() || STATE.lastHelloNanos() != 0L) {
                    STATE.bindConnection(null);
                    STATE.resetProtocolState();
                }
                return false;
            }
            if (previous != current) {
                STATE.bindConnection(current);
                STATE.resetProtocolState();
            }
            return true;
        }
    }

    private static void retryHelloIfNeeded() {
        long now = System.nanoTime();
        if (STATE.lastHelloNanos() == 0L || now - STATE.lastHelloNanos() >= HELLO_RETRY_NANOS) sendHello();
    }

    private static void sendHello() {
        CompoundTag data = new CompoundTag();
        data.putInt("protocol", DdsProtocol.VERSION);
        data.putString("mod_version", CarpetDDSAddition.getVersion());
        data.putString("supported_s2c", DdsProtocol.encodeIds(DdsPacketRegistry.localS2CPackets()));
        data.putString("supported_c2s", DdsProtocol.encodeIds(DdsPacketRegistry.localC2SPackets()));
        if (sendRaw(DdsProtocol.HELLO_C2S, data)) STATE.markHelloSent(System.nanoTime());
    }

    private static boolean sendRaw(String packetId, CompoundTag data) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener == null) return false;
        //#if MC >= 12002
        //$$ listener.getConnection().send(new ServerboundCustomPayloadPacket(new DdsProtocol.Payload(packetId, data)));
        //#else
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        new DdsProtocol.Payload(packetId, data).write(buf);
        listener.getConnection().send(new ServerboundCustomPayloadPacket(DdsProtocol.CHANNEL, buf));
        //#endif
        return true;
    }
    //#endif
}
