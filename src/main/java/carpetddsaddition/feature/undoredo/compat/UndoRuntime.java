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

final class UndoRuntime {
    private UndoRuntime() {}

    //#if MC >= 12109
    //$$ static void clearPlayer(ServerPlayer player) {
    //$$     PlayerUndoHistory history = UndoRuntimeState.removeHistory(player);
    //$$     if (history != null) history.clear();
    //$$ }
    //$$ static boolean isRestoring() { return UndoRuntimeState.isRestoring(); }
    //#endif

    static void clearAll() {
        //#if MC >= 12109
        //$$ UndoRuntimeState.clearAll();
        //#endif
    }
}