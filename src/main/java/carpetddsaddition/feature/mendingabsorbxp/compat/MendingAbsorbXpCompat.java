/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.mendingabsorbxp.compat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
//#if MC >= 12100
//$$ import net.minecraft.core.Holder;
//$$ import net.minecraft.world.item.enchantment.Enchantment;
//#else
import net.minecraft.world.item.enchantment.EnchantmentHelper;
//#endif

/** Minecraft-version adapter for mendingAbsorbXp. */
public final class MendingAbsorbXpCompat {
    private static final float REPAIR_SOUND_VOLUME = 0.25F, REPAIR_SOUND_PITCH = 1.20F;
    private static final float FINISHED_SOUND_VOLUME = 0.45F, FINISHED_SOUND_PITCH = 1.60F;
    private MendingAbsorbXpCompat() {}

    public static boolean isSurvival(ServerPlayer player) {
        //#if MC <= 12104
        return player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
        //#else
        //$$ return player.gameMode() == GameType.SURVIVAL;
        //#endif
    }

    public static boolean hasMending(ItemStack stack) {
        //#if MC >= 12100
        //$$ for (Holder<Enchantment> enchantment : stack.getEnchantments().keySet())
        //$$     if (enchantment.is(Enchantments.MENDING)) return true;
        //$$ return false;
        //#else
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) > 0;
        //#endif
    }

    public static void playFeedback(ServerPlayer player, boolean finished) {
        playSound(player, finished ? FINISHED_SOUND_VOLUME : REPAIR_SOUND_VOLUME,
                finished ? FINISHED_SOUND_PITCH : REPAIR_SOUND_PITCH);
    }

    private static void playSound(ServerPlayer player, float volume, float pitch) {
        //#if MC >= 12000
        //$$ player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
        //$$         SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume, pitch);
        //#elseif MC >= 11902
        //$$ player.level.playSeededSound(null, player.getX(), player.getY(), player.getZ(),
        //$$         SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume, pitch, player.getRandom().nextLong());
        //#elseif MC >= 11601
        //$$ player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
        //$$         SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume, pitch);
        //#else
        player.level.playSound(null, player.position().x, player.position().y, player.position().z,
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, volume, pitch);
        //#endif
    }
}
