/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Owns dynamically generated wood stonecutter recipe lifecycle. */
public final class StonecutterCutsWoodLifecycle implements DdsLifecycleComponent {
    public static final StonecutterCutsWoodLifecycle INSTANCE=new StonecutterCutsWoodLifecycle();
    private StonecutterCutsWoodLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { StonecutterCutsWood.refresh(server,true); }
    @Override public void onReload(MinecraftServer server) { StonecutterCutsWood.refresh(server,true); }
    @Override public void onServerClosed(MinecraftServer server) { StonecutterCutsWood.reset(); }
}
