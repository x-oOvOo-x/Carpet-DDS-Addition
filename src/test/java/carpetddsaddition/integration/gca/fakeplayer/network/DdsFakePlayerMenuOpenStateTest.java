/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DdsFakePlayerMenuOpenStateTest {
    @Test void markerIsConnectionBoundContainerBoundAndOneShot() {
        Object connection = new Object(), other = new Object();
        DdsFakePlayerMenuOpenState<Object> state = new DdsFakePlayerMenuOpenState<>(100L);

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_INVENTORY, 10L);
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(other, 7, 20L));

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_INVENTORY, 10L);
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, 8, 20L));

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_INVENTORY, 10L);
        assertEquals(DdsFakePlayerPackets.MENU_INVENTORY, state.consume(connection, 7, 20L));
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, 7, 21L));
    }

    @Test void queuedMarkersPreserveWireOrderWhenClientThreadFallsBehind() {
        Object connection = new Object();
        DdsFakePlayerMenuOpenState<Object> state = new DdsFakePlayerMenuOpenState<>(100L);

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_INVENTORY, 10L);
        state.expect(connection, 8, DdsFakePlayerPackets.MENU_ENDER_3, 11L);
        assertEquals(DdsFakePlayerPackets.MENU_INVENTORY, state.consume(connection, 7, 20L));
        assertEquals(DdsFakePlayerPackets.MENU_ENDER_3, state.consume(connection, 8, 21L));
    }

    @Test void newConnectionDropsOutstandingMarkersFromPreviousServer() {
        Object oldConnection = new Object(), newConnection = new Object();
        DdsFakePlayerMenuOpenState<Object> state = new DdsFakePlayerMenuOpenState<>(100L);

        state.expect(oldConnection, 7, DdsFakePlayerPackets.MENU_INVENTORY, 10L);
        state.expect(newConnection, 8, DdsFakePlayerPackets.MENU_ENDER_3, 11L);
        assertEquals(DdsFakePlayerPackets.MENU_ENDER_3, state.consume(newConnection, 8, 12L));
    }

    @Test void matchingWireOrderedMarkerDoesNotExpireWhileOpenScreenWaits() {
        Object connection = new Object();
        DdsFakePlayerMenuOpenState<Object> state = new DdsFakePlayerMenuOpenState<>(100L);

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_ENDER_6, 10L);
        assertEquals(DdsFakePlayerPackets.MENU_ENDER_6, state.consume(connection, 7, 1_000L));
    }

    @Test void staleInvalidAndMisalignedMarkersFailClosed() {
        Object connection = new Object();
        DdsFakePlayerMenuOpenState<Object> state = new DdsFakePlayerMenuOpenState<>(100L);

        state.expect(connection, 7, DdsFakePlayerPackets.MENU_ENDER_6, 10L);
        state.expect(connection, 8, DdsFakePlayerPackets.MENU_ENDER_3, 111L);
        assertEquals(DdsFakePlayerPackets.MENU_ENDER_3, state.consume(connection, 8, 112L));

        state.expect(connection, 9, DdsFakePlayerPackets.MENU_INVENTORY, 120L);
        state.expect(connection, 10, DdsFakePlayerPackets.MENU_ENDER_6, 121L);
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, 99, 122L));
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, 10, 123L));

        state.expect(connection, -1, DdsFakePlayerPackets.MENU_ENDER_3, 130L);
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, -1, 131L));

        state.expect(connection, 7, 99, 140L);
        assertEquals(DdsFakePlayerPackets.MENU_NONE, state.consume(connection, 7, 141L));
    }
}
