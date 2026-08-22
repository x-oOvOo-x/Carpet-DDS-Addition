/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Version-independent state store for DDS safe Camera sessions. */
public final class CommandCamera {
    private static final Map<UUID, CameraState> STATES = new HashMap<>();
    private static final Map<UUID, UUID> FOLLOW_TARGETS = new HashMap<>();
    private static final Map<UUID, Integer> FOLLOW_ATTACH_DELAY = new HashMap<>();
    private CommandCamera() {}

    public static boolean isCamera(UUID playerId) { return STATES.containsKey(playerId); }
    public static CameraState state(UUID playerId) { return STATES.get(playerId); }
    public static boolean beginSession(UUID playerId, CameraState state) {
        if (STATES.containsKey(playerId)) return false;
        STATES.put(playerId, state);
        return true;
    }
    public static boolean removeSession(UUID playerId, CameraState expected) {
        if (!STATES.remove(playerId, expected)) return false;
        clearFollow(playerId);
        return true;
    }
    public static boolean hasFollowTargets() { return !FOLLOW_TARGETS.isEmpty(); }
    public static boolean isFollowing(UUID playerId) { return FOLLOW_TARGETS.containsKey(playerId); }
    public static UUID followTarget(UUID playerId) { return FOLLOW_TARGETS.get(playerId); }
    public static void beginFollow(UUID playerId, UUID targetId, int attachDelayTicks) {
        FOLLOW_TARGETS.put(playerId, targetId);
        if (attachDelayTicks > 0) FOLLOW_ATTACH_DELAY.put(playerId, attachDelayTicks);
        else FOLLOW_ATTACH_DELAY.remove(playerId);
    }
    /** @return -1 if no delayed attach exists. */
    public static int followAttachDelay(UUID playerId) {
        Integer delay = FOLLOW_ATTACH_DELAY.get(playerId);
        return delay == null ? -1 : delay;
    }
    public static void setFollowAttachDelay(UUID playerId, int ticks) {
        if (ticks >= 0) FOLLOW_ATTACH_DELAY.put(playerId, ticks);
        else FOLLOW_ATTACH_DELAY.remove(playerId);
    }
    public static void clearFollowAttachDelay(UUID playerId) { FOLLOW_ATTACH_DELAY.remove(playerId); }
    public static boolean clearFollow(UUID playerId) {
        boolean removed = FOLLOW_TARGETS.remove(playerId) != null;
        FOLLOW_ATTACH_DELAY.remove(playerId);
        return removed;
    }
    /** Clears stale process-local state before a new server accepts players; runtime disable intentionally does not call this. */
    public static void resetRuntimeState() {
        STATES.clear();
        FOLLOW_TARGETS.clear();
        FOLLOW_ATTACH_DELAY.clear();
    }
}
