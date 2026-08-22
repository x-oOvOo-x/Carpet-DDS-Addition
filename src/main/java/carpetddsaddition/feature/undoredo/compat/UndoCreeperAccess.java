/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

public interface UndoCreeperAccess {
    long dds$getUndoIgnitionOriginId();

    void dds$setUndoIgnitionOriginId(long recordId);

    boolean dds$isIgnited();

    void dds$setIgnited(boolean ignited);

    int dds$getOldSwell();

    void dds$setOldSwell(int value);

    int dds$getSwell();

    void dds$setSwell(int value);

    int dds$getSwellDir();

    void dds$setSwellDir(int value);
}
