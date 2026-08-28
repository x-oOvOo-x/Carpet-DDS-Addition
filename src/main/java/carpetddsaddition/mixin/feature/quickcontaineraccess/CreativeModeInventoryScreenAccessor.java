/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
//#if MC < 11903
import org.spongepowered.asm.mixin.gen.Accessor;
//#endif

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    //#if MC < 11903
    @Accessor("selectedTab")
    static int carpetDdsAddition$getSelectedTab() { throw new AssertionError(); }
    //#endif
}
