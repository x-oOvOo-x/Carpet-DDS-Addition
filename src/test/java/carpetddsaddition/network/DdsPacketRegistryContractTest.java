/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.core.bootstrap.DdsNetworkBootstrap;
import carpetddsaddition.feature.playershulkerrestock.compat.PlayerShulkerRestockNetworkCompat;
import carpetddsaddition.network.DdsFakePlayerIdentitySync;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessPackets;
import carpetddsaddition.feature.undoredo.network.UndoRedoPackets;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.LinkedHashSet;
import static org.junit.jupiter.api.Assertions.*;

final class DdsPacketRegistryContractTest {
    @BeforeAll static void initializeRegistry() { DdsNetworkBootstrap.initialize(); }

    @Test void protocolV1CapabilityOrderIsStable() {
        assertEquals(new LinkedHashSet<>(Arrays.asList(
                QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessPackets.CREATIVE_STORAGE_C2S,
                QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S,
                PlayerShulkerRestockNetworkCompat.PREFERENCES_C2S,
                UndoRedoPackets.UNDO_C2S,
                UndoRedoPackets.REDO_C2S,
                DdsFakePlayerPackets.ACTION_C2S,
                DdsFakePlayerPackets.PROVIDER_ACTION_C2S,
                DdsFakePlayerPackets.CANONICAL_ENDER_C2S,
                DdsFakePlayerPackets.RETURN_PARENT_C2S)), DdsPacketRegistry.localC2SPackets());
        assertEquals(new LinkedHashSet<>(Arrays.asList(
                QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C,
                QuickContainerAccessPackets.NAVIGATION_STATE_S2C,
                PlayerShulkerRestockNetworkCompat.STATUS_S2C,
                DdsFakePlayerPackets.CONTEXT_S2C,
                DdsFakePlayerPackets.STATE_S2C,
                DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C,
                DdsFakePlayerPackets.STRUCTURE_RESULT_S2C,
                DdsFakePlayerIdentitySync.SNAPSHOT_S2C)), DdsPacketRegistry.localS2CPackets());
    }

    @Test void bundledClientDoesNotAdvertiseTweakOwnedFakePlayerPackets() {
        assertEquals(new LinkedHashSet<>(Arrays.asList(
                QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessPackets.CREATIVE_STORAGE_C2S,
                QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                UndoRedoPackets.UNDO_C2S,
                UndoRedoPackets.REDO_C2S)), DdsPacketRegistry.clientC2SPackets());
        assertEquals(new LinkedHashSet<>(Arrays.asList(
                QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C,
                QuickContainerAccessPackets.NAVIGATION_STATE_S2C)), DdsPacketRegistry.clientS2CPackets());
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(PlayerShulkerRestockNetworkCompat.PREFERENCES_C2S));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(PlayerShulkerRestockNetworkCompat.STATUS_S2C));
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(DdsFakePlayerPackets.ACTION_C2S));
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(DdsFakePlayerPackets.PROVIDER_ACTION_C2S));
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(DdsFakePlayerPackets.CANONICAL_ENDER_C2S));
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(DdsFakePlayerPackets.RETURN_PARENT_C2S));
        assertFalse(DdsPacketRegistry.clientC2SPackets().contains(QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(DdsFakePlayerPackets.CONTEXT_S2C));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(DdsFakePlayerPackets.STATE_S2C));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(DdsFakePlayerPackets.STRUCTURE_RESULT_S2C));
        assertFalse(DdsPacketRegistry.clientS2CPackets().contains(DdsFakePlayerIdentitySync.SNAPSHOT_S2C));
    }

    @Test void qcaLegacyAndNavigationPacketsKeepDisabledGracePolicy() {
        for (String id : Arrays.asList(QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S)) {
            DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(id);
            assertFalse(packet.requireClientAdvertisement()); assertTrue(packet.allowDisabledGrace());
        }
    }

    @Test void creativeStorageRequiresNegotiationWithoutDisabledGrace() {
        DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(QuickContainerAccessPackets.CREATIVE_STORAGE_C2S);
        assertTrue(packet.requireClientAdvertisement()); assertFalse(packet.allowDisabledGrace());
    }

    @Test void undoRedoRequireNeitherAdvertisementNorDisabledGrace() {
        for (String id : Arrays.asList(UndoRedoPackets.UNDO_C2S, UndoRedoPackets.REDO_C2S)) {
            DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(id);
            assertFalse(packet.requireClientAdvertisement()); assertFalse(packet.allowDisabledGrace());
        }
    }

    @Test void restockPreferencesRequireOptInAndNeverUseDisabledProtocolGrace() {
        DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(
                PlayerShulkerRestockNetworkCompat.PREFERENCES_C2S);
        assertNotNull(packet);
        assertTrue(packet.requireClientAdvertisement());
        assertFalse(packet.allowDisabledGrace());
    }

    @Test void fakePlayerActionsRequireExplicitClientAdvertisement() {
        for(String id:Arrays.asList(DdsFakePlayerPackets.ACTION_C2S,DdsFakePlayerPackets.PROVIDER_ACTION_C2S)){
            DdsPacketRegistry.C2SPacket packet = DdsPacketRegistry.getC2S(id);
            assertTrue(packet.requireClientAdvertisement()); assertFalse(packet.allowDisabledGrace());
        }
    }

    @Test void strictStructurePacketsRequireNegotiationAndExtensionAvailability() {
        for(String id:Arrays.asList(DdsFakePlayerPackets.CANONICAL_ENDER_C2S,DdsFakePlayerPackets.RETURN_PARENT_C2S)){
            DdsPacketRegistry.C2SPacket packet=DdsPacketRegistry.getC2S(id);
            assertTrue(packet.requireClientAdvertisement()); assertFalse(packet.allowDisabledGrace());
            assertTrue(packet.hasAvailabilityPredicate());
        }
    }

    @Test void qcaAndUndoPacketsHaveFeatureAvailabilityPredicates() {
        for (String id : Arrays.asList(
                QuickContainerAccessPackets.OPEN_SLOT_C2S,
                QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S,
                QuickContainerAccessPackets.STORAGE_CLICK_C2S,
                QuickContainerAccessPackets.CREATIVE_STORAGE_C2S,
                QuickContainerAccessPackets.NAVIGATE_BACK_C2S,
                QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S,
                UndoRedoPackets.UNDO_C2S,
                UndoRedoPackets.REDO_C2S))
            assertTrue(DdsPacketRegistry.getC2S(id).hasAvailabilityPredicate(), id);
        assertFalse(DdsPacketRegistry.getC2S(DdsFakePlayerPackets.ACTION_C2S).hasAvailabilityPredicate());
        assertFalse(DdsPacketRegistry.getC2S(DdsFakePlayerPackets.PROVIDER_ACTION_C2S).hasAvailabilityPredicate());
    }

    @Test void retiredWireIdsAreNotRegisteredOrAcceptedDuringHello() {
        String oldAction = "qca_owner_ender_action_c2s";
        String oldMenu = "fake_player_open_menu_s2c";
        assertNull(DdsPacketRegistry.getC2S(oldAction));
        assertFalse(DdsPacketRegistry.localC2SPackets().contains(oldAction));
        assertFalse(DdsPacketRegistry.localS2CPackets().contains(oldMenu));
        assertFalse(DdsProtocol.decodeKnownIds(oldAction, DdsPacketRegistry.localC2SPackets()).contains(oldAction));
        assertFalse(DdsProtocol.decodeKnownIds(oldMenu, DdsPacketRegistry.localS2CPackets()).contains(oldMenu));
        assertNotNull(DdsPacketRegistry.getC2S(QuickContainerAccessPackets.OPEN_OWNER_ENDER_C2S));
        assertTrue(DdsPacketRegistry.localS2CPackets().contains(QuickContainerAccessPackets.NAVIGATION_STATE_S2C));
        assertTrue(DdsPacketRegistry.localS2CPackets().contains(DdsFakePlayerPackets.CONTEXT_S2C));
        assertTrue(DdsPacketRegistry.localS2CPackets().contains(DdsFakePlayerPackets.STATE_S2C));
    }

    @Test void transportReservedIdsCannotBeRegisteredAsFeaturePackets() {
        assertThrows(IllegalArgumentException.class, () -> DdsPacketRegistry.registerC2S(DdsProtocol.HELLO_C2S, (player, data) -> {}, false, false));
        assertThrows(IllegalArgumentException.class, () -> DdsPacketRegistry.registerS2C(DdsProtocol.HELLO_S2C));
    }

    @Test void ambiguousCapabilityIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> DdsPacketRegistry.registerC2S(" leading_space", (player, data) -> {}, false, false));
        assertThrows(IllegalArgumentException.class, () -> DdsPacketRegistry.registerS2C("contains,comma"));
    }

    @Test void registryIsFrozenAfterBootstrap() {
        int c2s = DdsPacketRegistry.localC2SPackets().size(), s2c = DdsPacketRegistry.localS2CPackets().size();
        assertThrows(IllegalStateException.class, () -> DdsPacketRegistry.registerC2S("late_test_c2s", (player, data) -> {}, false, false));
        assertThrows(IllegalStateException.class, () -> DdsPacketRegistry.registerS2C("late_test_s2c"));
        assertEquals(c2s, DdsPacketRegistry.localC2SPackets().size());
        assertEquals(s2c, DdsPacketRegistry.localS2CPackets().size());
    }

    @Test void duplicateFeaturePacketRegistrationIsRejectedWithoutMutation() {
        assertThrows(IllegalStateException.class, () -> DdsPacketRegistry.registerC2S(
                QuickContainerAccessPackets.STORAGE_CLICK_C2S, (player, data) -> {}, false, true));
        assertThrows(IllegalStateException.class, () -> DdsPacketRegistry.registerS2C(QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C));
        assertEquals(13, DdsPacketRegistry.localC2SPackets().size());
        assertEquals(8, DdsPacketRegistry.localS2CPackets().size());
    }
}
