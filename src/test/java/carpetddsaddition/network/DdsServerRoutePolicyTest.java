/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.core.bootstrap.DdsNetworkBootstrap;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessPackets;
import carpetddsaddition.feature.undoredo.network.UndoRedoPackets;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsServerRoutePolicyTest {

    @BeforeAll
    static void initializeRegistry() {
        DdsNetworkBootstrap.initialize();
    }

    @Test
    void protocolCompatibilityIsExactProtocolV1() {
        assertTrue(
                DdsServerRoutePolicy
                        .isProtocolCompatible(
                                DdsProtocol.VERSION
                        )
        );

        assertFalse(
                DdsServerRoutePolicy
                        .isProtocolCompatible(
                                DdsProtocol.VERSION - 1
                        )
        );

        assertFalse(
                DdsServerRoutePolicy
                        .isProtocolCompatible(
                                DdsProtocol.VERSION + 1
                        )
        );
    }

    @Test
    void enabledQcaAndUndoRedoDoNotRequireAdvertisement() {
        Set<String> none =
                Collections.emptySet();

        assertTrue(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet(
                                        QuickContainerAccessPackets
                                                .STORAGE_CLICK_C2S
                                ),
                                none
                        )
        );

        assertTrue(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet(
                                        UndoRedoPackets.UNDO_C2S
                                ),
                                none
                        )
        );

        assertTrue(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet(
                                        UndoRedoPackets.REDO_C2S
                                ),
                                none
                        )
        );
    }

    @Test
    void enabledFakePlayerActionRequiresExactAdvertisement() {
        DdsPacketRegistry.C2SPacket packet =
                packet(
                        DdsFakePlayerPackets.ACTION_C2S
                );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet,
                                Collections.emptySet()
                        )
        );

        Set<String> unrelated =
                new LinkedHashSet<>();

        unrelated.add(
                QuickContainerAccessPackets
                        .STORAGE_CLICK_C2S
        );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet,
                                unrelated
                        )
        );

        Set<String> advertised =
                new LinkedHashSet<>();

        advertised.add(
                DdsFakePlayerPackets.ACTION_C2S
        );

        assertTrue(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                packet,
                                advertised
                        )
        );
    }

    @Test
    void disabledGraceIsQcaOnly() {
        Set<String> none =
                Collections.emptySet();

        assertTrue(
                DdsServerRoutePolicy
                        .canDispatchDisabledGrace(
                                packet(
                                        QuickContainerAccessPackets
                                                .STORAGE_CLICK_C2S
                                ),
                                none
                        )
        );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchDisabledGrace(
                                packet(
                                        UndoRedoPackets.UNDO_C2S
                                ),
                                none
                        )
        );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchDisabledGrace(
                                packet(
                                        UndoRedoPackets.REDO_C2S
                                ),
                                none
                        )
        );

        Set<String> advertisedGca =
                new LinkedHashSet<>();

        advertisedGca.add(
                DdsFakePlayerPackets.ACTION_C2S
        );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchDisabledGrace(
                                packet(
                                        DdsFakePlayerPackets.ACTION_C2S
                                ),
                                advertisedGca
                        )
        );
    }

    @Test
    void unresolvedRouteAlwaysFailsClosed() {
        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchEnabled(
                                null,
                                Collections.emptySet()
                        )
        );

        assertFalse(
                DdsServerRoutePolicy
                        .canDispatchDisabledGrace(
                                null,
                                Collections.emptySet()
                        )
        );
    }

    private static DdsPacketRegistry.C2SPacket packet(
            String packetId
    ) {
        DdsPacketRegistry.C2SPacket packet =
                DdsPacketRegistry.getC2S(
                        packetId
                );

        if (packet == null) {
            throw new AssertionError(
                    "Missing registered packet: "
                            + packetId
            );
        }

        return packet;
    }
}
