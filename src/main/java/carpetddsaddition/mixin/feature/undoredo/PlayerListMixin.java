/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.players.PlayerList;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(PlayerList.class)
//$$ public abstract class PlayerListMixin {
//$$     @Inject(method = "remove", at = @At("HEAD"))
//$$     private void dds$clearUndoHistoryOnDisconnect(ServerPlayer player, CallbackInfo ci) { UndoManager.clearPlayer(player); }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoPlayerListTarget")
public abstract class PlayerListMixin {}
//#endif
