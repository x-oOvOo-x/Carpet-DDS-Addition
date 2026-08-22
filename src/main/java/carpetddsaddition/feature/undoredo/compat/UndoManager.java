/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

public final class UndoManager {
    private UndoManager() {}

    //#if MC >= 12109
    //$$ public static int undo(ServerPlayer player) { return UndoExecutor.undo(player); }
    //$$ public static int redo(ServerPlayer player) { return UndoExecutor.redo(player); }
    //$$ public static void clearPlayer(ServerPlayer player) { UndoRuntime.clearPlayer(player); }
    //$$ public static boolean isRestoring() { return UndoRuntime.isRestoring(); }
    //#endif

    public static void clearAll() { UndoRuntime.clearAll(); }
}