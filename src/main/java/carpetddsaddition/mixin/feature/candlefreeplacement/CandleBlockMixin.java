/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.candlefreeplacement;

import carpetddsaddition.feature.candlefreeplacement.CandleFreePlacement;
//#if MC >= 11700
//$$ import net.minecraft.world.level.block.CandleBlock;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11700
//$$ @Mixin(CandleBlock.class)
//#else
@Pseudo
@Mixin(targets="carpetddsaddition.disabled.candlefreeplacement.CandleBlock")
//#endif
public abstract class CandleBlockMixin {
    //#if MC >= 11700
    //$$ @Inject(method="canSurvive",at=@At("HEAD"),cancellable=true)
    //$$ private void carpetDDSAddition$candleFreePlacement(CallbackInfoReturnable<Boolean> cir){if(CandleFreePlacement.enabled())cir.setReturnValue(true);}
    //#endif
}
