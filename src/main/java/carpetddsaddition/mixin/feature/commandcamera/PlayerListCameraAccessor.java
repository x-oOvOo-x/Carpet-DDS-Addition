/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.commandcamera;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11601 && MC <= 260200
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.players.PlayerList;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 11601 && MC <= 260200
//$$ @Mixin(PlayerList.class)
//$$ public interface PlayerListCameraAccessor {
//$$     @Invoker("save") void carpetDDSAddition$savePlayer(ServerPlayer player);
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CommandCameraPlayerListTarget")
public interface PlayerListCameraAccessor {}
//#endif
