/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

/** Immutable, Minecraft-version-independent origin of one DDS Camera session. */
public final class CameraState {
    public static final int CURRENT_SCHEMA = 2;
    /** v1 sessions did not store air; this sentinel means "restore current max air". */
    public static final int MAX_AIR_SENTINEL = Integer.MAX_VALUE;

    private final int schemaVersion;
    private final String gameModeId, dimensionId;
    private final double x, y, z, motionX, motionY, motionZ;
    private final float yaw, pitch, fallDistance;
    private final boolean fallFlying;
    private final int airSupply, remainingFireTicks, frozenTicks, fallFlyingTicks;

    public CameraState(String gameModeId, String dimensionId, double x, double y, double z, float yaw, float pitch,
                       double motionX, double motionY, double motionZ, float fallDistance, boolean fallFlying,
                       int airSupply, int remainingFireTicks, int frozenTicks, int fallFlyingTicks) {
        this(CURRENT_SCHEMA, gameModeId, dimensionId, x, y, z, yaw, pitch, motionX, motionY, motionZ,
                fallDistance, fallFlying, airSupply, remainingFireTicks, frozenTicks, fallFlyingTicks);
    }

    static CameraState legacy(String gameModeId, String dimensionId, double x, double y, double z, float yaw, float pitch,
                              double motionX, double motionY, double motionZ) {
        return new CameraState(1, gameModeId, dimensionId, x, y, z, yaw, pitch, motionX, motionY, motionZ,
                0.0F, false, MAX_AIR_SENTINEL, 0, 0, 0);
    }

    private CameraState(int schemaVersion, String gameModeId, String dimensionId, double x, double y, double z,
                        float yaw, float pitch, double motionX, double motionY, double motionZ, float fallDistance,
                        boolean fallFlying, int airSupply, int remainingFireTicks, int frozenTicks, int fallFlyingTicks) {
        this.schemaVersion = schemaVersion;
        this.gameModeId = gameModeId;
        this.dimensionId = dimensionId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.fallDistance = fallDistance;
        this.fallFlying = fallFlying;
        this.airSupply = airSupply;
        this.remainingFireTicks = remainingFireTicks;
        this.frozenTicks = frozenTicks;
        this.fallFlyingTicks = fallFlyingTicks;
    }

    public int schemaVersion() { return schemaVersion; }
    public String gameModeId() { return gameModeId; }
    public String dimensionId() { return dimensionId; }
    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
    public float yaw() { return yaw; }
    public float pitch() { return pitch; }
    public double motionX() { return motionX; }
    public double motionY() { return motionY; }
    public double motionZ() { return motionZ; }
    public float fallDistance() { return fallDistance; }
    public boolean fallFlying() { return fallFlying; }
    public int airSupply() { return airSupply; }
    public int remainingFireTicks() { return remainingFireTicks; }
    public int frozenTicks() { return frozenTicks; }
    public int fallFlyingTicks() { return fallFlyingTicks; }
}
