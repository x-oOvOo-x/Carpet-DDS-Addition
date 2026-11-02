/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops.compat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** Only the version-dependent server-level accessor belongs in compat. */
public final class DirectDropPlayerCompat {
    private DirectDropPlayerCompat() {}

    public static ServerLevel level(ServerPlayer player) {
        //#if MC >= 12106
        //$$ return player.level();
        //#elseif MC >= 12000
        //$$ return player.serverLevel();
        //#else
        return player.getLevel();
        //#endif
    }
}
