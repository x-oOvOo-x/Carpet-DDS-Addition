/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.playerinventory;

import java.util.Map;
import java.util.WeakHashMap;

/** Pure identity/replay guards shared by strict PlayerInventory structural requests. */
final class DdsPlayerInventoryStructureRequestGuard {
    private DdsPlayerInventoryStructureRequestGuard() {}

    static boolean requestFieldsValid(int containerId, Object target, long generation, long requestId) {
        return containerId >= 0 && target != null && generation > 0L && requestId > 0L;
    }

    static boolean generationMatches(long requested, long current) {
        return requested > 0L && current > 0L && requested == current;
    }

    static boolean bindingMatches(Object sessionViewer, Object viewer,
                                  Object sessionConnection, Object connection,
                                  Object sessionMenu, Object menu,
                                  int sessionContainerId, int containerId) {
        return sessionViewer != null && sessionViewer == viewer
                && sessionConnection != null && sessionConnection == connection
                && sessionMenu != null && sessionMenu == menu
                && sessionContainerId == containerId;
    }

    static boolean liveTargetMatches(Object online, Object liveTarget) {
        return online != null && online == liveTarget;
    }

    static boolean targetPresenceMatches(boolean offlineSession, Object online, Object liveTarget) {
        return offlineSession ? online == null : liveTargetMatches(online, liveTarget);
    }

    static boolean canonicalSizeMatches(int observed, int expected) {
        return observed >= 0 && observed == expected;
    }

    static boolean canonicalEnderCapacityMatches(int observed, int expected) {
        return (observed == 27 || observed == 54) && canonicalSizeMatches(observed, expected);
    }

    static boolean nextGenerationMatches(long sourceGeneration, long currentGeneration) {
        return sourceGeneration > 0L && sourceGeneration < Long.MAX_VALUE
                && currentGeneration == sourceGeneration + 1L;
    }

    static boolean parentMatches(Object capturedConnection, Object currentConnection,
                                 String capturedProvider, String currentProvider,
                                 Object capturedTarget, Object currentTarget,
                                 long sourceGeneration, long currentGeneration,
                                 boolean rootReopenable) {
        return capturedConnection != null && capturedConnection == currentConnection
                && capturedProvider != null && capturedProvider.equals(currentProvider)
                && capturedTarget != null && capturedTarget == currentTarget
                && nextGenerationMatches(sourceGeneration, currentGeneration)
                && rootReopenable;
    }

    static final class Sequence {
        private final Map<Object, Long> lastByConnection = new WeakHashMap<>();

        synchronized boolean accept(Object connection, long requestId) {
            if (connection == null || requestId <= 0L) return false;
            long previous = lastByConnection.getOrDefault(connection, 0L);
            if (requestId <= previous) return false;
            lastByConnection.put(connection, requestId);
            return true;
        }

        synchronized void clear(Object connection) {
            if (connection != null) lastByConnection.remove(connection);
        }

        synchronized void clear() {
            lastByConnection.clear();
        }
    }
}
