/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.gca.fakeplayer;

/** Exact compatibility gate for the historical Provider stop transport. */
final class DdsProviderStopGate {
    private DdsProviderStopGate() {}

    static boolean isExactStop(DdsFakePlayerAction action) {
        return action == DdsFakePlayerAction.STOP_ATTACK || action == DdsFakePlayerAction.STOP_USE;
    }

    static boolean allowsLegacy(DdsFakePlayerAction action, boolean providerInventory,
                                boolean targetUuidMatches, boolean liveInstanceMatches,
                                boolean fake, boolean alive) {
        return isExactStop(action) && providerInventory && targetUuidMatches
                && liveInstanceMatches && fake && alive;
    }
}
