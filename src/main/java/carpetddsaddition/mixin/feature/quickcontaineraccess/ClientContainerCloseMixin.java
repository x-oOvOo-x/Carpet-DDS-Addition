/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessClientNetwork;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientContainerCloseMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(method = "handleContainerClose", at = @At("HEAD"), cancellable = true)
    private void dds$suppressQcaTransitionClose(ClientboundContainerClosePacket packet, CallbackInfo ci) {
        int id = ((ClientboundContainerClosePacketAccessor) (Object) packet).carpetDdsAddition$getContainerId();
        if (QuickContainerAccessClientNetwork.shouldSuppressTransitionClose(id)) ci.cancel();
    }
    //#endif
}
