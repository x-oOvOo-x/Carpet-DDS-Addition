/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

public interface UndoSpawnOriginAccess {
    long dds$getUndoSpawnOriginId();

    void dds$setUndoSpawnOriginId(long recordId);
}
