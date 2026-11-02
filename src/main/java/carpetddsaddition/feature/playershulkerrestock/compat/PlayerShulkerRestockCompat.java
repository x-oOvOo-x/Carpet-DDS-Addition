/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.playershulkerrestock.compat;

import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestock;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

/** Keep Minecraft-version details out of the restock policy and transaction. */
public final class PlayerShulkerRestockCompat {
    private PlayerShulkerRestockCompat() {}
    public static boolean supported() {
        //#if MC >= 11404 && MC <= 260300
        return true;
        //#else
        //$$ return false;
        //#endif
    }
    public static PlayerShulkerRestock.Mode mode() {
        //#if MC >= 11404 && MC <= 260300
        String value = DDSRules.autoRestock();
        if ("always".equals(value)) return PlayerShulkerRestock.Mode.ALWAYS;
        if ("client".equals(value)) return PlayerShulkerRestock.Mode.CLIENT;
        //#endif
        return PlayerShulkerRestock.Mode.OFF;
    }
    public static int offhandSlot() {
        //#if MC >= 11701
        //$$ return Inventory.SLOT_OFFHAND;
        //#else
        return 40;
        //#endif
    }
    public static Inventory inventory(ServerPlayer player) {
        //#if MC >= 11701
        //$$ return player.getInventory();
        //#else
        return player.inventory;
        //#endif
    }
    public static int selectedSlot(Inventory inv) {
        //#if MC >= 12105
        //$$ return inv.getSelectedSlot();
        //#else
        return inv.selected;
        //#endif
    }
    public static boolean isSurvival(ServerPlayer player) {
        //#if MC >= 12105
        //$$ return player.gameMode() == GameType.SURVIVAL;
        //#else
        return player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
        //#endif
    }
}
