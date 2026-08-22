/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsClientStateTest {

    @Test
    void negotiatedCapabilitiesAreCopiedAndQueriedOnlyWhileActive() {
        DdsClientState state =
                new DdsClientState();

        Set<String> capabilities =
                new LinkedHashSet<>();

        capabilities.add("alpha");
        capabilities.add("beta");

        state.updateHandshake(
                true,
                capabilities
        );

        capabilities.clear();

        assertTrue(
                state.isServerProtocolActive()
        );

        assertTrue(
                state.doesServerSupport("alpha")
        );

        assertTrue(
                state.doesServerSupport("beta")
        );

        assertFalse(
                state.doesServerSupport("missing")
        );
    }

    @Test
    void inactiveHandshakeFailsClosedAndClearsCapabilities() {
        DdsClientState state =
                new DdsClientState();

        Set<String> capabilities =
                new LinkedHashSet<>();

        capabilities.add("alpha");

        state.updateHandshake(
                true,
                capabilities
        );

        state.updateHandshake(
                false,
                capabilities
        );

        assertFalse(
                state.isServerProtocolActive()
        );

        assertFalse(
                state.doesServerSupport("alpha")
        );
    }

    @Test
    void resetClearsNegotiatedStateAndHelloRetryTimestamp() {
        DdsClientState state =
                new DdsClientState();

        Set<String> capabilities =
                new LinkedHashSet<>();

        capabilities.add("alpha");

        state.updateHandshake(
                true,
                capabilities
        );

        state.markHelloSent(1234L);

        assertTrue(
                state.lastHelloNanos() != 0L
        );

        state.resetProtocolState();

        assertFalse(
                state.isServerProtocolActive()
        );

        assertFalse(
                state.doesServerSupport("alpha")
        );

        assertTrue(
                state.lastHelloNanos() == 0L
        );
    }
}
