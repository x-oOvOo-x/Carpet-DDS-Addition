/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.UndoRedo;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import java.util.function.BooleanSupplier;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(MinecraftServer.class)
//$$ public abstract class MinecraftServerMixin {
//$$     @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
//$$     private void dds$beforeServerTick(BooleanSupplier haveTime, CallbackInfo ci) { UndoRedo.onServerTickStart(); }
//$$     @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At("RETURN"))
//$$     private void dds$afterServerTick(BooleanSupplier haveTime, CallbackInfo ci) { UndoRedo.onServerTickEnd(); }
//$$     @Inject(method = "stopServer", at = @At("HEAD"))
//$$     private void dds$onServerStop(CallbackInfo ci) { UndoRedo.onServerStop(); }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoMinecraftServerTarget")
public abstract class MinecraftServerMixin {}
//#endif