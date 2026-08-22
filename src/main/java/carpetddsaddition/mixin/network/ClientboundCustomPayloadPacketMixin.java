/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
//#if MC >= 12002
//$$ import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
//#else
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
//#endif
//#if MC >= 12006 && MC <= 260200
//$$ import carpetddsaddition.network.DdsProtocol;
//$$ import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.ModifyArg;
//$$ import java.util.ArrayList;
//$$ import java.util.Collections;
//$$ import java.util.List;
//#endif

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class ClientboundCustomPayloadPacketMixin {
    //#if MC >= 12006 && MC <= 260200
    //$$ @ModifyArg(
    //$$         method = "<clinit>",
    //$$         at = @At(value = "INVOKE", target =
    //$$                 "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;codec("
    //$$                 + "Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$FallbackProvider;"
    //$$                 + "Ljava/util/List;)Lnet/minecraft/network/codec/StreamCodec;")
    //$$ )
    //$$ private static List<?> dds$registerClientboundCodec(List<CustomPacketPayload.TypeAndCodec<?, ?>> codecs) {
    //$$     List<CustomPacketPayload.TypeAndCodec<?, ?>> copy = new ArrayList<>(codecs);
    //$$     copy.add(new CustomPacketPayload.TypeAndCodec<>(DdsProtocol.Payload.TYPE, DdsProtocol.Payload.CODEC));
    //$$     return Collections.unmodifiableList(copy);
    //$$ }
    //#endif
}