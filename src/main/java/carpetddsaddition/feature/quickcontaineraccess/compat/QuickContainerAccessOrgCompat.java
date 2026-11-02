/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
import net.minecraft.server.level.ServerPlayer;

/** Optional ownership bridge for Carpet Org Addition remote player menus. */
final class QuickContainerAccessOrgCompat {
    private QuickContainerAccessOrgCompat() {}

    static ServerPlayer owner(Object value) {
        return DdsRemotePlayerOwnerResolver.owner(value);
    }
}
