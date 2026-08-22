/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsProtocolContractTest {

    @Test
    void protocolV1WireIdentityRemainsStable() {
        assertEquals(1, DdsProtocol.VERSION);
        assertEquals("hello_c2s", DdsProtocol.HELLO_C2S);
        assertEquals("hello_s2c", DdsProtocol.HELLO_S2C);

        assertEquals(
                "carpet-dds-addition:network/v1",
                DdsProtocol.CHANNEL.toString()
        );
    }

    @Test
    void capabilityEncodingPreservesRegistrationOrder() {
        Set<String> ids = new LinkedHashSet<>(Arrays.asList("alpha", "beta", "gamma"));
        assertEquals("alpha,beta,gamma", DdsProtocol.encodeIds(ids));
    }

    @Test
    void capabilityDecodingTrimsDeduplicatesAndPreservesFirstOrder() {
        assertEquals(
                new LinkedHashSet<>(Arrays.asList("alpha", "beta", "gamma")),
                DdsProtocol.decodeIds(" alpha,beta,alpha,, gamma ")
        );
    }

    @Test
    void knownCapabilityDecodingDropsUnknownIdsAndPreservesFirstOrder() {
        assertEquals(
                new LinkedHashSet<>(Arrays.asList("beta", "alpha")),
                DdsProtocol.decodeKnownIds(
                        " unknown,beta,alpha,beta,other ",
                        new LinkedHashSet<>(Arrays.asList("alpha", "beta"))
                )
        );
    }

    @Test
    void emptyCapabilityInputDecodesToEmptySet() {
        assertTrue(DdsProtocol.decodeIds(null).isEmpty());
        assertTrue(DdsProtocol.decodeIds("").isEmpty());
        assertTrue(DdsProtocol.decodeIds("   ").isEmpty());
    }
}
