/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.rules;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Re-baselines rule observers for each server instance without dispatching feature callbacks. */
public final class DdsRuleEventsLifecycle implements DdsLifecycleComponent {
    public static final DdsRuleEventsLifecycle INSTANCE = new DdsRuleEventsLifecycle();
    private DdsRuleEventsLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { DdsRuleEvents.captureCurrentState(); }
}
