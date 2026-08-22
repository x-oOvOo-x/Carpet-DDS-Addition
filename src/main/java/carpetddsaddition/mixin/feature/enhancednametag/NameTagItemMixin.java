/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.enhancednametag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//#if MC >= 11904
//$$ import carpetddsaddition.feature.enhancednametag.compat.EnhancedNameTagInteractionCompat;
//$$ import net.minecraft.world.InteractionHand;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.NameTagItem;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC >= 11904
//$$ @Mixin(NameTagItem.class)
//#else
@Pseudo
@Mixin(targets = "net.minecraft.world.entity.Display")
//#endif
public abstract class NameTagItemMixin {
    //#if MC >= 11904
    //$$ @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$enhancedNameTag(ItemStack stack, Player player, LivingEntity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    //$$     if (EnhancedNameTagInteractionCompat.tryUseOnEntity(stack, player, target)) cir.setReturnValue(InteractionResult.SUCCESS);
    //$$ }
    //#endif
}
