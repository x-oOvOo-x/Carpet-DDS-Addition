/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
//#if MC >= 11903
//$$ import net.minecraft.core.Holder;
//#endif
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
//#endif

final class QuickContainerAccessSounds {
    private QuickContainerAccessSounds() {}

    //#if MC >= 11404 && MC <= 260200
    static void sendOpen(ServerPlayer player, QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) send(player, SoundEvents.SHULKER_BOX_OPEN, 0.5F, 1.0F);
        else if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) send(player, SoundEvents.ENDER_CHEST_OPEN, 0.5F, 1.0F);
    }
    static void sendClose(ServerPlayer player, QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) send(player, SoundEvents.SHULKER_BOX_CLOSE, 0.5F, 1.0F);
        else if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) send(player, SoundEvents.ENDER_CHEST_CLOSE, 0.5F, 1.0F);
    }
    static void sendAnvilUse(ServerPlayer player) { send(player, SoundEvents.ANVIL_USE, 1.0F, 1.0F); }
    static void sendAnvilDestroy(ServerPlayer player) { send(player, SoundEvents.ANVIL_DESTROY, 1.0F, 1.0F); }

    private static void send(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        //#if MC >= 11903
        //$$ player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.BLOCKS,
        //$$         player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()));
        //#else
        //#if MC >= 11900
        //$$ player.connection.send(new ClientboundSoundPacket(sound, SoundSource.BLOCKS,
        //$$         player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()));
        //#else
        player.connection.send(new ClientboundSoundPacket(sound, SoundSource.BLOCKS,
                //#if MC >= 11502
                player.getX(), player.getY(), player.getZ(),
                //#else
                //$$ player.x, player.y, player.z,
                //#endif
                volume, pitch));
        //#endif
        //#endif
    }
    //#endif
}
