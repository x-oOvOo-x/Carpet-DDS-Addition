/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera.compat;

//#if MC >= 11601 && MC <= 260300
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import net.minecraft.commands.arguments.DimensionArgument;
//$$ import net.minecraft.commands.arguments.EntityArgument;
//$$ import net.minecraft.commands.arguments.coordinates.Vec3Argument;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.phys.Vec3;
//#endif

/**
 * Registers the public DDS Camera commands:
 * /c, /c back, /c tp <player|position>, /c tp dimension <dimension> [position],
 * and /c follow <player>.
 */
public final class CommandCameraCommandCompat {
    private CommandCameraCommandCompat() {
    }

    //#if MC >= 11601 && MC <= 260300
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
    //$$                             Commands.literal("tp")
    //$$                                     .then(
    //$$                                             Commands.literal("dimension")
    //$$                                                     .then(
    //$$                                                             Commands.argument(
    //$$                                                                     "dimension",
    //$$                                                                     DimensionArgument.dimension()
    //$$                                                             )
    //$$                                                                     .executes(context -> {
    //$$                                                                         ServerLevel destination =
    //$$                                                                                 DimensionArgument.getDimension(
    //$$                                                                                         context,
    //$$                                                                                         "dimension"
    //$$                                                                                 );
    //$$                                                                         Vec3 target =
    //$$                                                                                 context.getSource()
    //$$                                                                                         .withLevel(destination)
    //$$                                                                                         .getPosition();
    //$$                                                                         return CommandCameraCompat.tp(
    //$$                                                                                 context.getSource()
    //$$                                                                                         .getPlayerOrException(),
    //$$                                                                                 destination,
    //$$                                                                                 target
    //$$                                                                         );
    //$$                                                                     })
    //$$                                                                     .then(
    //$$                                                                             Commands.argument(
    //$$                                                                                     "pos",
    //$$                                                                                     Vec3Argument.vec3()
    //$$                                                                             )
    //$$                                                                                     .executes(context -> {
    //$$                                                                                         ServerLevel destination =
    //$$                                                                                                 DimensionArgument.getDimension(
    //$$                                                                                                         context,
    //$$                                                                                                         "dimension"
    //$$                                                                                                 );
    //$$                                                                                         Vec3 target =
    //$$                                                                                                 Vec3Argument.getCoordinates(
    //$$                                                                                                         context,
    //$$                                                                                                         "pos"
    //$$                                                                                                 ).getPosition(
    //$$                                                                                                         context.getSource()
    //$$                                                                                                                 .withLevel(destination)
    //$$                                                                                                 );
    //$$                                                                                         return CommandCameraCompat.tp(
    //$$                                                                                                 context.getSource()
    //$$                                                                                                         .getPlayerOrException(),
    //$$                                                                                                 destination,
    //$$                                                                                                 target
    //$$                                                                                         );
    //$$                                                                                     })
    //$$                                                                     )
    //$$                                                     )
    //$$                                     )
    //$$                                     .then(
    //$$                                             Commands.argument(
    //$$                                                     "player",
    //$$                                                     EntityArgument.player()
    //$$                                             )
    //$$                                                     .executes(context ->
    //$$                                                             CommandCameraCompat.tp(
    //$$                                                                     context.getSource()
    //$$                                                                             .getPlayerOrException(),
    //$$                                                                     EntityArgument.getPlayer(
    //$$                                                                             context,
    //$$                                                                             "player"
    //$$                                                                     )
    //$$                                                             ))
    //$$                                     )
    //$$                                     .then(
    //$$                                             Commands.argument("pos", Vec3Argument.vec3())
    //$$                                                     .executes(context ->
    //$$                                                             CommandCameraCompat.tp(
    //$$                                                                     context.getSource()
    //$$                                                                             .getPlayerOrException(),
    //$$                                                                     Vec3Argument.getVec3(
    //$$                                                                             context,
    //$$                                                                             "pos"
    //$$                                                                     )
    //$$                                                             ))
    //$$                                     )
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
