/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.dynamicrecipes;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import carpetddsaddition.feature.dynamicrecipes.compat.UnifiedRecipeCoordinatorCompat;
import net.minecraft.server.MinecraftServer;

/** Drops the source-snapshot cache with its server instance. */
public final class UnifiedRecipeLifecycle implements DdsLifecycleComponent {
    public static final UnifiedRecipeLifecycle INSTANCE = new UnifiedRecipeLifecycle();
    private UnifiedRecipeLifecycle() {}
    @Override public void onServerClosed(MinecraftServer server) {
        UnifiedRecipeCoordinatorCompat.reset();
    }
}
