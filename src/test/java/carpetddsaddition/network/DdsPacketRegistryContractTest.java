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

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsPacketRegistryContractTest {

    @BeforeAll
    static void initializeRegistry() {
        DdsNetworkBootstrap.initialize();
    }

    @Test
    void protocolV1CapabilityOrderIsStable() {
        assertEquals(
                new LinkedHashSet<>(
                        Arrays.asList(
                                QuickContainerAccessPackets
                                        .STORAGE_CLICK_C2S,
                                UndoRedoPackets.UNDO_C2S,
                                UndoRedoPackets.REDO_C2S,
                                DdsFakePlayerPackets.ACTION_C2S
                        )
                ),
                DdsPacketRegistry.localC2SPackets()
        );

        assertTrue(
                DdsPacketRegistry.localS2CPackets()
                        .isEmpty()
        );
    }

    @Test
    void qcaKeepsLegacyAdvertisementAndDisabledGracePolicy() {
        DdsPacketRegistry.C2SPacket packet =
                DdsPacketRegistry.getC2S(
                        QuickContainerAccessPackets
                                .STORAGE_CLICK_C2S
                );

        assertFalse(
                packet.requireClientAdvertisement()
        );

        assertTrue(
                packet.allowDisabledGrace()
        );
    }

    @Test
    void undoRedoRequireNeitherAdvertisementNorDisabledGrace() {
        DdsPacketRegistry.C2SPacket undo =
                DdsPacketRegistry.getC2S(
                        UndoRedoPackets.UNDO_C2S
                );

        DdsPacketRegistry.C2SPacket redo =
                DdsPacketRegistry.getC2S(
                        UndoRedoPackets.REDO_C2S
                );

        assertFalse(
                undo.requireClientAdvertisement()
        );

        assertFalse(
                undo.allowDisabledGrace()
        );

        assertFalse(
                redo.requireClientAdvertisement()
        );

        assertFalse(
                redo.allowDisabledGrace()
        );
    }

    @Test
    void fakePlayerActionsRequireExplicitClientAdvertisement() {
        DdsPacketRegistry.C2SPacket packet =
                DdsPacketRegistry.getC2S(
                        DdsFakePlayerPackets.ACTION_C2S
                );

        assertTrue(
                packet.requireClientAdvertisement()
        );

        assertFalse(
                packet.allowDisabledGrace()
        );
    }

    @Test
    void transportReservedIdsCannotBeRegisteredAsFeaturePackets() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DdsPacketRegistry.registerC2S(
                        DdsProtocol.HELLO_C2S,
                        (player, data) -> {
                        },
                        false,
                        false
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> DdsPacketRegistry.registerC2S(
                        DdsProtocol.HELLO_S2C,
                        (player, data) -> {
                        },
                        false,
                        false
                )
        );
    }

    @Test
    void ambiguousCapabilityIdsAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DdsPacketRegistry.registerC2S(
                        " leading_space",
                        (player, data) -> {
                        },
                        false,
                        false
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> DdsPacketRegistry.registerC2S(
                        "contains,comma",
                        (player, data) -> {
                        },
                        false,
                        false
                )
        );
    }

    @Test
    void duplicateFeaturePacketRegistrationIsRejectedWithoutMutation() {
        assertThrows(
                IllegalStateException.class,
                () -> DdsPacketRegistry.registerC2S(
                        QuickContainerAccessPackets
                                .STORAGE_CLICK_C2S,
                        (player, data) -> {
                        },
                        false,
                        true
                )
        );

        assertEquals(
                4,
                DdsPacketRegistry.localC2SPackets()
                        .size()
        );
    }
}
