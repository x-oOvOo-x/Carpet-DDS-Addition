/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

public interface UndoHopperAccess extends UndoOriginAccess {
    long IDLE_GRACE_TICKS = 16L;

    long dds$getUndoTransferDeadline();

    void dds$setUndoTransferDeadline(long gameTime);
}
