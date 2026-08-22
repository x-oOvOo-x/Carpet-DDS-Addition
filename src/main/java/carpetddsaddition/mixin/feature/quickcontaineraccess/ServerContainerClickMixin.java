/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#if MC >= 11404 && MC <= 260200
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
//#endif

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerContainerClickMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
    private void dds$handleQuickContainerClick(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        if (!QuickContainerAccessCompat.shouldInspectContainerClickPackets()) return;
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = listener.player;
        PacketUtils.ensureRunningOnSameThread(packet, listener,
                //#if MC >= 12001
                //$$ (ServerLevel) player.serverLevel()
                //#else
                (ServerLevel) player.level
                //#endif
        );
        AbstractContainerMenu menu = player.containerMenu;
        //#if MC >= 12105
        //$$ int packetContainerId = packet.containerId();
        //$$ int packetSlotNum = packet.slotNum();
        //$$ int packetButtonNum = packet.buttonNum();
        //#if MC >= 260102
        //$$ Object packetClickType = packet.containerInput();
        //#else
        //$$ Object packetClickType = packet.clickType();
        //#endif
        //#else
        int packetContainerId = packet.getContainerId();
        int packetSlotNum = packet.getSlotNum();
        int packetButtonNum = packet.getButtonNum();
        Object packetClickType = packet.getClickType();
        //#endif
        if (menu.containerId != packetContainerId || player.isSpectator()) return;
        if (!QuickContainerAccessCompat.handleInventoryClick(
                menu, packetSlotNum, packetButtonNum, packetClickType, player)) return;
        player.resetLastActionTime();
        //#if MC >= 11701
        //$$ player.containerMenu.sendAllDataToRemote();
        //#else
        player.refreshContainer(player.containerMenu);
        player.broadcastCarriedItem();
        //#endif
        ci.cancel();
    }
    //#endif
}