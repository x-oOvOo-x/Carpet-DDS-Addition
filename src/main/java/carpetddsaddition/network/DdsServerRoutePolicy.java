/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import java.util.Set;

/** Pure server-side DDS route/capability policy. */
final class DdsServerRoutePolicy {
    private DdsServerRoutePolicy() {}

    static boolean isProtocolCompatible(int protocolVersion) { return protocolVersion == DdsProtocol.VERSION; }
    static boolean canDispatchEnabled(DdsPacketRegistry.C2SPacket packet, Set<String> supportedC2S) {
        return packet != null && satisfiesAdvertisement(packet, supportedC2S);
    }
    static boolean canDispatchDisabledGrace(DdsPacketRegistry.C2SPacket packet, Set<String> supportedC2S) {
        return packet != null && packet.allowDisabledGrace() && satisfiesAdvertisement(packet, supportedC2S);
    }
    private static boolean satisfiesAdvertisement(DdsPacketRegistry.C2SPacket packet, Set<String> supportedC2S) {
        return !packet.requireClientAdvertisement() || supportedC2S != null && supportedC2S.contains(packet.packetId());
    }
}
