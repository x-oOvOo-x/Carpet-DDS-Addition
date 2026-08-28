/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

/** Optional ownership/canonical-storage metadata for server-backed inventory views. */
public interface DdsInventoryOwnerView {
    ServerPlayer ddsInventoryOwner();
    default Container ddsCanonicalContainer() { return null; }
}
