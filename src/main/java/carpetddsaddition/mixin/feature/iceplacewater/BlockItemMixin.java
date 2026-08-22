/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.iceplacewater;

import carpetddsaddition.feature.iceplacewater.compat.IcePlaceWaterCompat;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
//#if MC >= 11602
//$$ import net.minecraft.world.item.context.UseOnContext;
//#else
import net.minecraft.world.item.UseOnContext;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$icePlaceWater(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = IcePlaceWaterCompat.tryPlace((BlockItem) (Object) this, context);
        if (result != null) cir.setReturnValue(result);
    }
}
