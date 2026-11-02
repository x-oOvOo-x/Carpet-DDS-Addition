/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

/** Position mapping shared by old and modern Minecraft entity APIs. */
public final class DirectDropEntityCompat {
    private DirectDropEntityCompat() {}

    public static BlockPos position(Entity entity) {
        //#if MC >= 11500
        return new BlockPos(
                (int) Math.floor(entity.getX()),
                (int) Math.floor(entity.getY()),
                (int) Math.floor(entity.getZ()));
        //#else
        //$$ return new BlockPos(
        //$$         (int) Math.floor(entity.x),
        //$$         (int) Math.floor(entity.y),
        //$$         (int) Math.floor(entity.z));
        //#endif
    }
}
