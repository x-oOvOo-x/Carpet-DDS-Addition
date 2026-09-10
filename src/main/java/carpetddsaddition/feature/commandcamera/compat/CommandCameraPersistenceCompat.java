/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera.compat;

import carpetddsaddition.feature.commandcamera.CommandCamera;
import net.minecraft.server.MinecraftServer;

//#if MC >= 11601
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.level.storage.LevelResource;
//#endif

/** Minecraft-version adapter for the world-owned Camera session file. */
public final class CommandCameraPersistenceCompat {
    private CommandCameraPersistenceCompat() {}
    public static void load(MinecraftServer server, String fileName) {
//#if MC >= 11601
//$$     CommandCamera.load(server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(fileName));
//#endif
    }
//#if MC >= 11601
//$$ public static void loadIfNeeded(ServerPlayer player) {
//$$     MinecraftServer server = CommandCameraCompat.levelOf(player).getServer();
//$$     CommandCamera.loadIfNeeded(server.getWorldPath(LevelResource.ROOT).resolve("data")
//$$             .resolve("carpet-dds-addition-camera.properties"));
//$$ }
//#endif
}
