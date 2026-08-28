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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Repairs a damaged Mending offhand item from a Survival player's stored XP. */
public final class MendingAbsorbXp {
    private static final int REPAIR_INTERVAL_TICKS = 10, DURABILITY_PER_XP = 2, FEEDBACK_INTERVAL_TICKS = 50;
    private static final Map<UUID, PendingRepair> PENDING = new HashMap<>();
    private MendingAbsorbXp() {}
    public static boolean enabled() { return DDSRules.mendingAbsorbXp(); }
    public static void resetRuntimeState() { PENDING.clear(); }

    public static void tick(ServerPlayer player) {
        UUID id = player.getUUID();
        PendingRepair pending = PENDING.get(id);
        ItemStack stack = player.getOffhandItem();
        if (pending != null && pending.owner != player) {
            PENDING.remove(id);
            pending = null;
        }
        if (!player.isAlive()) {
            settleAfterDeath(id, pending, stack);
            return;
        }
        if (!enabled() || !MendingAbsorbXpCompat.isSurvival(player)) {
            flushOrRefund(player, id, pending, stack);
            return;
        }
        if (pending != null && pending.stack != stack) {
            refund(player, id, pending.xp);
            pending = null;
        }
        if (!repairable(stack)) {
            if (pending != null) refund(player, id, pending.xp);
            return;
        }
        if (player.isShiftKeyDown()) {
            int credited = pending == null ? 0 : pending.xp, required = requiredXp(stack);
            int extra = Math.min(player.totalExperience, Math.max(0, required - credited));
            if (extra > 0) player.giveExperiencePoints(-extra);
            PENDING.remove(id);
            repair(player, stack, credited + extra, true);
            return;
        }
        if (player.totalExperience <= 0) {
            if (pending != null) {
                PENDING.remove(id);
                repair(player, stack, pending.xp, false);
            }
            return;
        }
        if (pending == null) {
            pending = new PendingRepair(player, stack);
            PENDING.put(id, pending);
        }
        int required = requiredXp(stack);
        int xp = Math.min(Math.min(player.totalExperience, (player.tickCount & 1) == 0 ? 2 : 3),
                Math.max(0, required - pending.xp));
        if (xp > 0) {
            player.giveExperiencePoints(-xp);
            pending.xp += xp;
            if (pending.xp < required && pending.feedbackTicks++ % FEEDBACK_INTERVAL_TICKS == 0)
                MendingAbsorbXpCompat.playFeedback(player, false);
        }
        if (++pending.ticks >= REPAIR_INTERVAL_TICKS || pending.xp >= required || player.totalExperience <= 0) {
            boolean finished = repair(player, stack, pending.xp, false);
            pending.xp = pending.ticks = 0;
            if (finished || player.totalExperience <= 0) PENDING.remove(id);
        }
    }

    public static void onDeath(ServerPlayer player) {
        UUID id = player.getUUID();
        PendingRepair pending = PENDING.get(id);
        if (pending != null && pending.owner == player) settleBeforeDeath(player, id, pending, player.getOffhandItem());
    }

    public static void onDisconnect(ServerPlayer player) {
        UUID id = player.getUUID();
        PendingRepair pending = PENDING.get(id);
        if (pending == null || pending.owner != player) return;
        if (!player.isAlive()) settleAfterDeath(id, pending, player.getOffhandItem());
        else flushOrRefund(player, id, pending, player.getOffhandItem());
    }

    private static boolean repairable(ItemStack stack) {
        return stack.isDamaged() && MendingAbsorbXpCompat.hasMending(stack);
    }

    private static int requiredXp(ItemStack stack) {
        return (stack.getDamageValue() + DURABILITY_PER_XP - 1) / DURABILITY_PER_XP;
    }

    private static void refund(ServerPlayer player, UUID id, int xp) {
        PENDING.remove(id);
        if (xp > 0) player.giveExperiencePoints(xp);
    }

    private static void flushOrRefund(ServerPlayer player, UUID id, PendingRepair pending, ItemStack stack) {
        if (pending == null) return;
        PENDING.remove(id);
        if (pending.stack == stack && repairable(stack)) repair(player, stack, pending.xp, false);
        else if (pending.xp > 0) player.giveExperiencePoints(pending.xp);
    }

    private static void settleBeforeDeath(ServerPlayer player, UUID id, PendingRepair pending, ItemStack stack) {
        PENDING.remove(id);
        if (pending.xp <= 0) return;
        if (pending.stack != stack || !repairable(stack)) {
            player.giveExperiencePoints(pending.xp);
            return;
        }
        int used = Math.min(pending.xp, requiredXp(stack));
        if (used > 0) stack.setDamageValue(Math.max(0, stack.getDamageValue() - used * DURABILITY_PER_XP));
        if (pending.xp > used) player.giveExperiencePoints(pending.xp - used);
    }

    private static void settleAfterDeath(UUID id, PendingRepair pending, ItemStack stack) {
        if (pending == null) return;
        PENDING.remove(id);
        if (pending.stack != stack || !repairable(stack) || pending.xp <= 0) return;
        int used = Math.min(pending.xp, requiredXp(stack));
        stack.setDamageValue(Math.max(0, stack.getDamageValue() - used * DURABILITY_PER_XP));
    }

    private static boolean repair(ServerPlayer player, ItemStack stack, int xp, boolean instant) {
        if (xp <= 0) return !stack.isDamaged();
        int used = Math.min(xp, requiredXp(stack));
        if (xp > used) player.giveExperiencePoints(xp - used);
        if (used <= 0) return !stack.isDamaged();
        int newDamage = Math.max(0, stack.getDamageValue() - used * DURABILITY_PER_XP);
        stack.setDamageValue(newDamage);
        if (newDamage == 0 || instant) MendingAbsorbXpCompat.playFeedback(player, newDamage == 0);
        return newDamage == 0;
    }

    private static final class PendingRepair {
        private final ServerPlayer owner;
        private final ItemStack stack;
        private int xp, ticks, feedbackTicks;
        private PendingRepair(ServerPlayer owner, ItemStack stack) { this.owner = owner; this.stack = stack; }
    }
}
