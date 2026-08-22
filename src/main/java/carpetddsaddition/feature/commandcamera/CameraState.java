/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

/** Immutable, Minecraft-version-independent origin of one DDS Camera session. */
public final class CameraState {
    private final String dimensionId;
    private final double x, y, z, motionX, motionY, motionZ;
    private final float yaw, pitch;

    public CameraState(String dimensionId, double x, double y, double z, float yaw, float pitch,
                       double motionX, double motionY, double motionZ) {
        this.dimensionId = dimensionId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public String dimensionId() { return dimensionId; }
    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
    public float yaw() { return yaw; }
    public float pitch() { return pitch; }
    public double motionX() { return motionX; }
    public double motionY() { return motionY; }
    public double motionZ() { return motionZ; }
}
