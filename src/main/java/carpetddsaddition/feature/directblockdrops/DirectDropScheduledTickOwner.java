/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.server.level.ServerPlayer;

/** Runtime-only provenance attached to an accepted scheduled block tick. */
public interface DirectDropScheduledTickOwner {
    ServerPlayer dds$getDirectDropPlayer();
    long dds$getDirectDropStartTick();
    long dds$getDirectDropGeneration();
    void dds$setDirectDropOwner(ServerPlayer player, long startTick, long generation);

    /** Keep the original attribution when Undo replaces a scheduled tick. */
    static void copy(Object source, Object destination) {
        if (!(source instanceof DirectDropScheduledTickOwner)
                || !(destination instanceof DirectDropScheduledTickOwner)) return;
        DirectDropScheduledTickOwner from = (DirectDropScheduledTickOwner) source;
        ((DirectDropScheduledTickOwner) destination).dds$setDirectDropOwner(
                from.dds$getDirectDropPlayer(), from.dds$getDirectDropStartTick(),
                from.dds$getDirectDropGeneration());
    }
}
