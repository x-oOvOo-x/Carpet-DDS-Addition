/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoRedoCommandCompat;
//$$ import net.minecraft.commands.CommandBuildContext;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(Commands.class)
//$$ public abstract class CommandsMixin {
//$$     @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;
//$$     @Inject(method = "<init>", at = @At("RETURN"))
//$$     private void dds$registerUndoRedo(Commands.CommandSelection environment,
//$$                                       CommandBuildContext commandBuildContext, CallbackInfo ci) {
//$$         UndoRedoCommandCompat.register(dispatcher);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCommandsTarget")
public abstract class CommandsMixin {}
//#endif
