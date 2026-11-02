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

import carpet.CarpetExtension;
import carpetddsaddition.core.lifecycle.DdsLifecycle;
//#if MC >= 11700
//$$ import net.minecraft.server.MinecraftServer;
//#endif
//#if MC >= 11902
//$$ import carpetddsaddition.generated.DDSRuleTranslations;
//$$ import java.util.Map;
//#endif

public abstract class CarpetExtensionCompat implements CarpetExtension {
    //#if MC >= 11902
    //$$ @Override
    //$$ public void registerCommands(
    //$$         com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher,
    //$$         net.minecraft.commands.CommandBuildContext commandBuildContext) {
    //$$     DdsRuleCommandCompat.register(dispatcher);
    //$$ }
    //#elseif MC >= 11601
    //$$ @Override
    //$$ public void registerCommands(
    //$$         com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
    //$$     DdsRuleCommandCompat.register(dispatcher);
    //$$ }
    //#endif
    //#if MC >= 11902
    //$$ @Override
    //$$ public carpet.api.settings.SettingsManager extensionSettingsManager() {
    //$$     return CarpetRuleCompat.settingsManager();
    //$$ }
    //#else
    @Override
    public carpet.settings.SettingsManager customSettingsManager() {
        return CarpetRuleCompat.settingsManager();
    }
    //#endif
    //#if MC >= 11902
    //$$ @Override public Map<String, String> canHasTranslations(String lang) { return DDSRuleTranslations.forLanguage(lang); }
    //#endif
    //#if MC >= 11700
    //$$ @Override public void onServerLoaded(MinecraftServer server) { DdsLifecycle.onServerLoaded(server); }
    //$$ @Override public void onReload(MinecraftServer server) { DdsLifecycle.onReload(server); }
    //$$ @Override public void onTick(MinecraftServer server) { DdsLifecycle.onTick(server); }
    //$$ @Override public void onServerClosed(MinecraftServer server) { DdsLifecycle.onServerClosed(server); }
    //#endif
}