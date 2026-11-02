package carpetddsaddition.integration.gca.fakeplayer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class DdsProviderStopGateTest {
    @Test void exactStopsRequireEveryLegacyAuthorizationFact() {
        assertTrue(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, true, true, true, true, true));
        assertTrue(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_USE, true, true, true, true, true));

        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.ATTACK_CONTINUOUS, true, true, true, true, true));
        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, false, true, true, true, true));
        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, true, false, true, true, true));
        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, true, true, false, true, true));
        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, true, true, true, false, true));
        assertFalse(DdsProviderStopGate.allowsLegacy(
                DdsFakePlayerAction.STOP_ATTACK, true, true, true, true, false));
    }
}
