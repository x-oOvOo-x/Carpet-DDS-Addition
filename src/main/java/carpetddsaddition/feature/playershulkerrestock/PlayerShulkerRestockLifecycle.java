/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Never carry opted-in preferences across server/world instances. */
public final class PlayerShulkerRestockLifecycle implements DdsLifecycleComponent {
    public static final PlayerShulkerRestockLifecycle INSTANCE = new PlayerShulkerRestockLifecycle();
    private PlayerShulkerRestockLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { PlayerShulkerRestockNetwork.reset(); }
    @Override public void onServerClosed(MinecraftServer server) { PlayerShulkerRestockNetwork.reset(); }
}
