/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.lifecycle;

import net.minecraft.server.MinecraftServer;

/** Minimal DDS-owned lifecycle surface; Carpet remains only the platform event source. */
public interface DdsLifecycleComponent {
    default void onServerLoaded(MinecraftServer server) {}
    default void onReload(MinecraftServer server) {}
    default void onServerClosed(MinecraftServer server) {}
}
