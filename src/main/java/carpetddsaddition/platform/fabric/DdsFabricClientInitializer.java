/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.fabric;

import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerClientNetwork;
import net.fabricmc.api.ClientModInitializer;

/** Client-only bootstrap; dedicated servers never load resource-pack or enhanced-screen setup. */
public final class DdsFabricClientInitializer implements ClientModInitializer {
    @Override public void onInitializeClient() {
        DdsBuiltinResourcePackCompat.register();
        DdsFakePlayerClientNetwork.initialize();
    }
}
