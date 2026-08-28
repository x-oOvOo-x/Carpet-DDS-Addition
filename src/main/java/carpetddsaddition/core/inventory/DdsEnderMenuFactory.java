/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/** Optional menu context used to preserve specialized Ender Chest UIs through nested QCA opens. */
public interface DdsEnderMenuFactory {
    AbstractContainerMenu ddsCreateEnderMenu(int containerId, Inventory viewerInventory);
}
