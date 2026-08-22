/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.nosweetberrybusheffects;

import carpetddsaddition.feature.nosweetberrybusheffects.NoSweetBerryBushEffects;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$noSweetBerryBushEffects(CallbackInfo ci) {
        if (NoSweetBerryBushEffects.enabled()) ci.cancel();
    }
}
