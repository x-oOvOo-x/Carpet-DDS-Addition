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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package carpetddsaddition.mixin.feature.disablezombiesieges;

import carpetddsaddition.feature.disablezombiesieges.DisableZombieSieges;
import net.minecraft.world.entity.ai.village.VillageSiege;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VillageSiege.class)
public abstract class VillageSiegeMixin {
    @ModifyVariable(
            method = "tick",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private boolean carpetDDSAddition$disableZombieSieges(boolean spawnHostiles) {
        return DisableZombieSieges.enabled() ? false : spawnHostiles;
    }
}