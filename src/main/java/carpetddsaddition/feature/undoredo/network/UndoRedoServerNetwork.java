/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.network;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.network.DdsPacketRegistry;
//$$
//$$ public final class UndoRedoServerNetwork {
//$$     private UndoRedoServerNetwork() {
//$$     }
//$$
//$$     public static void registerPackets() {
//$$         DdsPacketRegistry.registerC2S(
//$$                 UndoRedoPackets.UNDO_C2S,
//$$                 (player, data) -> UndoManager.undo(player),
//$$                 false,
//$$                 false
//$$         );
//$$
//$$         DdsPacketRegistry.registerC2S(
//$$                 UndoRedoPackets.REDO_C2S,
//$$                 (player, data) -> UndoManager.redo(player),
//$$                 false,
//$$                 false
//$$         );
//$$     }
//$$ }
//#else
public final class UndoRedoServerNetwork {
    private UndoRedoServerNetwork() {
    }

    public static void registerPackets() {
        // Undo/Redo networking starts at Minecraft 1.21.9.
    }
}
//#endif
