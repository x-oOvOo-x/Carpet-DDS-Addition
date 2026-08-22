/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.network;

//#if MC >= 12109 && MC <= 260200
//$$ import carpetddsaddition.network.DdsClientNetwork;
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

/**
 * Client network slice for undoRedo keyboard shortcuts.
 */
public final class UndoRedoClientNetwork {

    private UndoRedoClientNetwork() {
    }

    public static boolean canUseShortcut() {
        //#if MC >= 12109 && MC <= 260200
        //$$ /*
        //$$  * Preserve the existing behavior: shortcuts are enabled only
        //$$  * when the server advertises BOTH Undo and Redo.
        //$$  */
        //$$ return DdsClientNetwork.canSend(
        //$$         UndoRedoPackets.UNDO_C2S
        //$$ )
        //$$         && DdsClientNetwork.canSend(
        //$$                 UndoRedoPackets.REDO_C2S
        //$$         );
        //#else
        return false;
        //#endif
    }

    public static boolean sendUndo() {
        //#if MC >= 12109 && MC <= 260200
        //$$ if (!canUseShortcut()) {
        //$$     return false;
        //$$ }
        //$$
        //$$ return DdsClientNetwork.send(
        //$$         UndoRedoPackets.UNDO_C2S,
        //$$         new CompoundTag()
        //$$ );
        //#else
        return false;
        //#endif
    }

    public static boolean sendRedo() {
        //#if MC >= 12109 && MC <= 260200
        //$$ if (!canUseShortcut()) {
        //$$     return false;
        //$$ }
        //$$
        //$$ return DdsClientNetwork.send(
        //$$         UndoRedoPackets.REDO_C2S,
        //$$         new CompoundTag()
        //$$ );
        //#else
        return false;
        //#endif
    }
}
