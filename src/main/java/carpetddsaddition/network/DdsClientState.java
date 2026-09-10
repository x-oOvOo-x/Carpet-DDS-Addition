/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.client.multiplayer.ClientPacketListener;
//#endif

/** Negotiated DDS client connection/session state. */
final class DdsClientState {
    private volatile boolean serverProtocolActive;
    private volatile Set<String> serverSupportedC2S = Collections.emptySet();
    private volatile long lastHelloNanos;

    //#if MC >= 11404 && MC <= 260200
    private volatile WeakReference<ClientPacketListener> connectionRef = new WeakReference<>(null);
    //#endif

    boolean isServerProtocolActive() { return serverProtocolActive; }
    boolean doesServerSupport(String packetId) { return serverProtocolActive && serverSupportedC2S.contains(packetId); }

    void updateHandshake(boolean active, Set<String> supportedC2S) {
        serverSupportedC2S = !active || supportedC2S == null || supportedC2S.isEmpty()
                ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(supportedC2S));
        serverProtocolActive = active;
    }

    void resetProtocolState() {
        serverSupportedC2S = Collections.emptySet();
        serverProtocolActive = false;
        lastHelloNanos = 0L;
    }

    long lastHelloNanos() { return lastHelloNanos; }
    void markHelloSent(long value) { lastHelloNanos = value; }

    //#if MC >= 11404 && MC <= 260200
    ClientPacketListener connection() { return connectionRef.get(); }
    void bindConnection(ClientPacketListener connection) { connectionRef = new WeakReference<>(connection); }
    //#endif
}
