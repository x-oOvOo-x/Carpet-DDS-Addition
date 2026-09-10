/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.crafterrecipelock;

//#if MC >= 12004
//$$ import carpetddsaddition.feature.crafterrecipelock.compat.CrafterRecipeLockCompat;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.item.AxeItem;
//$$ import net.minecraft.world.item.context.UseOnContext;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12004
//$$ @Mixin(AxeItem.class)
//$$ public abstract class AxeItemMixin {
//$$     @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
//$$     private void dds$unlockCrafter(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
//$$         InteractionResult result = CrafterRecipeLockCompat.tryUnlock(context);
//$$         if (result != null) cir.setReturnValue(result);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockAxeTarget")
public abstract class AxeItemMixin {}
//#endif
