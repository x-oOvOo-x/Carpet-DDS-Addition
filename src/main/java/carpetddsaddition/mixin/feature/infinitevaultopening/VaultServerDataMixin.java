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

package carpetddsaddition.mixin.feature.infinitevaultopening;

import carpetddsaddition.feature.infinitevaultopening.InfiniteVaultOpening;
//#if MC >= 12100
//$$ import java.util.Collections;
//$$ import java.util.Set;
//$$ import java.util.UUID;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.level.block.entity.vault.VaultServerData;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 12100
//$$ @Mixin(VaultServerData.class)
//#else
@Pseudo
@Mixin(targets = "net.minecraft.world.level.block.entity.vault.VaultServerData")
//#endif
public abstract class VaultServerDataMixin {
    //#if MC >= 12100
    //$$ @Inject(
    //$$         method = "hasRewardedPlayer",
    //$$         at = @At("HEAD"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$infiniteVaultOpening(Player player, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (InfiniteVaultOpening.enabled()) cir.setReturnValue(false);
    //$$ }
    //$$
    //$$ @Inject(
    //$$         method = "getRewardedPlayers",
    //$$         at = @At("HEAD"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$hideRewardedPlayers(CallbackInfoReturnable<Set<UUID>> cir) {
    //$$     if (InfiniteVaultOpening.enabled()) cir.setReturnValue(Collections.emptySet());
    //$$ }
    //#endif
}