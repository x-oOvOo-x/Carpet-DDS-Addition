/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

public interface UndoOriginAccess {
    long dds$getUndoOriginId();

    void dds$setUndoOriginId(long recordId);
}
