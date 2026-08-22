/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoActionRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContainerCompat;
//$$ import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
//$$ import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.network.ServerGamePacketListenerImpl;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ServerGamePacketListenerImpl.class)
//$$ public abstract class ServerGamePacketListenerContainerMixin {
//$$     @Shadow public ServerPlayer player;
//$$     @Inject(method = "handleContainerClick(Lnet/minecraft/network/protocol/game/ServerboundContainerClickPacket;)V",
//$$             at = @At(value = "INVOKE",
//#if MC >= 260102
//$$                     target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
//$$                             + "Lnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V",
//#else
//$$                     target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
//$$                             + "Lnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
//#endif
//$$                     shift = At.Shift.BEFORE))
//$$     private void dds$beforeContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
//$$         UndoActionRecorder.beginContainerAction(player);
//$$         UndoContainerCompat.captureMenuContainersBefore(player);
//$$     }
//$$
//$$     @Inject(method = "handleContainerClick(Lnet/minecraft/network/protocol/game/ServerboundContainerClickPacket;)V",
//$$             at = @At(value = "INVOKE",
//#if MC >= 260102
//$$                     target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
//$$                             + "Lnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V",
//#else
//$$                     target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
//$$                             + "Lnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
//#endif
//$$                     shift = At.Shift.AFTER))
//$$     private void dds$afterContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
//$$         UndoContainerCompat.captureMenuContainersAfter(player);
//$$         UndoActionRecorder.endContainerAction(player);
//$$     }
//$$
//$$     @Inject(method = "handleContainerClose(Lnet/minecraft/network/protocol/game/ServerboundContainerClosePacket;)V",
//$$             at = @At("RETURN"))
//$$     private void dds$afterContainerClose(ServerboundContainerClosePacket packet, CallbackInfo ci) {
//$$         UndoActionRecorder.finishPendingContainerAction(player);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerGamePacketListenerContainerTarget")
public abstract class ServerGamePacketListenerContainerMixin {}
//#endif