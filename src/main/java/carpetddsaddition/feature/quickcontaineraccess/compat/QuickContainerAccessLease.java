/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260300
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
//#endif

public final class QuickContainerAccessLease {
    private QuickContainerAccessLease() {}
    public static boolean isHeldByAnyone(
            //#if MC >= 11404 && MC <= 260300
            ItemStack stack
            //#else
            //$$ Object stack
            //#endif
    ) {
        //#if MC >= 11404 && MC <= 260300
        return QuickContainerAccessSession.isLeasedAnywhere(stack);
        //#else
        //$$ return false;
        //#endif
    }
    public static int shulkerCapacity() {
        //#if MC >= 11404 && MC <= 260300
        return QuickContainerAccessStorage.shulkerSize();
        //#else
        //$$ return 27;
        //#endif
    }
    public static boolean isHeldByOther(
            //#if MC >= 11404 && MC <= 260300
            ServerPlayer player, ItemStack stack
            //#else
            //$$ Object player, Object stack
            //#endif
    ) {
        //#if MC >= 11404 && MC <= 260300
        return QuickContainerAccessSession.isLeasedByOther(player, stack);
        //#else
        //$$ return false;
        //#endif
    }
}
