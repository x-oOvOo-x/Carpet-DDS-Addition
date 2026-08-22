/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Owns dynamically generated deepslate stonecutter recipe lifecycle. */
public final class StonecutterCutsDeepslateLifecycle implements DdsLifecycleComponent {
    public static final StonecutterCutsDeepslateLifecycle INSTANCE = new StonecutterCutsDeepslateLifecycle();
    private StonecutterCutsDeepslateLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { StonecutterCutsDeepslate.refresh(server, true); }
    @Override public void onReload(MinecraftServer server) { StonecutterCutsDeepslate.refresh(server, true); }
    @Override public void onServerClosed(MinecraftServer server) { StonecutterCutsDeepslate.reset(); }
}
