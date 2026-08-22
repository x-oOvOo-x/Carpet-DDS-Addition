/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.network;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12002 && MC < 12005
//$$ import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//#endif
//#if MC <= 11605
import carpetddsaddition.network.ServerboundCustomPayloadPacketAccess;
//#endif
//#if MC >= 11404 && MC <= 260200
import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.network.DdsProtocol;
import carpetddsaddition.network.DdsServerNetwork;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
//#else
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
//#endif
//#endif

@Mixin(
        //#if MC >= 12002 && MC < 12005
        //$$ ServerCommonPacketListenerImpl.class
        //#else
        ServerGamePacketListenerImpl.class
        //#endif
)
public abstract class ServerCustomPayloadMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(
            method =
            //#if MC >= 12002
            //$$ "handleCustomPayload(Lnet/minecraft/network/protocol/common/ServerboundCustomPayloadPacket;)V",
            //#else
            "handleCustomPayload(Lnet/minecraft/network/protocol/game/ServerboundCustomPayloadPacket;)V",
            //#endif
            at = @At("HEAD"), cancellable = true
    )
    private void dds$handleCustomPayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        //#if MC >= 12002
        //$$ if (!(packet.payload() instanceof DdsProtocol.Payload)) return;
        //$$ DdsProtocol.Payload payload = (DdsProtocol.Payload) packet.payload();
        //#else
        //#if MC >= 11701
        //$$ if (!DdsProtocol.CHANNEL.equals(packet.getIdentifier())) return;
        //$$ DdsProtocol.Payload payload = new DdsProtocol.Payload(packet.getData());
        //#else
        ServerboundCustomPayloadPacketAccess accessor = (ServerboundCustomPayloadPacketAccess) (Object) packet;
        if (!DdsProtocol.CHANNEL.equals(accessor.dds$getIdentifier())) return;
        DdsProtocol.Payload payload = new DdsProtocol.Payload(accessor.dds$getData());
        //#endif
        //#endif

        //#if MC >= 12002 && MC < 12005
        //$$ Object self = this;
        //$$ if (!(self instanceof ServerGamePacketListenerImpl)) return;
        //$$ ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) self;
        //#else
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        //#endif

        ci.cancel();
        if (DDSRules.ddsNetworkProtocol()) DdsServerNetwork.handlePayload(listener, payload);
        else DdsServerNetwork.handlePayloadWhileDisabled(listener, payload);
    }
    //#endif
}