/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import net.minecraft.server.MinecraftServer;

/** Owns QCA process-local menu/session lifecycle state. */
public final class QuickContainerAccessLifecycle implements DdsLifecycleComponent {
    public static final QuickContainerAccessLifecycle INSTANCE = new QuickContainerAccessLifecycle();
    private QuickContainerAccessLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { QuickContainerAccessCompat.resetRuntimeState(); }
    @Override public void onServerClosed(MinecraftServer server) { QuickContainerAccessCompat.resetRuntimeState(); }
}
