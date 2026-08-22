/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

public final class UndoRedoCommandCompat {
    private UndoRedoCommandCompat() {
    }

    //#if MC >= 12109
    //$$ public static void register(
    //$$         CommandDispatcher<CommandSourceStack> dispatcher
    //$$ ) {
    //$$     dispatcher.register(
    //$$             Commands.literal("undo")
    //$$                     .requires(source -> source.getEntity() instanceof ServerPlayer)
    //$$                     .executes(context -> UndoManager.undo(
    //$$                             context.getSource().getPlayerOrException()))
    //$$     );
    //$$
    //$$     dispatcher.register(
    //$$             Commands.literal("redo")
    //$$                     .requires(source -> source.getEntity() instanceof ServerPlayer)
    //$$                     .executes(context -> UndoManager.redo(
    //$$                             context.getSource().getPlayerOrException()))
    //$$     );
    //$$ }
    //#endif
}
