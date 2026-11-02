/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.network;

import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessPackets;
import carpetddsaddition.network.DdsProtocol;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
//#if MC >= 12002 && MC < 12006
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#endif

/** Development-only DDS Protocol v1 packet registration/round-trip audit. */
public final class DdsProtocolBaselineAudit {
    private static final String AUDIT_PROPERTY = "carpetddsaddition.protocol_baseline";
    private static final String SENTINEL_KEY = "dds_baseline";
    private DdsProtocolBaselineAudit() {}

    public static void runIfRequested() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || !Boolean.getBoolean(AUDIT_PROPERTY)) return;
        System.out.println("[Carpet DDS Addition] Running DDS Protocol baseline audit...");
        try {
            runAudit();
            System.out.println("[Carpet DDS Addition] DDS Protocol baseline audit passed.");
            System.exit(0);
        } catch (Throwable throwable) {
            System.err.println("[Carpet DDS Addition] DDS Protocol baseline audit failed.");
            throwable.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void runAudit() throws Exception {
        DdsProtocol.Payload helloC2S = payload(DdsProtocol.HELLO_C2S);
        DdsProtocol.Payload helloS2C = payload(DdsProtocol.HELLO_S2C);
        DdsProtocol.Payload featureC2S = payload(QuickContainerAccessPackets.OPEN_SLOT_C2S);
        DdsProtocol.Payload featureS2C = payload(QuickContainerAccessPackets.NAVIGATION_STATE_S2C);
        //#if MC >= 12006
        //$$ assertCodecRoundTrip(helloC2S, "hello c2s codec");
        //$$ assertCodecRoundTrip(helloS2C, "hello s2c codec");
        //$$ assertCodecRoundTrip(featureC2S, "QCA c2s codec");
        //$$ assertCodecRoundTrip(featureS2C, "QCA s2c codec");
        //#elseif MC >= 12002
        //$$ assertRegistered(ServerboundCustomPayloadPacket.class, helloC2S, "serverbound hello");
        //$$ assertRegistered(ClientboundCustomPayloadPacket.class, helloS2C, "clientbound hello");
        //$$ assertRegistered(ServerboundCustomPayloadPacket.class, featureC2S, "serverbound QCA");
        //$$ assertRegistered(ClientboundCustomPayloadPacket.class, featureS2C, "clientbound QCA");
        //#else
        assertRawRoundTrip(helloC2S, "hello c2s raw");
        assertRawRoundTrip(helloS2C, "hello s2c raw");
        assertRawRoundTrip(featureC2S, "QCA c2s raw");
        assertRawRoundTrip(featureS2C, "QCA s2c raw");
        //#endif
    }

    private static DdsProtocol.Payload payload(String packetId) {
        CompoundTag data = new CompoundTag();
        data.putInt(SENTINEL_KEY, 1);
        return new DdsProtocol.Payload(packetId, data);
    }

    //#if MC >= 12006
    //$$ private static void assertCodecRoundTrip(DdsProtocol.Payload payload, String path) {
    //$$     FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    //$$     DdsProtocol.Payload.CODEC.encode(buffer, payload);
    //$$     assertPayload(DdsProtocol.Payload.CODEC.decode(buffer), payload.packetId(), path);
    //$$ }
    //#elseif MC >= 12002
    //$$ private static void assertRegistered(Class<?> packetClass, DdsProtocol.Payload payload, String path) throws Exception {
    //$$     Field field = packetClass.getDeclaredField("KNOWN_TYPES");
    //$$     field.setAccessible(true);
    //$$     Object value = field.get(null);
    //$$     if (!(value instanceof Map)) throw new IllegalStateException(path + " KNOWN_TYPES is not a Map");
    //$$     Object rawReader = ((Map<?, ?>) value).get(DdsProtocol.CHANNEL);
    //$$     if (!(rawReader instanceof FriendlyByteBuf.Reader)) throw new IllegalStateException(path + " DDS channel missing from KNOWN_TYPES");
    //$$     FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    //$$     payload.write(buffer);
    //$$     Object decoded = ((FriendlyByteBuf.Reader<?>) rawReader).apply(buffer);
    //$$     assertPayload(decoded, payload.packetId(), path + " registered reader");
    //$$ }
    //#else
    private static void assertRawRoundTrip(DdsProtocol.Payload payload, String path) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        payload.write(buffer);
        assertPayload(new DdsProtocol.Payload(buffer), payload.packetId(), path);
    }
    //#endif

    private static void assertPayload(Object value, String expectedPacketId, String path) {
        if (!(value instanceof DdsProtocol.Payload)) {
            throw new IllegalStateException(path + " decoded " + (value == null ? "null" : value.getClass().getName()));
        }
        DdsProtocol.Payload payload = (DdsProtocol.Payload) value;
        if (!expectedPacketId.equals(payload.packetId())) throw new IllegalStateException(path + " packet id mismatch");
        //#if MC >= 12105
        //$$ int sentinel = payload.data().getIntOr(SENTINEL_KEY, -1);
        //#else
        int sentinel = payload.data().contains(SENTINEL_KEY) ? payload.data().getInt(SENTINEL_KEY) : -1;
        //#endif
        if (sentinel != 1) throw new IllegalStateException(path + " payload sentinel mismatch");
    }
}
