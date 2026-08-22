/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.mendingabsorbxp;

import carpetddsaddition.feature.mendingabsorbxp.compat.MendingAbsorbXpCompat;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Repairs a damaged Mending offhand item from a Survival player's stored XP. */
public final class MendingAbsorbXp {
    private static final int REPAIR_INTERVAL_TICKS = 5, DURABILITY_PER_XP = 2, FEEDBACK_INTERVAL_TICKS = 50;
    private MendingAbsorbXp() {}
    public static boolean enabled() { return DDSRules.mendingAbsorbXp(); }

    public static void tick(ServerPlayer player) {
        if (!enabled() || !MendingAbsorbXpCompat.isSurvival(player)
                || player.tickCount % REPAIR_INTERVAL_TICKS != 0 || player.totalExperience <= 0) return;
        ItemStack stack = player.getOffhandItem();
        if (!stack.isDamaged() || !MendingAbsorbXpCompat.hasMending(stack)) return;
        player.giveExperiencePoints(-1);
        int newDamage = Math.max(0, stack.getDamageValue() - DURABILITY_PER_XP);
        stack.setDamageValue(newDamage);
        if (newDamage == 0) {
            MendingAbsorbXpCompat.playFeedback(player, true);
            return;
        }
        if (player.tickCount % FEEDBACK_INTERVAL_TICKS == 0) MendingAbsorbXpCompat.playFeedback(player, false);
    }
}
