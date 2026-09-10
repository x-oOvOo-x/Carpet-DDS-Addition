/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition;

import carpet.CarpetServer;
import carpetddsaddition.core.bootstrap.DdsNetworkBootstrap;
import carpetddsaddition.core.bootstrap.DdsRuntimeBootstrap;
import carpetddsaddition.core.mixin.DdsMixinAudit;
import carpetddsaddition.platform.carpet.CarpetExtensionCompat;
import carpetddsaddition.platform.carpet.CarpetRuleCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class CarpetDDSAddition extends CarpetExtensionCompat implements ModInitializer {
    public static final String MOD_ID = "carpet-dds-addition";
    public static final String MOD_NAME = "Carpet DDS Addition";
    private static String version = "unknown";

    @Override
    public void onInitialize() {
        version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new)
                .getMetadata().getVersion().getFriendlyString();
        DdsMixinAudit.runIfRequested();
        DdsNetworkBootstrap.initialize();
        DdsRuntimeBootstrap.initialize();
        CarpetServer.manageExtension(this);
        // Legacy Carpet may already have initialized SettingsManager before extension registration.
        CarpetRuleCompat.registerRules();
    }

    @Override public void onGameStarted() { CarpetRuleCompat.registerRules(); }
    public static String getVersion() { return version; }
    @Override public String version() { return version; }
}
