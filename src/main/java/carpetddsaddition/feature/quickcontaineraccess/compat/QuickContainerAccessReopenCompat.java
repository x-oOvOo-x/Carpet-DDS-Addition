/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260300
import carpetddsaddition.core.inventory.DdsReopenableMenu;
import carpetddsaddition.integration.playerinventory.DdsPlayerInventoryReopenCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
//#endif

/** QCA adapter for the shared validated Provider inventory reopener. */
final class QuickContainerAccessReopenCompat {
    private QuickContainerAccessReopenCompat() {}

    //#if MC >= 11404 && MC <= 260300
    static DdsReopenableMenu forInventory(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer owner, Container source) {
        return DdsPlayerInventoryReopenCompat.forInventory(viewer, menu, owner, source);
    }
    //#endif
}
