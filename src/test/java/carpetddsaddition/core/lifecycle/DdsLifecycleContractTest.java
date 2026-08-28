/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.lifecycle;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class DdsLifecycleContractTest {
    @Test void dispatchesLoadReloadCloseInRegistrationOrder() {
        List<String> calls = new ArrayList<>();
        DdsLifecycle.configure(component("a", calls), component("b", calls));
        DdsLifecycle.onServerLoaded(null);
        DdsLifecycle.onReload(null);
        DdsLifecycle.onServerClosed(null);
        assertEquals(List.of("a:load", "b:load", "a:reload", "b:reload", "a:close", "b:close"), calls);
    }

    private static DdsLifecycleComponent component(String name, List<String> calls) {
        return new DdsLifecycleComponent() {
            @Override public void onServerLoaded(net.minecraft.server.MinecraftServer server) { calls.add(name + ":load"); }
            @Override public void onReload(net.minecraft.server.MinecraftServer server) { calls.add(name + ":reload"); }
            @Override public void onServerClosed(net.minecraft.server.MinecraftServer server) { calls.add(name + ":close"); }
        };
    }
}
