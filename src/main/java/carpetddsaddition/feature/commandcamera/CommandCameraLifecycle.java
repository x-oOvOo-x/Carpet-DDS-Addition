/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import carpetddsaddition.feature.commandcamera.compat.CommandCameraPersistenceCompat;
import net.minecraft.server.MinecraftServer;

/** Owns world-persistent Camera sessions and process-local follow state. */
public final class CommandCameraLifecycle implements DdsLifecycleComponent {
    public static final CommandCameraLifecycle INSTANCE = new CommandCameraLifecycle();
    private static final String FILE_NAME = "carpet-dds-addition-camera.properties";
    private CommandCameraLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) {
        CommandCameraPersistenceCompat.load(server, FILE_NAME);
    }
    @Override public void onServerClosed(MinecraftServer server) { CommandCamera.close(); }
}
