/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.enhancednametag;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import carpetddsaddition.feature.enhancednametag.compat.BlockAnnotationTracker;
import carpetddsaddition.feature.enhancednametag.compat.EnhancedNameTagSavedData;
import net.minecraft.server.MinecraftServer;

/** Owns enhancedNameTag runtime caches. */
public final class EnhancedNameTagLifecycle implements DdsLifecycleComponent {
    public static final EnhancedNameTagLifecycle INSTANCE = new EnhancedNameTagLifecycle();
    private EnhancedNameTagLifecycle() {}
    @Override public void onServerClosed(MinecraftServer server) {
        BlockAnnotationTracker.resetRuntimeState();
        EnhancedNameTagSavedData.resetRuntimeState();
    }
}
