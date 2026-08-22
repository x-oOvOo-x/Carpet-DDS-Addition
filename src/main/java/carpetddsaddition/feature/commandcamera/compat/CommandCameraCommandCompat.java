/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera.compat;

//#if MC >= 11601 && MC <= 260200
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import net.minecraft.commands.arguments.EntityArgument;
//$$ import net.minecraft.server.level.ServerPlayer;
//#endif

/**
 * Registers the three public DDS Camera commands:
 * /c, /c back and /c follow <player>.
 */
public final class CommandCameraCommandCompat {
    private CommandCameraCommandCompat() {
    }

    //#if MC >= 11601 && MC <= 260200
    //$$ public static void register(
    //$$         CommandDispatcher<CommandSourceStack> dispatcher
    //$$ ) {
    //$$     dispatcher.register(
    //$$             Commands.literal("c")
    //$$                     .requires(source ->
    //$$                             source.getEntity()
    //$$                                     instanceof ServerPlayer)
    //$$                     .executes(context ->
    //$$                             CommandCameraCompat.toggle(
    //$$                                     context.getSource()
    //$$                                             .getPlayerOrException()
    //$$                             ))
    //$$                     .then(
    //$$                             Commands.literal("back")
    //$$                                     .executes(context ->
    //$$                                             CommandCameraCompat.back(
    //$$                                                     context.getSource()
    //$$                                                             .getPlayerOrException()
    //$$                                             ))
    //$$                     )
    //$$                     .then(
    //$$                             Commands.literal("follow")
    //$$                                     .then(
    //$$                                             Commands.argument(
    //$$                                                     "player",
    //$$                                                     EntityArgument.player()
    //$$                                             )
    //$$                                                     .executes(context -> {
    //$$                                                         ServerPlayer actor =
    //$$                                                                 context
    //$$                                                                         .getSource()
    //$$                                                                         .getPlayerOrException();
    //$$                                                         ServerPlayer target =
    //$$                                                                 EntityArgument.getPlayer(
    //$$                                                                         context,
    //$$                                                                         "player"
    //$$                                                                 );
    //$$                                                         return CommandCameraCompat.follow(
    //$$                                                                 actor,
    //$$                                                                 target
    //$$                                                         );
    //$$                                                     })
    //$$                                     )
    //$$                     )
    //$$     );
    //$$ }
    //#endif
}
