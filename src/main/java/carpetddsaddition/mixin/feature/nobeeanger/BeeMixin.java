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

package carpetddsaddition.mixin.feature.nobeeanger;

import carpetddsaddition.feature.nobeeanger.NoBeeAnger;
//#if MC >= 12111
//$$ import net.minecraft.world.entity.animal.bee.Bee;
//#elseif MC >= 11500
import net.minecraft.world.entity.animal.Bee;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//#if MC >= 11500
@Mixin(Bee.class)
//#else
//$$ @Pseudo
//$$ @Mixin(targets = "net.minecraft.world.entity.animal.Bee")
//#endif
public abstract class BeeMixin {
    //#if MC >= 12111
    //$$ @ModifyVariable(
    //$$         method = "setPersistentAngerEndTime",
    //$$         at = @At("HEAD"),
    //$$         argsOnly = true
    //$$ )
    //$$ private long carpetDDSAddition$noBeeAnger(long endTime) {
    //$$     return NoBeeAnger.enabled() ? -1L : endTime;
    //$$ }
    //#elseif MC >= 11600
    //$$ @ModifyVariable(
    //$$         method = "setRemainingPersistentAngerTime",
    //$$         at = @At("HEAD"),
    //$$         argsOnly = true
    //$$ )
    //$$ private int carpetDDSAddition$noBeeAnger(int time) {
    //$$     return NoBeeAnger.enabled() ? 0 : time;
    //$$ }
    //#elseif MC >= 11500
    @ModifyVariable(
            method = "setAngerTime",
            at = @At("HEAD"),
            argsOnly = true
    )
    private int carpetDDSAddition$noBeeAnger(int time) {
        return NoBeeAnger.enabled() ? 0 : time;
    }
    //#endif
}