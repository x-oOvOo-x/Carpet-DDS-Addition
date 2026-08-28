/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.mendingabsorbxp;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import net.minecraft.server.MinecraftServer;

/** Clears stale process-local mending escrow before a new server session. */
public final class MendingAbsorbXpLifecycle implements DdsLifecycleComponent {
    public static final MendingAbsorbXpLifecycle INSTANCE = new MendingAbsorbXpLifecycle();
    private MendingAbsorbXpLifecycle() {}
    @Override public void onServerLoaded(MinecraftServer server) { MendingAbsorbXp.resetRuntimeState(); }
}
