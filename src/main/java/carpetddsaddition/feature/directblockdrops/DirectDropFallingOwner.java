/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** Non-persisted provenance on a falling scaffolding entity. */
public interface DirectDropFallingOwner {
    void dds$setDirectDropOwner(ServerPlayer player, ServerLevel level, long startTick, long generation);
    ServerPlayer dds$getDirectDropPlayer();
    ServerLevel dds$getDirectDropLevel();
    long dds$getDirectDropStartTick();
    long dds$getDirectDropGeneration();
}
