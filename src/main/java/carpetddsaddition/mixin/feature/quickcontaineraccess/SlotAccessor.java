/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {
    //#if MC < 11701
    @Accessor("slot") int carpetDdsAddition$getContainerSlot();
    //#endif
}
