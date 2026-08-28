/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.rules;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class DdsRuleEventsContractTest {
    @Test void transitionAndRecaptureContract() {
        AtomicBoolean first = new AtomicBoolean(), second = new AtomicBoolean();
        List<String> events = new ArrayList<>();
        DdsRuleEvents.configure(
                DdsRuleEvents.observe(first::get, server -> events.add("first")),
                DdsRuleEvents.observe(second::get, server -> events.add("second")));

        DdsRuleEvents.captureCurrentState();
        assertEquals(List.of(), events);
        DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of(), events);

        first.set(true); DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of("first"), events);
        DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of("first"), events);

        first.set(false); second.set(true); DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of("first", "first", "second"), events);

        events.clear();
        DdsRuleEvents.captureCurrentState();
        DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of(), events);
        first.set(true); second.set(false); DdsRuleEvents.onRuleChanged(null);
        assertEquals(List.of("first", "second"), events);
    }
}
