/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Clears wood stonecutter runtime state when the server closes. */
public final class StonecutterCutsWoodLifecycle implements DdsLifecycleComponent {
    public static final StonecutterCutsWoodLifecycle INSTANCE=new StonecutterCutsWoodLifecycle();
    private StonecutterCutsWoodLifecycle() {}
    @Override public void onServerClosed(MinecraftServer server){StonecutterCutsWood.reset();}
}
