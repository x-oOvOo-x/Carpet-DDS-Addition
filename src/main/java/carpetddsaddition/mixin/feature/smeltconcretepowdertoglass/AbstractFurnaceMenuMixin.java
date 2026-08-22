/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.smeltconcretepowdertoglass;

import carpetddsaddition.feature.smeltconcretepowdertoglass.compat.ConcretePowderToGlassCompat;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Normal-furnace shift-click fallback for DDS synthetic concrete-powder recipes. */
@Mixin(AbstractFurnaceMenu.class)
public abstract class AbstractFurnaceMenuMixin {
    @Inject(method = "canSmelt", at = @At("RETURN"), cancellable = true)
    private void carpetDDSAddition$smeltConcretePowderToGlass$canSmelt(ItemStack stack,
            CallbackInfoReturnable<Boolean> cir) {
        // Use the concrete menu type: 26.2 removed AbstractFurnaceMenu.recipeType.
        if (!cir.getReturnValue() && (Object) this instanceof FurnaceMenu
                && ConcretePowderToGlassCompat.isEnabledFurnaceInput(stack)) cir.setReturnValue(true);
    }
}
