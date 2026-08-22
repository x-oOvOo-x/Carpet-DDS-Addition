/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import com.mojang.brigadier.ParseResults;
//$$ import carpetddsaddition.feature.undoredo.UndoCause;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoActionRecorder;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(Commands.class)
//$$ public abstract class CommandExecutionMixin {
//$$     @Inject(method = "performCommand", at = @At(value = "INVOKE",
//$$             target = "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V",
//$$             shift = At.Shift.BEFORE))
//$$     private void dds$beforePlayerCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
//$$         if (dds$isHistoryCommand(command)) return;
//$$         if (parseResults.getContext().getSource().getEntity() instanceof ServerPlayer player)
//$$             UndoActionRecorder.beginPlayerAction(player, UndoCause.COMMAND);
//$$     }
//$$
//$$     @Inject(method = "performCommand", at = @At(value = "INVOKE",
//$$             target = "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V",
//$$             shift = At.Shift.AFTER))
//$$     private void dds$afterPlayerCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
//$$         if (dds$isHistoryCommand(command)) return;
//$$         if (parseResults.getContext().getSource().getEntity() instanceof ServerPlayer player)
//$$             UndoActionRecorder.endPlayerAction(player);
//$$     }
//$$
//$$     @Unique
//$$     private static boolean dds$isHistoryCommand(String command) {
//$$         if (command == null) return false;
//$$         String normalized = command.trim();
//$$         if (normalized.startsWith("/")) normalized = normalized.substring(1);
//$$         return normalized.equals("undo") || normalized.equals("redo");
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCommandExecutionTarget")
public abstract class CommandExecutionMixin {}
//#endif