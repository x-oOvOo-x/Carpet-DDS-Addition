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

package carpetddsaddition.mixin.integration.carpet;

import carpet.CarpetServer;
import carpet.utils.Messenger;
//#if MC >= 11500
import carpet.utils.Translations;
//#endif
import carpetddsaddition.CarpetDDSAddition;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11901
//$$ import carpet.api.settings.SettingsManager;
//#else
import carpet.settings.SettingsManager;
//#endif

@Mixin(SettingsManager.class)
public abstract class SettingsManagerMixin {
    @Inject(
            method = "listAllSettings",
            //#if MC < 11901
            slice = @Slice(from = @At(
                    value = "CONSTANT",
                    //#if MC >= 11500
                    args = "stringValue=ui.version",
                    //#else
                    //$$ args = "stringValue= version: ",
                    //#endif
                    ordinal = 0
            )),
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 11901
                    //$$ target = "Lcarpet/api/settings/SettingsManager;getCategories()Ljava/lang/Iterable;"
                    //#elseif MC >= 11600
                    //$$ target = "Lcarpet/settings/SettingsManager;getCategories()Ljava/lang/Iterable;",
                    //$$ ordinal = 0
                    //#else
                    target = "Lnet/minecraft/commands/CommandSourceStack;getPlayerOrException()Lnet/minecraft/server/level/ServerPlayer;",
                    ordinal = 0,
                    remap = true
                    //#endif
            ),
            remap = false
    )
    private void carpetDDSAddition$showVersion(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        if ((SettingsManager) (Object) this != CarpetServer.settingsManager) return;
        Messenger.m(source,
                //#if MC >= 11901
                //$$ "g " + CarpetDDSAddition.MOD_NAME + " " + Translations.tr("carpet.settings.command.version", "version")
                //$$         + ": " + CarpetDDSAddition.getVersion()
                //#elseif MC >= 11500
                "g " + CarpetDDSAddition.MOD_NAME + " " + Translations.tr("ui.version", "version")
                        + ": " + CarpetDDSAddition.getVersion()
                //#else
                //$$ "g " + CarpetDDSAddition.MOD_NAME + " version: " + CarpetDDSAddition.getVersion()
                //#endif
        );
    }
}