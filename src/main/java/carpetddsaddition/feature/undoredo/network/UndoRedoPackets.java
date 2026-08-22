/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.network;

/**
 * Stable DDS Protocol v1 packet ids owned by undoRedo.
 */
public final class UndoRedoPackets {

    public static final String UNDO_C2S =
            "undo_c2s";

    public static final String REDO_C2S =
            "redo_c2s";

    private UndoRedoPackets() {
    }
}
