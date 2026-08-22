/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.UndoCause;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoActionRecorder;
//$$ import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
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
//$$ public abstract class ServerGamePacketListenerPlayerActionMixin {
//$$     @Shadow public ServerPlayer player;
//$$     @Inject(method = "handlePlayerAction(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket;)V",
//$$             at = @At("HEAD"))
//$$     private void dds$beforeReleaseUseItem(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
//$$         if (packet.getAction() != ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM) return;
//$$         UndoActionRecorder.beginPlayerAction(player, UndoCause.USE_ITEM);
//$$     }
//$$     @Inject(method = "handlePlayerAction(Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket;)V",
//$$             at = @At("RETURN"))
//$$     private void dds$afterReleaseUseItem(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
//$$         if (packet.getAction() != ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM) return;
//$$         UndoActionRecorder.endPlayerAction(player);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerGamePacketListenerPlayerActionTarget")
public abstract class ServerGamePacketListenerPlayerActionMixin {}
//#endif