/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
//#endif

public final class QuickContainerAccessLease {
    private QuickContainerAccessLease() {}
    public static boolean isHeldByOther(
            //#if MC >= 11404 && MC <= 260200
            ServerPlayer player, ItemStack stack
            //#else
            //$$ Object player, Object stack
            //#endif
    ) {
        //#if MC >= 11404 && MC <= 260200
        return QuickContainerAccessSession.isLeasedByOther(player, stack);
        //#else
        //$$ return false;
        //#endif
    }
}
