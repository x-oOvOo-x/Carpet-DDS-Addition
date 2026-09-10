/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

import java.util.ArrayDeque;
import java.util.Deque;

/** Connection-bound FIFO pairing negotiated DDS markers with following vanilla open-screen packets. */
public final class DdsFakePlayerMenuOpenState<C> {
    private static final int DEFAULT_MAX_PENDING = 128;
    private final long maxAgeNanos;
    private final int maxPending;
    private final Deque<Pending<C>> pending = new ArrayDeque<>();

    public DdsFakePlayerMenuOpenState(long maxAgeNanos) { this(maxAgeNanos, DEFAULT_MAX_PENDING); }
    DdsFakePlayerMenuOpenState(long maxAgeNanos, int maxPending) {
        if (maxAgeNanos <= 0L || maxPending <= 0) throw new IllegalArgumentException("limits must be positive");
        this.maxAgeNanos = maxAgeNanos; this.maxPending = maxPending;
    }

    public synchronized void expect(C connection, int containerId, int layout, long nowNanos) {
        if (connection == null || containerId < 0 || !DdsFakePlayerPackets.isMenuLayout(layout)) return;
        if (!pending.isEmpty() && pending.peekLast().connection != connection) pending.clear();
        else pruneStale(nowNanos);
        if (pending.size() >= maxPending) pending.removeFirst();
        pending.addLast(new Pending<>(connection, containerId, layout, nowNanos));
    }

    public synchronized int consume(C connection, int containerId, long nowNanos) {
        Pending<C> value = pending.pollFirst();
        if (value == null) return DdsFakePlayerPackets.MENU_NONE;
        if (value.connection == connection && value.containerId == containerId) {
            // A matching head is the wire-ordered partner of this OpenScreen packet. Never age
            // it out here: vanilla fallback would make following DDS ContainerData fatal.
            return value.layout;
        }
        // A head mismatch means the packet pairing invariant was lost; discard the remaining
        // queue rather than shift every later marker onto the wrong vanilla OpenScreen.
        pending.clear();
        return DdsFakePlayerPackets.MENU_NONE;
    }

    public synchronized void clear() { pending.clear(); }

    private void pruneStale(long nowNanos) {
        while (!pending.isEmpty()) {
            long age = nowNanos - pending.peekFirst().createdAtNanos;
            if (age >= 0L && age <= maxAgeNanos) return;
            pending.removeFirst();
        }
    }

    private static final class Pending<C> {
        private final C connection;
        private final int containerId, layout;
        private final long createdAtNanos;
        private Pending(C connection, int containerId, int layout, long createdAtNanos) {
            this.connection = connection; this.containerId = containerId; this.layout = layout; this.createdAtNanos = createdAtNanos;
        }
    }
}
