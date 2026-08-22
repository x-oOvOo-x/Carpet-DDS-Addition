/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.commandcamera;

//#if MC >= 11601 && MC <= 260200
//$$ import carpetddsaddition.feature.commandcamera.compat.CommandCameraCompat;
//$$ import carpetddsaddition.feature.commandcamera.compat.CommandCameraMapTeleportCompat;
//$$ import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.network.ServerGamePacketListenerImpl;
//#if MC >= 11902 && MC <= 12004
//$$ import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
//#endif
//#if MC <= 12101
//$$ import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
//#else
//$$ import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
//#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 11601 && MC <= 260200
//$$ @Mixin(ServerGamePacketListenerImpl.class)
//$$ public abstract class ServerGamePacketListenerImplMixin {
//$$     @Shadow public ServerPlayer player;
    //#if MC <= 11802
    //$$ @Inject(method = "handleCommand", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$cameraSelfTeleportLegacy(String command, CallbackInfo ci) {
    //$$     carpetDDSAddition$handleCameraSelfTeleport(command, ci);
    //$$ }
    //#elseif MC <= 12004
    //$$ @Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$cameraSelfTeleportPacket(ServerboundChatCommandPacket packet, CallbackInfo ci) {
    //$$     carpetDDSAddition$handleCameraSelfTeleport(packet.command(), ci);
    //$$ }
    //#else
    //$$ @Inject(method = "performUnsignedChatCommand", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$cameraSelfTeleportModern(String command, CallbackInfo ci) {
    //$$     carpetDDSAddition$handleCameraSelfTeleport(command, ci);
    //$$ }
    //#endif
//$$
//$$     private void carpetDDSAddition$handleCameraSelfTeleport(String command, CallbackInfo ci) {
//$$         if (CommandCameraMapTeleportCompat.tryHandle(player, command)) ci.cancel();
//$$     }
//$$
//$$     @Inject(method = "tick", at = @At("TAIL"))
//$$     private void carpetDDSAddition$tickCameraFollow(CallbackInfo ci) { CommandCameraCompat.tick(player); }
//$$
//$$     @Inject(method = "handleMovePlayer", at = @At("HEAD"), cancellable = true)
//$$     private void carpetDDSAddition$lockFollowMovement(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
//$$         if (CommandCameraCompat.shouldBlockMovement(player)) ci.cancel();
//$$     }
    //#if MC <= 12101
    //$$ @Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$releaseCameraFollowLegacy(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
    //$$     if (packet.getAction() != ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY) return;
    //$$     if (CommandCameraCompat.handleFollowInput(player, true)) ci.cancel();
    //$$ }
    //#else
    //$$ @Inject(method = "handlePlayerInput", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$lockFollowInput(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
    //$$     if (CommandCameraCompat.handleFollowInput(player, packet.input().shift())) ci.cancel();
    //$$ }
    //#endif
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CommandCameraPacketTarget")
public abstract class ServerGamePacketListenerImplMixin {}
//#endif
