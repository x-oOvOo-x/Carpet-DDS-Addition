/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class CommandCameraPersistenceTest {
    @TempDir Path directory;
    @AfterEach void closeStore() { CommandCamera.close(); }

    private static CameraState state() {
        return new CameraState("survival", "minecraft:the_nether", 1.25D, -42.0D, 3.5D,
                90.0F, -15.0F, 0.1D, -0.2D, 0.3D, 17.5F, true, -7, 83, 41, 19);
    }

    @Test void v2SessionRoundTripsEveryField() {
        Path file = directory.resolve("camera.properties");
        UUID playerId = UUID.randomUUID();
        CameraState original = state();
        CommandCamera.load(file);
        assertTrue(CommandCamera.beginSession(playerId, original));
        CommandCamera.close();
        CommandCamera.load(file);
        CameraState loaded = CommandCamera.state(playerId);
        assertNotNull(loaded);
        assertEquals(2, loaded.schemaVersion());
        assertEquals(original.gameModeId(), loaded.gameModeId());
        assertEquals(original.dimensionId(), loaded.dimensionId());
        assertEquals(original.x(), loaded.x()); assertEquals(original.y(), loaded.y()); assertEquals(original.z(), loaded.z());
        assertEquals(original.yaw(), loaded.yaw()); assertEquals(original.pitch(), loaded.pitch());
        assertEquals(original.motionX(), loaded.motionX()); assertEquals(original.motionY(), loaded.motionY()); assertEquals(original.motionZ(), loaded.motionZ());
        assertEquals(original.fallDistance(), loaded.fallDistance()); assertEquals(original.fallFlying(), loaded.fallFlying());
        assertEquals(original.airSupply(), loaded.airSupply()); assertEquals(original.remainingFireTicks(), loaded.remainingFireTicks());
        assertEquals(original.frozenTicks(), loaded.frozenTicks()); assertEquals(original.fallFlyingTicks(), loaded.fallFlyingTicks());
        assertTrue(CommandCamera.removeSession(playerId, loaded));
        CommandCamera.close(); CommandCamera.load(file);
        assertFalse(CommandCamera.isCamera(playerId));
    }

    @Test void v1LoadsWithSafeDefaultsAndUpgradesOnSave() throws Exception {
        Path file = directory.resolve("camera.properties");
        UUID id = UUID.randomUUID();
        Properties p = new Properties();
        p.setProperty(id.toString(), "survival\tminecraft:overworld\t1\t2\t3\t4\t5\t0.1\t-0.2\t0.3");
        try (OutputStream out = Files.newOutputStream(file)) { p.store(out, "legacy"); }
        CommandCamera.load(file);
        CameraState loaded = CommandCamera.state(id);
        assertNotNull(loaded); assertEquals(1, loaded.schemaVersion());
        assertEquals(0.0F, loaded.fallDistance()); assertFalse(loaded.fallFlying());
        assertEquals(CameraState.MAX_AIR_SENTINEL, loaded.airSupply());
        assertEquals(0, loaded.remainingFireTicks()); assertEquals(0, loaded.frozenTicks()); assertEquals(0, loaded.fallFlyingTicks());
        CommandCamera.close();
        p.clear(); try (InputStream in = Files.newInputStream(file)) { p.load(in); }
        assertTrue(p.getProperty(id.toString()).startsWith("2\t"));
        CommandCamera.load(file);
        assertEquals(CameraState.MAX_AIR_SENTINEL, CommandCamera.state(id).airSupply());
    }

    @Test void malformedRecordDoesNotPoisonOtherSessions() throws Exception {
        Path file = directory.resolve("camera.properties");
        UUID good = UUID.randomUUID(), bad = UUID.randomUUID();
        Properties p = new Properties();
        p.setProperty(good.toString(), CommandCamera.encode(state()));
        p.setProperty(bad.toString(), "2\tsurvival\tminecraft:overworld\tNaN\t0\t0\t0\t0\t0\t0\t0\t0\tfalse\t300\t0\t0\t0");
        try (OutputStream out = Files.newOutputStream(file)) { p.store(out, "mixed"); }
        CommandCamera.load(file);
        assertTrue(CommandCamera.isCamera(good));
        assertFalse(CommandCamera.isCamera(bad));
    }

    @Test void codecRejectsUnknownVersionIllegalBooleanAndCounters() {
        String valid = CommandCamera.encode(state());
        assertThrows(IllegalArgumentException.class, () -> CommandCamera.decode(valid.replaceFirst("^2", "3")));
        assertThrows(IllegalArgumentException.class, () -> CommandCamera.decode(valid.replace("\ttrue\t", "\tTRUE\t")));
        assertThrows(IllegalArgumentException.class, () -> CommandCamera.decode(valid.substring(0, valid.lastIndexOf('\t') + 1) + "-1"));
        assertThrows(IllegalArgumentException.class, () -> CommandCamera.decode(valid.replace("\t1.25\t", "\tInfinity\t")));
    }

    @Test void beginAndRemoveRollbackWhenPersistenceFails() throws Exception {
        Path file = directory.resolve("camera.properties"), temp = directory.resolve("camera.properties.tmp");
        UUID id = UUID.randomUUID();
        Files.createDirectory(temp);
        CommandCamera.load(file);
        assertFalse(CommandCamera.beginSession(id, state()));
        assertFalse(CommandCamera.isCamera(id));
        CommandCamera.close(); Files.deleteIfExists(temp);
        CommandCamera.load(file);
        assertTrue(CommandCamera.beginSession(id, state()));
        Files.createDirectory(temp);
        CameraState expected = CommandCamera.state(id);
        assertFalse(CommandCamera.removeSession(id, expected));
        assertSame(expected, CommandCamera.state(id));
    }

    @Test void invalidSessionIsRejectedWithoutMutatingStore() {
        Path file = directory.resolve("camera.properties");
        UUID id = UUID.randomUUID();
        CameraState invalid = new CameraState("survival", "minecraft:overworld", Double.NaN, 0, 0,
                0, 0, 0, 0, 0, 0, false, 300, 0, 0, 0);
        CommandCamera.load(file);
        assertFalse(CommandCamera.beginSession(id, invalid));
        assertFalse(CommandCamera.isCamera(id));
        assertFalse(CommandCamera.beginSession(null, state()));
    }

    @Test void atomicReplacementPreservesMultipleSessions() {
        Path file = directory.resolve("camera.properties");
        UUID first = UUID.randomUUID(), second = UUID.randomUUID();
        CommandCamera.load(file);
        assertTrue(CommandCamera.beginSession(first, state()));
        assertTrue(CommandCamera.beginSession(second, state()));
        CommandCamera.close(); CommandCamera.load(file);
        assertNotNull(CommandCamera.state(first));
        assertNotNull(CommandCamera.state(second));
    }

    @Test void cameraReturnHeightKeepsOriginUnlessItIsBelowSafetyFloor() {
        assertEquals(12.5D, CommandCamera.cameraReturnY(12.5D, -64));
        assertEquals(-63.0D, CommandCamera.cameraReturnY(-100.0D, -64));
        assertEquals(1.0D, CommandCamera.cameraReturnY(-50.0D, 0));
    }
}
