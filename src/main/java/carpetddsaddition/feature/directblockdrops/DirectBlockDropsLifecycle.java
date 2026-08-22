/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Owns directBlockDrops process-local lifecycle state. */
public final class DirectBlockDropsLifecycle implements DdsLifecycleComponent {
    public static final DirectBlockDropsLifecycle INSTANCE = new DirectBlockDropsLifecycle();
    private DirectBlockDropsLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { DirectBlockDrops.resetRuntimeState(); }
    @Override public void onServerClosed(MinecraftServer server) { DirectBlockDrops.resetRuntimeState(); }
}
