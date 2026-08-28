/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.inventory;

import net.minecraft.server.level.ServerPlayer;

/** Optional server menu contract used when a nested feature must safely restore its parent view. */
public interface DdsReopenableMenu {
    boolean ddsCanReopen(ServerPlayer viewer);
    boolean ddsReopen(ServerPlayer viewer);
}
