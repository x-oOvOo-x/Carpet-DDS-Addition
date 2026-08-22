/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.petregeneration;

import carpetddsaddition.feature.petregeneration.compat.PetRegenerationCompat;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.world.entity.TamableAnimal;

/** Vanilla horse-style regeneration: each tick has a 1/900 chance to heal 1 health point. */
public final class PetRegeneration {
    public static final int HEAL_CHANCE_DENOMINATOR = 900;
    public static final float HEAL_AMOUNT = 1.0F;
    private PetRegeneration() {}

    public static boolean enabled() { return DDSRules.petRegeneration(); }

    public static void tick(Object animal) {
        if (!(animal instanceof TamableAnimal) || !enabled()) return;
        TamableAnimal pet = (TamableAnimal) animal;
        if (PetRegenerationCompat.isClientSide(pet)) return;
        float health = pet.getHealth();
        if (health <= 0.0F || health >= pet.getMaxHealth() || !PetRegenerationCompat.hasOwner(pet)) return;
        if (pet.getRandom().nextInt(HEAL_CHANCE_DENOMINATOR) == 0) pet.heal(HEAL_AMOUNT);
    }
}
