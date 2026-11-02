/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Executes the same guarded transfer helper as production, with simulated inventory slots. */
final class AutoRestockTransferTest {
    private static final class Source {
        int count;
        boolean compatible, leased;
        int writes;
        Source(int count, boolean compatible, boolean leased) {
            this.count = count; this.compatible = compatible; this.leased = leased;
        }
        boolean fill(int[] target, int max, boolean unchangedTarget) {
            if (!compatible || leased) return false;
            return AutoRestockTransfer.commit(max - target[0], count,
                    () -> !leased && unchangedTarget,
                    amount -> { count -= amount; target[0] += amount; writes++; });
        }
    }

    @Test void usesOnlyFirstMatchingAvailableTier() {
        int[] target = {1};
        Source backpackLoose = new Source(5, false, false);
        Source backpackBox = new Source(7, true, false);
        Source enderLoose = new Source(64, true, false);
        Source enderBox = new Source(64, true, false);
        assertFalse(backpackLoose.fill(target, 64, true));
        assertTrue(backpackBox.fill(target, 64, true));
        assertEquals(8, target[0]);
        assertEquals(0, backpackBox.count);
        assertEquals(64, enderLoose.count);
        assertEquals(64, enderBox.count);
        assertEquals(1, backpackBox.writes);
    }

    @Test void partialTransferConservesQuantity() {
        int[] target = {60};
        Source source = new Source(12, true, false);
        int before = target[0] + source.count;
        assertTrue(source.fill(target, 64, true));
        assertEquals(64, target[0]);
        assertEquals(8, source.count);
        assertEquals(before, target[0] + source.count);
    }

    @Test void leaseMismatchAndChangedTargetFailWithoutWrites() {
        for (Source source : new Source[]{new Source(16, false, false), new Source(16, true, true)}) {
            int[] target = {1};
            assertFalse(source.fill(target, 64, true));
            assertEquals(16, source.count);
            assertEquals(1, target[0]);
            assertEquals(0, source.writes);
        }
        int[] target = {1};
        Source source = new Source(16, true, false);
        assertFalse(source.fill(target, 64, false));
        assertEquals(16, source.count);
        assertEquals(1, target[0]);
    }

    @Test void emptyAndFullTargetsBoundTransfers() {
        int[] target = {0};
        Source source = new Source(2, true, false);
        assertTrue(source.fill(target, 64, true));
        assertEquals(2, target[0]);
        assertEquals(0, source.count);
        assertFalse(source.fill(target, 64, true));
        target[0] = 64;
        Source full = new Source(64, true, false);
        assertFalse(full.fill(target, 64, true));
        assertEquals(64, full.count);
    }
}
