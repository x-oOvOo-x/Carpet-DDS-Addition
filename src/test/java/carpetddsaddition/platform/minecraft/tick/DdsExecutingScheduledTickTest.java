/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.tick;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class DdsExecutingScheduledTickTest {
    @Test void restoresNestedScopesAndSurvivesExceptions() {
        Object outer = new Object(), inner = new Object();
        assertNull(DdsExecutingScheduledTick.current());
        try (DdsExecutingScheduledTick.Scope ignored = DdsExecutingScheduledTick.enter(outer)) {
            assertSame(outer, DdsExecutingScheduledTick.current());
            assertThrows(IllegalStateException.class, () -> {
                try (DdsExecutingScheduledTick.Scope nested = DdsExecutingScheduledTick.enter(inner)) {
                    assertSame(inner, DdsExecutingScheduledTick.current());
                    throw new IllegalStateException("vanilla tick failed");
                }
            });
            assertSame(outer, DdsExecutingScheduledTick.current());
        }
        assertNull(DdsExecutingScheduledTick.current());
        try (DdsExecutingScheduledTick.Scope ignored = DdsExecutingScheduledTick.enter(null)) {
            assertNull(DdsExecutingScheduledTick.current());
        }
    }
}
