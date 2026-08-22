/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.commandcamera;

//#if MC >= 11601 && MC <= 260200
//$$ import carpetddsaddition.feature.commandcamera.compat.CommandCameraCompat;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.damagesource.DamageSource;
//#if MC >= 12103
//$$ import net.minecraft.server.level.ServerLevel;
//#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

/** Consumes void damage during an active DDS Camera session and triggers normal restore semantics. */
@SuppressWarnings("unused")
//#if MC >= 11601 && MC <= 260200
//$$ @Mixin(ServerPlayer.class)
//$$ public abstract class ServerPlayerMixin {
    //#if MC <= 12101
    //$$ @Inject(
    //$$         method = "hurt(" +
    //$$                 "Lnet/minecraft/world/damagesource/DamageSource;" +
    //$$                 "F" +
    //$$                 ")Z",
    //$$         at = @At("HEAD"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$cameraVoidAutoBackLegacy(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (CommandCameraCompat.handleVoidDamage((ServerPlayer) (Object) this, source)) cir.setReturnValue(false);
    //$$ }
    //#else
    //$$ @Inject(
    //$$         method = "hurtServer(" +
    //$$                 "Lnet/minecraft/server/level/ServerLevel;" +
    //$$                 "Lnet/minecraft/world/damagesource/DamageSource;" +
    //$$                 "F" +
    //$$                 ")Z",
    //$$         at = @At("HEAD"),
    //$$         cancellable = true
    //$$ )
    //$$ private void carpetDDSAddition$cameraVoidAutoBackModern(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (CommandCameraCompat.handleVoidDamage((ServerPlayer) (Object) this, source)) cir.setReturnValue(false);
    //$$ }
    //#endif
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CommandCameraServerPlayerTarget")
public abstract class ServerPlayerMixin {}
//#endif
