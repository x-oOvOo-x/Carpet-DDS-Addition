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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Mixin(ClientPacketListener.class)
public abstract class ClientHandshakeMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void dds$startHandshake(CallbackInfo ci) { DdsClientNetwork.onConnectedToServer(); }
    //#endif
}
