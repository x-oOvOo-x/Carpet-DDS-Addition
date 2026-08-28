/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import net.minecraft.world.inventory.AbstractContainerMenu;

/** Extra fail-closed guards for QCA sessions whose viewer/owner leaves the server. */
public final class QuickContainerAccessSafety {
    private QuickContainerAccessSafety() {}
    public static void validateOwners(AbstractContainerMenu menu) {
        //#if MC >= 11404 && MC <= 260200
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (session != null && !QuickContainerAccessNavigation.ownersPresent(session)) session.player.closeContainer();
        //#endif
    }
}
