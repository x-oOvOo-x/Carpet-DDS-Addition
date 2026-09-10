/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.naturalpassivespawning.compat;

import carpetddsaddition.feature.naturalpassivespawning.NaturalPassiveSpawning;
import carpetddsaddition.feature.naturalpassivespawning.NaturalPassiveSpawning.Group;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
//#if MC >= 260200
//$$ import net.minecraft.world.entity.EntityTypes;
//#endif

public final class NaturalPassiveSpawningCompat {
    private NaturalPassiveSpawningCompat() {}

    /** True when every DDS group represented by this vanilla category is blocked. */
    public static boolean shouldSkipCategory(MobCategory category) {
        if (category == MobCategory.CREATURE) return NaturalPassiveSpawning.prevents(Group.LAND);
        if (category == MobCategory.AMBIENT) return NaturalPassiveSpawning.prevents(Group.BAT);
        //#if MC >= 11800
        //$$ if (category == MobCategory.AXOLOTLS) return NaturalPassiveSpawning.prevents(Group.AXOLOTL);
        //#endif
        //#if MC >= 11700
        //$$ if (category == MobCategory.UNDERGROUND_WATER_CREATURE) {
        //#if MC >= 11800
        //$$     return NaturalPassiveSpawning.prevents(Group.SQUID);
        //#else
        //$$     return NaturalPassiveSpawning.prevents(Group.SQUID) && NaturalPassiveSpawning.prevents(Group.AXOLOTL);
        //#endif
        //$$ }
        //#endif
        //#if MC >= 11601
        //$$ if (category == MobCategory.WATER_AMBIENT) return NaturalPassiveSpawning.prevents(Group.FISH);
        //$$ if (category == MobCategory.WATER_CREATURE)
        //$$     return NaturalPassiveSpawning.prevents(Group.SQUID) && NaturalPassiveSpawning.prevents(Group.DOLPHIN);
        //#else
        if (category == MobCategory.WATER_CREATURE)
            return NaturalPassiveSpawning.prevents(Group.FISH) && NaturalPassiveSpawning.prevents(Group.SQUID)
                    && NaturalPassiveSpawning.prevents(Group.DOLPHIN);
        //#endif
        return false;
    }

    /** Fine-grained filtering where multiple DDS groups share one vanilla category. */
    public static boolean allowsEntityType(EntityType<?> type) {
        //#if MC >= 260200
        //$$ if (type == EntityTypes.SQUID) return !NaturalPassiveSpawning.prevents(Group.SQUID);
        //$$ if (type == EntityTypes.DOLPHIN) return !NaturalPassiveSpawning.prevents(Group.DOLPHIN);
        //$$ if (type == EntityTypes.COD || type == EntityTypes.SALMON || type == EntityTypes.PUFFERFISH
        //$$         || type == EntityTypes.TROPICAL_FISH) return !NaturalPassiveSpawning.prevents(Group.FISH);
        //$$ if (type == EntityTypes.BAT) return !NaturalPassiveSpawning.prevents(Group.BAT);
        //$$ if (type == EntityTypes.GLOW_SQUID) return !NaturalPassiveSpawning.prevents(Group.SQUID);
        //$$ if (type == EntityTypes.AXOLOTL) return !NaturalPassiveSpawning.prevents(Group.AXOLOTL);
        //#else
        if (type == EntityType.SQUID) return !NaturalPassiveSpawning.prevents(Group.SQUID);
        if (type == EntityType.DOLPHIN) return !NaturalPassiveSpawning.prevents(Group.DOLPHIN);
        if (type == EntityType.COD || type == EntityType.SALMON || type == EntityType.PUFFERFISH
                || type == EntityType.TROPICAL_FISH) return !NaturalPassiveSpawning.prevents(Group.FISH);
        if (type == EntityType.BAT) return !NaturalPassiveSpawning.prevents(Group.BAT);
        //#if MC >= 11700
        //$$ if (type == EntityType.GLOW_SQUID) return !NaturalPassiveSpawning.prevents(Group.SQUID);
        //$$ if (type == EntityType.AXOLOTL) return !NaturalPassiveSpawning.prevents(Group.AXOLOTL);
        //#endif
        //#endif
        return true;
    }
}
