/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.network;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11404 && MC <= 260200
import carpetddsaddition.network.DdsClientNetwork;
import carpetddsaddition.network.DdsProtocol;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12002
//$$ import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//#else
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//#endif
//#endif

@Mixin(
        //#if MC >= 12002
        //$$ ClientCommonPacketListenerImpl.class
        //#else
        ClientPacketListener.class
        //#endif
)
public abstract class ClientCustomPayloadMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(
            method =
            //#if MC >= 12002
            //$$ "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V",
            //#else
            "handleCustomPayload(Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;)V",
            //#endif
            at = @At("HEAD"), cancellable = true
    )
    private void dds$handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        //#if MC >= 12002
        //$$ if (!(packet.payload() instanceof DdsProtocol.Payload)) return;
        //$$ DdsClientNetwork.handlePayload((DdsProtocol.Payload) packet.payload());
        //#else
        if (!DdsProtocol.CHANNEL.equals(packet.getIdentifier())) return;
        DdsClientNetwork.handlePayload(new DdsProtocol.Payload(packet.getData()));
        //#endif
        ci.cancel();
    }
    //#endif
}