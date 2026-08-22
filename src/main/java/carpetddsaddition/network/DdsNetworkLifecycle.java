/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Owns DDS network/session process-local lifecycle state. */
public final class DdsNetworkLifecycle implements DdsLifecycleComponent {
    public static final DdsNetworkLifecycle INSTANCE = new DdsNetworkLifecycle();
    private DdsNetworkLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { DdsServerNetwork.resetRuntimeState(); }
    @Override public void onServerClosed(MinecraftServer server) { DdsServerNetwork.resetRuntimeState(); }
}
