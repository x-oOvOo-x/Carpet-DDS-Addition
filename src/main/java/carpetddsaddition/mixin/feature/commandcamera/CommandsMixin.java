/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.commandcamera;

//#if MC >= 11601 && MC <= 260200
//$$ import com.mojang.brigadier.CommandDispatcher;
//$$ import carpetddsaddition.feature.commandcamera.compat.CommandCameraCommandCompat;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.commands.Commands;
//#if MC >= 11902
//$$ import net.minecraft.commands.CommandBuildContext;
//#endif
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

//#if MC >= 11601 && MC <= 260200
//$$ @Mixin(Commands.class)
//$$ public abstract class CommandsMixin {
//$$     @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;
    //#if MC <= 11802
    //$$ @Inject(method = "<init>", at = @At("RETURN"))
    //$$ private void carpetDDSAddition$registerCommandCamera(Commands.CommandSelection environment, CallbackInfo ci) {
    //$$     CommandCameraCommandCompat.register(dispatcher);
    //$$ }
    //#else
    //$$ @Inject(method = "<init>", at = @At("RETURN"))
    //$$ private void carpetDDSAddition$registerCommandCamera(Commands.CommandSelection environment, CommandBuildContext commandBuildContext, CallbackInfo ci) {
    //$$     CommandCameraCommandCompat.register(dispatcher);
    //$$ }
    //#endif
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CommandCameraCommandsTarget")
public abstract class CommandsMixin {}
//#endif
