/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.easymining;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class EasyMiningTest {
    @Test
    void modesRemainIndependentAndCombinationsAreOrderIndependent() {
        String[] modes = {"obsidian", "crying_obsidian", "deepslate", "ores", "all"};
        int combined = 0;
        for (String mode : modes) {
            int flags = EasyMining.parse(mode);
            assertNotEquals(0, flags, mode);
            assertEquals(0, combined & flags, mode);
            combined |= flags;
        }
        assertEquals(combined, EasyMining.parse(String.join(",", modes)));
        assertEquals(combined, EasyMining.parse("all,ores,deepslate,crying_obsidian,obsidian"));
        assertEquals(EasyMining.parse("obsidian,ores"), EasyMining.parse(" ores, obsidian,ores "));
    }

    @Test
    void malformedValuesNeverPartiallyEnableMining() {
        for (String value : new String[]{null, "", " ", "false", " false ", "true", "OBSIDIAN",
                "unknown", "obsidian,unknown", "all,unknown", "unknown,all", "false,all",
                "all,false", ",ores", "ores,", "obsidian,,ores"}) {
            assertEquals(0, EasyMining.parse(value), String.valueOf(value));
        }
    }

    @Test
    void cachedSelectionTracksEnableChangeDisableAndReenable() {
        for (String value : new String[]{"all", "ores", "obsidian,crying_obsidian", "false",
                "all,unknown", "deepslate", "deepslate", "false", "all"}) {
            assertEquals(EasyMining.parse(value), EasyMining.flags(value), value);
        }
    }

    @Test
    void unbreakableAndAlreadyInstantProgressIsPreserved() {
        for (float progress : new float[]{-1.0F, 0.0F, 1.0F, 2.0F, Float.POSITIVE_INFINITY}) {
            assertEquals(progress, EasyMining.adjustProgress(null, progress));
        }
        assertTrue(Float.isNaN(EasyMining.adjustProgress(null, Float.NaN)));
    }
}
