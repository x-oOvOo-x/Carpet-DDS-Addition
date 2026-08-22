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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.network.protocol.game.ServerboundInteractPacket;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.network.ServerGamePacketListenerImpl;
//$$ import net.minecraft.world.entity.Entity;
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
//$$ public abstract class ServerGamePacketListenerEntityMixin {
//$$     @Shadow public ServerPlayer player;
//$$     @Inject(method = "handleInteract(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;)V", at = @At("HEAD"))
//$$     private void dds$beforeEntityInteraction(ServerboundInteractPacket packet, CallbackInfo ci) {
//$$         UndoActionRecorder.beginPlayerAction(player, UndoCause.USE_ENTITY);
//$$         Entity target = dds$resolveTarget(packet);
//$$         if (target != null) UndoMutationRecorder.recordEntityBefore(target);
//$$     }
//$$     @Inject(method = "handleInteract(Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;)V", at = @At("RETURN"))
//$$     private void dds$afterEntityInteraction(ServerboundInteractPacket packet, CallbackInfo ci) {
//$$         Entity target = dds$resolveTarget(packet);
//$$         if (target != null) UndoMutationRecorder.recordEntityAfter(target);
//$$         UndoActionRecorder.endPlayerAction(player);
//$$     }
//$$     private Entity dds$resolveTarget(ServerboundInteractPacket packet) {
//$$         ServerLevel level = (ServerLevel) player.level();
//#if MC >= 260102
//$$         return level.getEntityOrPart(packet.entityId());
//#else
//$$         return packet.getTarget(level);
//#endif
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerGamePacketListenerEntityTarget")
public abstract class ServerGamePacketListenerEntityMixin {}
//#endif
