/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/** Persistent DDS Camera sessions plus process-local follow/protection state. */
public final class CommandCamera {
    private static final String HEADER = "Carpet DDS Addition Camera sessions";
    private static final double MAX_POSITION = 3.2E7D, MAX_MOTION = 1.0E5D;
    private static final float MAX_FALL_DISTANCE = 1.0E7F;
    private static final int MAX_COUNTER = 100_000_000, MAX_AIR_ABS = 1_000_000;
    private static final long VOID_FEEDBACK_INTERVAL_NANOS = 2_000_000_000L;
    private static final Map<UUID, CameraState> STATES = new HashMap<>();
    private static final Map<UUID, UUID> FOLLOW_TARGETS = new HashMap<>();
    private static final Map<UUID, Integer> FOLLOW_ATTACH_DELAY = new HashMap<>();
    private static final Map<UUID, Long> VOID_FEEDBACK_AT = new HashMap<>();
    private static Path sessionFile;
    private CommandCamera() {}

    public static boolean isCamera(UUID playerId) { return STATES.containsKey(playerId); }
    public static CameraState state(UUID playerId) { return STATES.get(playerId); }
    public static synchronized boolean beginSession(UUID playerId, CameraState state) {
        if (sessionFile == null || playerId == null || state == null || STATES.containsKey(playerId)) return false;
        try { validate(state); }
        catch (IllegalArgumentException exception) { warn("Rejected invalid session for " + playerId, exception); return false; }
        STATES.put(playerId, state);
        if (save()) return true;
        STATES.remove(playerId);
        return false;
    }
    public static synchronized boolean removeSession(UUID playerId, CameraState expected) {
        if (!STATES.remove(playerId, expected)) return false;
        if (!save()) {
            STATES.put(playerId, expected);
            return false;
        }
        clearRuntime(playerId);
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
    public static void clearRuntime(UUID playerId) {
        clearFollow(playerId);
        VOID_FEEDBACK_AT.remove(playerId);
    }
    public static boolean shouldNotifyVoid(UUID playerId) {
        long now = System.nanoTime(), previous = VOID_FEEDBACK_AT.getOrDefault(playerId, Long.MIN_VALUE);
        if (previous != Long.MIN_VALUE && now - previous < VOID_FEEDBACK_INTERVAL_NANOS) return false;
        VOID_FEEDBACK_AT.put(playerId, now);
        return true;
    }
    public static double cameraReturnY(double originY, int minBuildHeight) {
        return Math.max(originY, minBuildHeight + 1.0D);
    }

    /** Loads world-owned sessions and clears only process-local runtime state. */
    public static synchronized void load(Path file) {
        STATES.clear();
        FOLLOW_TARGETS.clear();
        FOLLOW_ATTACH_DELAY.clear();
        VOID_FEEDBACK_AT.clear();
        sessionFile = file;
        if (!Files.isRegularFile(file)) return;
        Properties values = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            values.load(input);
        } catch (IOException exception) {
            warn("Failed to load", exception);
            return;
        }
        for (String key : values.stringPropertyNames()) {
            try { STATES.put(UUID.fromString(key), decode(values.getProperty(key))); }
            catch (IllegalArgumentException exception) { warn("Ignored invalid session for " + key, exception); }
        }
    }

    public static synchronized void loadIfNeeded(Path file) {
        if (file.equals(sessionFile)) return;
        if (sessionFile != null) save();
        load(file);
    }

    public static synchronized void close() {
        save();
        STATES.clear();
        FOLLOW_TARGETS.clear();
        FOLLOW_ATTACH_DELAY.clear();
        VOID_FEEDBACK_AT.clear();
        sessionFile = null;
    }

    private static boolean save() {
        if (sessionFile == null) return false;
        Path parent = sessionFile.getParent(), temporary = sessionFile.resolveSibling(sessionFile.getFileName() + ".tmp");
        try {
            if (parent != null) Files.createDirectories(parent);
            Properties values = new Properties();
            for (Map.Entry<UUID, CameraState> entry : STATES.entrySet())
                values.setProperty(entry.getKey().toString(), encode(entry.getValue()));
            try (OutputStream output = Files.newOutputStream(temporary)) { values.store(output, HEADER); }
            try { Files.move(temporary, sessionFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, sessionFile, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException exception) {
            warn("Failed to save", exception);
            try { Files.deleteIfExists(temporary); } catch (IOException ignored) {}
            return false;
        }
    }

    static String encode(CameraState state) {
        validate(state);
        return String.join("\t", "2", state.gameModeId(), state.dimensionId(), Double.toString(state.x()),
                Double.toString(state.y()), Double.toString(state.z()), Float.toString(state.yaw()),
                Float.toString(state.pitch()), Double.toString(state.motionX()), Double.toString(state.motionY()),
                Double.toString(state.motionZ()), Float.toString(state.fallDistance()), Boolean.toString(state.fallFlying()),
                Integer.toString(state.airSupply()), Integer.toString(state.remainingFireTicks()),
                Integer.toString(state.frozenTicks()), Integer.toString(state.fallFlyingTicks()));
    }

    static CameraState decode(String value) {
        if (value == null) throw new IllegalArgumentException("Missing session value");
        String[] f = value.split("\t", -1);
        CameraState state;
        if (f.length == 10) {
            state = CameraState.legacy(f[0], f[1], parseDouble(f[2], "x"), parseDouble(f[3], "y"),
                    parseDouble(f[4], "z"), parseFloat(f[5], "yaw"), parseFloat(f[6], "pitch"),
                    parseDouble(f[7], "motionX"), parseDouble(f[8], "motionY"), parseDouble(f[9], "motionZ"));
        } else {
            if (f.length != 17) throw new IllegalArgumentException("Expected 10-field v1 or 17-field v2 session");
            if (!"2".equals(f[0])) throw new IllegalArgumentException("Unsupported Camera session version: " + f[0]);
            state = new CameraState(f[1], f[2], parseDouble(f[3], "x"), parseDouble(f[4], "y"),
                    parseDouble(f[5], "z"), parseFloat(f[6], "yaw"), parseFloat(f[7], "pitch"),
                    parseDouble(f[8], "motionX"), parseDouble(f[9], "motionY"), parseDouble(f[10], "motionZ"),
                    parseFloat(f[11], "fallDistance"), parseBoolean(f[12], "fallFlying"), parseInt(f[13], "airSupply"),
                    parseInt(f[14], "remainingFireTicks"), parseInt(f[15], "frozenTicks"), parseInt(f[16], "fallFlyingTicks"));
        }
        validate(state);
        return state;
    }

    private static void validate(CameraState s) {
        if (!("survival".equals(s.gameModeId()) || "creative".equals(s.gameModeId())
                || "adventure".equals(s.gameModeId()) || "spectator".equals(s.gameModeId())))
            throw new IllegalArgumentException("Invalid game mode: " + s.gameModeId());
        if (s.dimensionId() == null || s.dimensionId().isEmpty() || s.dimensionId().length() > 256)
            throw new IllegalArgumentException("Invalid dimension id");
        bounded(s.x(), MAX_POSITION, "x"); bounded(s.y(), MAX_POSITION, "y"); bounded(s.z(), MAX_POSITION, "z");
        finite(s.yaw(), "yaw"); finite(s.pitch(), "pitch");
        if (Math.abs(s.pitch()) > 90.001F) throw new IllegalArgumentException("Invalid pitch");
        bounded(s.motionX(), MAX_MOTION, "motionX"); bounded(s.motionY(), MAX_MOTION, "motionY");
        bounded(s.motionZ(), MAX_MOTION, "motionZ");
        finite(s.fallDistance(), "fallDistance");
        if (s.fallDistance() < 0.0F || s.fallDistance() > MAX_FALL_DISTANCE)
            throw new IllegalArgumentException("Invalid fallDistance");
        int air = s.airSupply();
        if (air != CameraState.MAX_AIR_SENTINEL && (air < -MAX_AIR_ABS || air > MAX_AIR_ABS))
            throw new IllegalArgumentException("Invalid airSupply");
        counter(s.remainingFireTicks(), "remainingFireTicks");
        counter(s.frozenTicks(), "frozenTicks");
        counter(s.fallFlyingTicks(), "fallFlyingTicks");
    }
    private static void bounded(double value, double max, String name) {
        if (!Double.isFinite(value) || Math.abs(value) > max) throw new IllegalArgumentException("Invalid " + name);
    }
    private static void finite(float value, String name) {
        if (!Float.isFinite(value)) throw new IllegalArgumentException("Invalid " + name);
    }
    private static void counter(int value, String name) {
        if (value < 0 || value > MAX_COUNTER) throw new IllegalArgumentException("Invalid " + name);
    }
    private static double parseDouble(String value, String name) {
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid " + name, e); }
    }
    private static float parseFloat(String value, String name) {
        try { return Float.parseFloat(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid " + name, e); }
    }
    private static int parseInt(String value, String name) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid " + name, e); }
    }
    private static boolean parseBoolean(String value, String name) {
        if ("true".equals(value)) return true;
        if ("false".equals(value)) return false;
        throw new IllegalArgumentException("Invalid " + name);
    }

    private static void warn(String message, Exception exception) {
        System.err.println("[Carpet DDS Addition] " + message + " Camera sessions: " + exception.getMessage());
    }
}
