/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.carpet.gca;

//#if MC >= 11904 && MC <= 260200
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerAccess;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.InteractionHand;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.player.Player;
//#if MC >= 260000
//$$ import net.minecraft.world.phys.Vec3;
//#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$
//$$ @Mixin(value = Player.class, priority = 1100)
//$$ public abstract class FakePlayerInteractionMixin {
//$$     @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
//$$     private void dds$enhanceGcaFakePlayerInventory(
//$$             Entity entity, InteractionHand hand,
//#if MC >= 260000
//$$             Vec3 interactionPos,
//#endif
//$$             CallbackInfoReturnable<InteractionResult> cir) {
//$$         Player self = (Player) (Object) this;
//$$         if (!(self instanceof ServerPlayer) || !(entity instanceof EntityPlayerMPFake)) return;
//$$         ServerPlayer viewer = (ServerPlayer) self;
//$$         if (!DdsFakePlayerAccess.tryOpenEnhanced(viewer, (EntityPlayerMPFake) entity, viewer.isShiftKeyDown())) return;
//$$         viewer.stopUsingItem();
//$$         cir.setReturnValue(InteractionResult.CONSUME);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.GcaFakePlayerInteractionTarget")
public abstract class FakePlayerInteractionMixin {}
//#endif
