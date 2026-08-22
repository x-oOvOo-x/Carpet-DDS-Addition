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

package carpetddsaddition.mixin.feature.noairborneminingpenalty;

import carpetddsaddition.feature.noairborneminingpenalty.NoAirborneMiningPenalty;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public abstract class PlayerMixin {
    /* Before 1.21 underwater is ordinal 0 and airborne ordinal 1; 1.21+ moves underwater to SUBMERGED_MINING_SPEED. */
    //#if MC >= 12100
    //$$ @ModifyConstant(
    //$$         method = "getDestroySpeed",
    //$$         constant = @Constant(floatValue = 5.0F, ordinal = 0)
    //$$ )
    //#else
    @ModifyConstant(
            method = "getDestroySpeed",
            constant = @Constant(floatValue = 5.0F, ordinal = 1)
    )
    //#endif
    private float carpetDDSAddition$removeAirborneMiningPenalty(float divisor) {
        return NoAirborneMiningPenalty.enabled() ? 1.0F : divisor;
    }
}