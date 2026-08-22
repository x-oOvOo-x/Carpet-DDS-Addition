/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.CarpetDDSAddition;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//#endif
//#if MC >= 12006
//$$ import net.minecraft.network.codec.StreamCodec;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
import net.minecraft.resources.ResourceLocation;
//#endif
//#endif

/** Versioned DDS shared network protocol. */
public final class DdsProtocol {
    public static final int VERSION = 1;
    public static final String HELLO_C2S = "hello_c2s", HELLO_S2C = "hello_s2c";
    private DdsProtocol() {}

    public static String encodeIds(Collection<String> ids) { return String.join(",", ids); }
    public static Set<String> decodeIds(String encoded) {
        if (encoded == null || encoded.trim().isEmpty()) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        for (String part : encoded.split(",")) {
            String id = part.trim();
            if (!id.isEmpty()) result.add(id);
        }
        return result;
    }

    /** Decodes only locally meaningful capabilities, without allocating token strings for unknown remote ids. */
    static Set<String> decodeKnownIds(String encoded, Collection<String> knownIds) {
        if (encoded == null || encoded.isEmpty() || knownIds == null || knownIds.isEmpty()) return Collections.emptySet();
        Set<String> result = new LinkedHashSet<>();
        int tokenStart = 0, length = encoded.length();
        for (int i = 0; i <= length; i++) {
            if (i < length && encoded.charAt(i) != ',') continue;
            int start = tokenStart, end = i;
            while (start < end && encoded.charAt(start) <= ' ') start++;
            while (end > start && encoded.charAt(end - 1) <= ' ') end--;
            int tokenLength = end - start;
            if (tokenLength > 0) for (String knownId : knownIds) {
                if (knownId != null && knownId.length() == tokenLength && encoded.regionMatches(start, knownId, 0, tokenLength)) {
                    result.add(knownId);
                    break;
                }
            }
            tokenStart = i + 1;
        }
        return result.isEmpty() ? Collections.emptySet() : result;
    }

    //#if MC >= 11404 && MC <= 260200
    //#if MC >= 12111
    //$$ public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "network/v1");
    //#elseif MC >= 12101
    //$$ public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "network/v1");
    //#else
    public static final ResourceLocation CHANNEL = new ResourceLocation(CarpetDDSAddition.MOD_ID, "network/v1");
    //#endif

    //#if MC >= 12002
    //$$ public static final class Payload implements CustomPacketPayload {
    //#else
    public static final class Payload {
    //#endif
        //#if MC >= 12006
        //$$ public static final Type<Payload> TYPE = new Type<>(CHANNEL);
        //$$ public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = CustomPacketPayload.codec(Payload::write, Payload::new);
        //#endif

        private final String packetId;
        private final CompoundTag data;

        public Payload(String packetId, CompoundTag data) {
            this.packetId = packetId;
            this.data = data == null ? new CompoundTag() : data;
        }
        public Payload(FriendlyByteBuf buf) {
            packetId = buf.readUtf();
            CompoundTag read = buf.readNbt();
            data = read == null ? new CompoundTag() : read;
        }

        //#if MC >= 12006
        //$$ private void write(FriendlyByteBuf buf) {
        //#else
        //#if MC >= 12002
        //$$ @Override
        //#endif
        public void write(FriendlyByteBuf buf) {
        //#endif
            buf.writeUtf(packetId);
            buf.writeNbt(data);
        }

        public String packetId() { return packetId; }
        public CompoundTag data() { return data; }

        //#if MC >= 12006
        //$$ @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
        //#elseif MC >= 12002
        //$$ @Override public ResourceLocation id() { return CHANNEL; }
        //#endif
    }
    //#endif
}
