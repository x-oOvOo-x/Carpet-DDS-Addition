/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pure, version-independent gate tests; not a substitute for runtime inventory tests. */
final class PlayerShulkerRestockPolicyTest {
    @Test void onlyObservedConsumptionCanRestock() {
        assertFalse(PlayerShulkerRestock.shouldRestock(0, 0, 64, 0));
        assertFalse(PlayerShulkerRestock.shouldRestock(5, 5, 64, 5));
        assertFalse(PlayerShulkerRestock.shouldRestock(5, 6, 64, 6));
        assertFalse(PlayerShulkerRestock.shouldRestock(5, -1, 64, 0));
        assertTrue(PlayerShulkerRestock.shouldRestock(1, 0, 64, 0));
    }

    @Test void thresholdOnlyTriggersAfterActualDecrease() {
        assertFalse(PlayerShulkerRestock.shouldRestock(16, 9, 64, 8));
        assertTrue(PlayerShulkerRestock.shouldRestock(16, 8, 64, 8));
        assertTrue(PlayerShulkerRestock.shouldRestock(64, 63, 64, 63));
        assertFalse(PlayerShulkerRestock.shouldRestock(3, 3, 64, 63));
    }

    @Test void openingContainersAndBeginningToEatNeverCountAsImmediateConsumption() {
        assertFalse(PlayerShulkerRestock.shouldCompleteImmediately(true, true));
        assertFalse(PlayerShulkerRestock.shouldCompleteImmediately(false, false));
        assertTrue(PlayerShulkerRestock.shouldCompleteImmediately(false, true));
        assertFalse(PlayerShulkerRestock.shouldRestock(4, 4, 64, 16));
    }

    @Test void clampAlwaysLeavesSpaceForAtLeastOneMovedItem() {
        assertEquals(0, PlayerShulkerRestock.effectiveThreshold(-10, 64));
        assertEquals(0, PlayerShulkerRestock.effectiveThreshold(0, 64));
        assertEquals(15, PlayerShulkerRestock.effectiveThreshold(63, 16));
        assertEquals(63, PlayerShulkerRestock.effectiveThreshold(63, 64));
        assertEquals(0, PlayerShulkerRestock.effectiveThreshold(63, 1));
        assertFalse(PlayerShulkerRestock.shouldRestock(1, 0, 1, 63));
    }
}
