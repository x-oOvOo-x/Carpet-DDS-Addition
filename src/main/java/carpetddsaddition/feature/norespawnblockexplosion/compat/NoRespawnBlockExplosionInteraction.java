/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.norespawnblockexplosion.compat;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
//#if MC >= 11900
//$$ import net.minecraft.network.chat.Component;
//#else
import net.minecraft.network.chat.TextComponent;
//#endif
//#if MC >= 260102
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

/** Cross-version feedback for blocked respawn-block explosions. */
public final class NoRespawnBlockExplosionInteraction {
    private static final String BLOCKED_MESSAGE = "腐竹不希望你这么做 | The server owner doesn't want you to do that";
    private NoRespawnBlockExplosionInteraction() {}

    public static InteractionResult blocked(Level level, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        sendBlockedMessage(player);
        //#if MC >= 12103
        //$$ return InteractionResult.SUCCESS_SERVER;
        //#else
        return InteractionResult.SUCCESS;
        //#endif
    }

    private static void sendBlockedMessage(Player player) {
        //#if MC >= 260102
        //$$ if (player instanceof ServerPlayer) ((ServerPlayer) player).sendOverlayMessage(Component.literal(BLOCKED_MESSAGE));
        //#elseif MC >= 11900
        //$$ player.displayClientMessage(Component.literal(BLOCKED_MESSAGE), true);
        //#else
        player.displayClientMessage(new TextComponent(BLOCKED_MESSAGE), true);
        //#endif
    }
}
