/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.Containers;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.level.Level;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(Containers.class)
//$$ public abstract class ContainersMixin {
//$$     @Inject(method = "dropContents(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;"
//$$             + "Lnet/minecraft/world/Container;)V", at = @At("HEAD"), cancellable = true)
//$$     private static void dds$suppressContainerEntityDropsDuringRestore(Level level, Entity entity,
//$$                                                                         Container container, CallbackInfo ci) {
//$$         if (UndoManager.isRestoring()) ci.cancel();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoContainersTarget")
public abstract class ContainersMixin {}
//#endif
