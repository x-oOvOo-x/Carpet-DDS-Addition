/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.naturalpassivespawning.compat;

import carpetddsaddition.feature.naturalpassivespawning.NaturalPassiveSpawning;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
//#if MC >= 260200
//$$ import net.minecraft.world.entity.EntityTypes;
//#endif

public final class NaturalPassiveSpawningCompat {
    private NaturalPassiveSpawningCompat() {}

    /** True when a whole category can be skipped before spawn-position search. */
    public static boolean shouldSkipCategory(MobCategory category) {
        if (category == MobCategory.CREATURE) return !NaturalPassiveSpawning.land();
        if (category == MobCategory.AMBIENT) return !NaturalPassiveSpawning.bat();
        //#if MC >= 11800
        //$$ if (category == MobCategory.AXOLOTLS) return !NaturalPassiveSpawning.axolotl();
        //#endif
        //#if MC >= 11700
        //$$ if (category == MobCategory.UNDERGROUND_WATER_CREATURE) {
        //#if MC >= 11800
        //$$     return !NaturalPassiveSpawning.squid();
        //#else
        //$$     return !NaturalPassiveSpawning.squid() && !NaturalPassiveSpawning.axolotl();
        //#endif
        //$$ }
        //#endif
        //#if MC >= 11601
        //$$ if (category == MobCategory.WATER_AMBIENT) return !NaturalPassiveSpawning.fish();
        //$$ if (category == MobCategory.WATER_CREATURE)
        //$$     return !NaturalPassiveSpawning.squid() && !NaturalPassiveSpawning.dolphin();
        //#else
        if (category == MobCategory.WATER_CREATURE)
            return !NaturalPassiveSpawning.fish() && !NaturalPassiveSpawning.squid() && !NaturalPassiveSpawning.dolphin();
        //#endif
        return false;
    }

    /** Fine-grained filtering where multiple DDS groups share one vanilla category. */
    public static boolean allowsEntityType(EntityType<?> type) {
        //#if MC >= 260200
        //$$ if (type == EntityTypes.SQUID) return NaturalPassiveSpawning.squid();
        //$$ if (type == EntityTypes.DOLPHIN) return NaturalPassiveSpawning.dolphin();
        //$$ if (type == EntityTypes.COD || type == EntityTypes.SALMON || type == EntityTypes.PUFFERFISH
        //$$         || type == EntityTypes.TROPICAL_FISH) return NaturalPassiveSpawning.fish();
        //$$ if (type == EntityTypes.BAT) return NaturalPassiveSpawning.bat();
        //$$ if (type == EntityTypes.GLOW_SQUID) return NaturalPassiveSpawning.squid();
        //$$ if (type == EntityTypes.AXOLOTL) return NaturalPassiveSpawning.axolotl();
        //#else
        if (type == EntityType.SQUID) return NaturalPassiveSpawning.squid();
        if (type == EntityType.DOLPHIN) return NaturalPassiveSpawning.dolphin();
        if (type == EntityType.COD || type == EntityType.SALMON || type == EntityType.PUFFERFISH
                || type == EntityType.TROPICAL_FISH) return NaturalPassiveSpawning.fish();
        if (type == EntityType.BAT) return NaturalPassiveSpawning.bat();
        //#if MC >= 11700
        //$$ if (type == EntityType.GLOW_SQUID) return NaturalPassiveSpawning.squid();
        //$$ if (type == EntityType.AXOLOTL) return NaturalPassiveSpawning.axolotl();
        //#endif
        //#endif
        return true;
    }
}
