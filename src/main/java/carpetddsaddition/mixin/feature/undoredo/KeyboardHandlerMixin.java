/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.network.UndoRedoClientNetwork;
//$$ import net.minecraft.client.KeyboardHandler;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.input.KeyEvent;
//$$ import org.lwjgl.glfw.GLFW;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(KeyboardHandler.class)
//$$ public abstract class KeyboardHandlerMixin {
//$$     @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"), cancellable = true)
//$$     private void dds$undoRedoShortcut(long window, int action, KeyEvent event, CallbackInfo ci) {
//$$         if (action != GLFW.GLFW_PRESS || event.key() != GLFW.GLFW_KEY_Z
//$$                 || !event.hasControlDown() || event.hasShiftDown()) return;
//$$         Minecraft minecraft = Minecraft.getInstance();
//$$         if (minecraft.player == null || !minecraft.player.isCreative() || minecraft.getConnection() == null) return;
    //#if MC >= 260200
    //$$     if (minecraft.gui.screen() != null) return;
    //#else
    //$$     if (minecraft.screen != null) return;
    //#endif
//$$         boolean sent = event.hasAltDown() ? UndoRedoClientNetwork.sendRedo() : UndoRedoClientNetwork.sendUndo();
//$$         if (sent) ci.cancel();
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoKeyboardTarget")
public abstract class KeyboardHandlerMixin {}
//#endif
