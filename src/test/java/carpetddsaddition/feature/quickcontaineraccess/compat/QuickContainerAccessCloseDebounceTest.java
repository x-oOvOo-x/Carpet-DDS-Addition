package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickContainerAccessCloseDebounceTest {
    @Test void rapidClicksStayInsideGraceWindow() {
        long start = 1_000_000_000L;
        assertTrue(QuickContainerAccessCloseDebounce.withinGrace(start, start));
        assertTrue(QuickContainerAccessCloseDebounce.withinGrace(start, start + QuickContainerAccessCloseDebounce.GRACE_NANOS - 1L));
    }

    @Test void quietPeriodArmsSourceSlotClose() {
        long start = 1_000_000_000L;
        assertFalse(QuickContainerAccessCloseDebounce.withinGrace(start, start + QuickContainerAccessCloseDebounce.GRACE_NANOS));
        assertFalse(QuickContainerAccessCloseDebounce.withinGrace(start, start + QuickContainerAccessCloseDebounce.GRACE_NANOS + 1L));
    }

    @Test void nonMonotonicTimestampDoesNotSuppress() {
        assertFalse(QuickContainerAccessCloseDebounce.withinGrace(10L, 9L));
    }
}
