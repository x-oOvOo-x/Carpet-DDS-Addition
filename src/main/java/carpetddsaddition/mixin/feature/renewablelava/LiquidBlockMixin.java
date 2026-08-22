/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.renewablelava;

import carpetddsaddition.feature.renewablelava.compat.RenewableLavaGeneratorCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
    //#if MC >= 11700
    //$$ @Inject(method = "shouldSpreadLiquid", at = @At("TAIL"), cancellable = true)
    //$$ private void carpetDDSAddition$renewableLavaGenerator(Level level, BlockPos pos, BlockState state,
    //$$         CallbackInfoReturnable<Boolean> cir) {
    //$$     if (RenewableLavaGeneratorCompat.tryGenerate(level, pos)) cir.setReturnValue(false);
    //$$ }
    //#endif
}
