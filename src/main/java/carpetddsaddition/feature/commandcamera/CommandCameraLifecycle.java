/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Owns safe Camera process-local lifecycle state. */
public final class CommandCameraLifecycle implements DdsLifecycleComponent {
    public static final CommandCameraLifecycle INSTANCE = new CommandCameraLifecycle();
    private CommandCameraLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { CommandCamera.resetRuntimeState(); }
    // No close reset: PlayerList#remove must restore active origins before stale state is cleared on next load.
}
