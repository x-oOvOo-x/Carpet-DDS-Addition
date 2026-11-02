/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.crafterrecipelock;

//#if MC >= 260300
//$$ import carpetddsaddition.feature.crafterrecipelock.compat.CrafterRecipeLockCompat;
//$$ import net.minecraft.tags.ItemTags;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.context.UseOnContext;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ @Mixin(ItemStack.class)
//$$ public abstract class ItemStackAxeMixin {
//$$     @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
//$$     private void dds$unlockCrafterWithAxe(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
//$$         if (!((ItemStack) (Object) this).is(ItemTags.AXES)) return;
//$$         InteractionResult result = CrafterRecipeLockCompat.tryUnlock(context);
//$$         if (result != null) cir.setReturnValue(result);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockItemStackAxeTarget")
public abstract class ItemStackAxeMixin {}
//#endif
