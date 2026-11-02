/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock;

import carpet.patches.EntityPlayerMPFake;
import carpetddsaddition.feature.playershulkerrestock.compat.PlayerShulkerRestockCompat;
import carpetddsaddition.feature.playershulkerrestock.compat.PlayerShulkerRestockStorage;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/** Restocks only after a server-side vanilla use call demonstrably consumes a stack. */
public final class PlayerShulkerRestock {
    private PlayerShulkerRestock() {}
    public enum Mode { OFF, ALWAYS, CLIENT }
    public static Mode mode() { return PlayerShulkerRestockCompat.mode(); }

    public static int effectiveThreshold(int requested, int maxStackSize) {
        return Math.min(Math.max(requested, 0), Math.max(0, maxStackSize - 1));
    }

    public static boolean shouldRestock(int before, int after, int maxStackSize, int threshold) {
        return maxStackSize > 1 && before > 0 && after >= 0 && after < before
                && after <= effectiveThreshold(threshold, maxStackSize);
    }

    public static Snapshot capture(ServerPlayer player, InteractionHand hand) {
        if (!PlayerShulkerRestockCompat.supported() || !eligible(player)) return null;
        Inventory inv = PlayerShulkerRestockCompat.inventory(player);
        int slot = hand == InteractionHand.MAIN_HAND ? PlayerShulkerRestockCompat.selectedSlot(inv)
                : PlayerShulkerRestockCompat.offhandSlot();
        ItemStack stack = inv.getItem(slot);
        if (stack.isEmpty() || stack.getMaxStackSize() <= 1 || !PlayerShulkerRestockNetwork.allows(player, stack))
            return null;
        return new Snapshot(player, hand, slot, stack.copy(), player.containerMenu);
    }

    /** Synchronous consumption (blocks, rockets); never replace a stack during a held-use animation. */
    public static void completeImmediate(Snapshot before) {
        if (before == null || before.player.isUsingItem()) return;
        Pending observed = observe(before);
        if (observed != null) observed.apply(true);
    }

    /** Food/drinks: at vanilla use completion RETURN, commit before Auto Eat swaps the offhand back. */
    public static void completeHeldUse(Snapshot before) {
        Pending observed = observe(before);
        if (observed != null) observed.apply(false);
    }

    public static boolean shouldCompleteImmediately(boolean stillUsing, boolean itemInteraction) {
        return !stillUsing && itemInteraction;
    }

    private static Pending observe(Snapshot before) {
        if (before == null || !PlayerShulkerRestockCompat.supported() || !eligible(before.player))
            return null;
        Inventory inv = PlayerShulkerRestockCompat.inventory(before.player);
        if (before.player.containerMenu != before.menu || before.menu.containerId != before.menuId)
            return null;
        if (before.hand == InteractionHand.MAIN_HAND && PlayerShulkerRestockCompat.selectedSlot(inv) != before.slot)
            return null;
        ItemStack now = inv.getItem(before.slot);
        if (!now.isEmpty() && !QuickContainerAccessItems.sameItemAndData(before.original, now)) return null;
        int after = now.isEmpty() ? 0 : now.getCount();
        if (!PlayerShulkerRestockNetwork.allows(before.player, before.original)
                || !shouldRestock(before.original.getCount(), after, before.original.getMaxStackSize(),
                        PlayerShulkerRestockNetwork.threshold(before.player))) return null;
        return new Pending(before, after);
    }

    private static final class Pending {
        private final Snapshot snapshot;
        private final int after;
        private Pending(Snapshot snapshot, int after) {
            this.snapshot = snapshot;
            this.after = after;
        }
        private void apply(boolean requireIdle) {
            ServerPlayer player = snapshot.player;
            if (!eligible(player) || requireIdle && player.isUsingItem()
                    || player.containerMenu != snapshot.menu || snapshot.menu.containerId != snapshot.menuId
                    || !PlayerShulkerRestockNetwork.allows(player, snapshot.original)
                    || !shouldRestock(snapshot.original.getCount(), after, snapshot.original.getMaxStackSize(),
                            PlayerShulkerRestockNetwork.threshold(player))) return;
            PlayerShulkerRestockStorage.restock(player, snapshot.slot, snapshot.original, after);
        }
    }

    private static boolean eligible(ServerPlayer player) {
        return player != null && !(player instanceof EntityPlayerMPFake)
                && player.isAlive() && player.connection != null && !player.isSpectator()
                && PlayerShulkerRestockCompat.isSurvival(player)
                && player.containerMenu != null
                && mode() != Mode.OFF;
    }

    public static final class Snapshot {
        private final ServerPlayer player;
        private final InteractionHand hand;
        private final int slot;
        private final ItemStack original;
        private final AbstractContainerMenu menu;
        private final int menuId;
        private Snapshot(ServerPlayer player, InteractionHand hand, int slot, ItemStack original,
                         AbstractContainerMenu menu) {
            this.player = player; this.hand = hand; this.slot = slot; this.original = original;
            this.menu = menu; this.menuId = menu.containerId;
        }
    }
}
