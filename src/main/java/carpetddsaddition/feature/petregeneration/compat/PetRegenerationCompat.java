/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.petregeneration.compat;

import net.minecraft.world.entity.TamableAnimal;

/** Version-sensitive helpers for petRegeneration. */
public final class PetRegenerationCompat {
    private PetRegenerationCompat() {}
    public static boolean hasOwner(TamableAnimal pet) {
        //#if MC >= 12105
        //$$ return pet.getOwnerReference() != null;
        //#else
        return pet.getOwnerUUID() != null;
        //#endif
    }
    public static boolean isClientSide(TamableAnimal pet) {
        //#if MC >= 12106
        //$$ return pet.level().isClientSide();
        //#else
        return pet.getCommandSenderWorld().isClientSide();
        //#endif
    }
}
