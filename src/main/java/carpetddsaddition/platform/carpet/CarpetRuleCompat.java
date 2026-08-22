/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
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
package carpetddsaddition.platform.carpet;

import carpet.CarpetServer;
import carpetddsaddition.core.rules.DdsRuleEvents;
import carpetddsaddition.generated.CarpetDDSAdditionSettings;

@SuppressWarnings({"deprecation", "removal"})
public final class CarpetRuleCompat {
    private static Object registeredManager;
    private CarpetRuleCompat() {}

    public static void registerRules() {
        Object manager = CarpetServer.settingsManager;
        if (manager == null || manager == registeredManager) return;
        CarpetServer.settingsManager.parseSettingsClass(CarpetDDSAdditionSettings.class);
        registerRuleObserver();
        DdsRuleEvents.captureCurrentState();
        registeredManager = manager;
    }

    private static void registerRuleObserver() {
        //#if MC >= 11901
        //$$ CarpetServer.settingsManager.registerRuleObserver((source, changedRule, userInput) ->
        //$$         DdsRuleEvents.onRuleChanged(CarpetServer.minecraft_server));
        //#elseif MC >= 11700
        //$$ CarpetServer.settingsManager.addRuleObserver((source, changedRule, userInput) ->
        //$$         DdsRuleEvents.onRuleChanged(CarpetServer.minecraft_server));
        //#endif
    }
}
