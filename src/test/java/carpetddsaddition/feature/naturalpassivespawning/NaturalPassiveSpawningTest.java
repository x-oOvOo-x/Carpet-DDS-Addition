package carpetddsaddition.feature.naturalpassivespawning;

import carpetddsaddition.feature.naturalpassivespawning.NaturalPassiveSpawning.Group;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NaturalPassiveSpawningTest {
    @Test void noneKeepsVanillaSpawning() {
        assertEquals(0, NaturalPassiveSpawning.parse("none"));
        assertEquals(0, NaturalPassiveSpawning.parse(""));
        assertEquals(0, NaturalPassiveSpawning.parse(null));
    }

    @Test void allBlocksEveryControlledGroup() {
        int mask = NaturalPassiveSpawning.parse("all");
        assertEquals(NaturalPassiveSpawning.allMask(), mask);
        for (Group group : Group.values()) assertTrue(NaturalPassiveSpawning.prevents(mask, group));
    }

    @Test void commaListBlocksOnlySelectedGroups() {
        int mask = NaturalPassiveSpawning.parse(" land, FISH ");
        assertTrue(NaturalPassiveSpawning.prevents(mask, Group.LAND));
        assertTrue(NaturalPassiveSpawning.prevents(mask, Group.FISH));
        assertFalse(NaturalPassiveSpawning.prevents(mask, Group.SQUID));
        assertFalse(NaturalPassiveSpawning.prevents(mask, Group.AXOLOTL));
        assertFalse(NaturalPassiveSpawning.prevents(mask, Group.BAT));
        assertFalse(NaturalPassiveSpawning.prevents(mask, Group.DOLPHIN));
    }

    @Test void invalidValuesFailSafeToVanilla() {
        assertEquals(0, NaturalPassiveSpawning.parse("fish,"));
        assertEquals(0, NaturalPassiveSpawning.parse("fish,unknown"));
        assertEquals(0, NaturalPassiveSpawning.parse("none,fish"));
        assertEquals(0, NaturalPassiveSpawning.parse("all,fish"));
    }

    @Test void duplicateGroupsAreIdempotent() {
        assertEquals(NaturalPassiveSpawning.parse("fish"), NaturalPassiveSpawning.parse("fish,fish"));
    }
}
