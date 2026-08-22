/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.lifecycle;

import net.minecraft.server.MinecraftServer;

/** Central DDS lifecycle dispatcher; concrete components are supplied by the bootstrap layer. */
public final class DdsLifecycle {
    private static DdsLifecycleComponent[] components = {};
    private static boolean configured;
    private DdsLifecycle() {}

    public static synchronized void configure(DdsLifecycleComponent... values) {
        if (configured) return;
        components = values.clone();
        configured = true;
    }

    public static void onServerLoaded(MinecraftServer server) {
        for (DdsLifecycleComponent component : components) component.onServerLoaded(server);
    }
    public static void onReload(MinecraftServer server) {
        for (DdsLifecycleComponent component : components) component.onReload(server);
    }
    public static void onServerClosed(MinecraftServer server) {
        for (DdsLifecycleComponent component : components) component.onServerClosed(server);
    }
}
