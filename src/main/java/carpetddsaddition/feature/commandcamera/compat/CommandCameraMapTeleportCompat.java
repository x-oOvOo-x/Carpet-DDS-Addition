/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera.compat;

//#if MC >= 11601 && MC <= 260200
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.util.Mth;
//$$ import net.minecraft.world.level.GameType;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.phys.Vec3;
//#endif

/** Handles map-mod self teleports for active DDS Camera sessions without loading/generating chunks. */
public final class CommandCameraMapTeleportCompat {
    private CommandCameraMapTeleportCompat() {}

    //#if MC >= 11601 && MC <= 260200
    //$$ public static boolean tryHandle(ServerPlayer player, String rawCommand) {
    //$$     if (!CommandCameraCompat.isCamera(player)) return false;
    //$$     Request request = parse(rawCommand);
    //$$     if (request == null) return false;
    //$$     if (!CommandCameraCompat.isEnabled()) {
    //$$         CommandCameraCompat.actionBar(player, "commandCamera 已关闭，无法进行 Camera 传送");
    //$$         return true;
    //$$     }
    //$$     if (CommandCameraCompat.gameModeOf(player) != GameType.SPECTATOR) {
    //$$         CommandCameraCompat.actionBar(player, "Camera 状态异常，请使用 /c 恢复");
    //$$         return true;
    //$$     }
    //$$     ServerLevel destination = request.dimension == null ? CommandCameraCompat.levelOf(player)
    //$$             : CommandCameraCompat.findLevel(player, request.dimension);
    //$$     if (destination == null) {
    //$$         CommandCameraCompat.actionBar(player, "目标维度不存在，无法传送");
    //$$         return true;
    //$$     }
    //$$     BlockPos targetPos = new BlockPos(Mth.floor(request.x), Mth.floor(request.y), Mth.floor(request.z));
    //$$     if (!Level.isInSpawnableBounds(targetPos)) {
    //$$         CommandCameraCompat.actionBar(player, "目标位置超出有效范围，无法传送");
    //$$         return true;
    //$$     }
    //$$     int chunkX = targetPos.getX() >> 4, chunkZ = targetPos.getZ() >> 4;
    //$$     if (!destination.getChunkSource().hasChunk(chunkX, chunkZ)) {
    //$$         CommandCameraCompat.actionBar(player, "目标区块未加载，无法传送");
    //$$         return true;
    //$$     }
    //$$     CommandCameraCompat.stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     if (!CommandCameraCompat.teleportPlayer(player, destination, request.x, request.y, request.z,
    //$$             CommandCameraCompat.yawOf(player), CommandCameraCompat.pitchOf(player))) {
    //$$         CommandCameraCompat.actionBar(player, "无法传送到目标位置");
    //$$         return true;
    //$$     }
    //$$     player.setDeltaMovement(Vec3.ZERO);
    //$$     return true;
    //$$ }
    //$$
    //$$ private static Request parse(String rawCommand) {
    //$$     if (rawCommand == null) return null;
    //$$     String command = rawCommand.trim();
    //$$     if (command.startsWith("/")) command = command.substring(1).trim();
    //$$     if (command.isEmpty()) return null;
    //$$     String[] parts = command.split("\\s+");
    //$$     Request direct = parseTeleportTail(parts, 0, null);
    //$$     if (direct != null) return direct;
    //$$     return parts.length >= 8 && "execute".equals(parts[0]) && "in".equals(parts[1]) && "run".equals(parts[3])
    //$$             ? parseTeleportTail(parts, 4, parts[2]) : null;
    //$$ }
    //$$ private static Request parseTeleportTail(String[] parts, int offset, String dimension) {
    //$$     int remaining = parts.length - offset;
    //$$     if (remaining != 4 && remaining != 5) return null;
    //$$     String verb = parts[offset];
    //$$     if (!"tp".equals(verb) && !"teleport".equals(verb)) return null;
    //$$     int coordinateOffset = offset + 1;
    //$$     if (remaining == 5) {
    //$$         if (!"@s".equals(parts[offset + 1])) return null;
    //$$         coordinateOffset++;
    //$$     }
    //$$     Double x = parseAbsolute(parts[coordinateOffset]), y = parseAbsolute(parts[coordinateOffset + 1]),
    //$$             z = parseAbsolute(parts[coordinateOffset + 2]);
    //$$     return x == null || y == null || z == null ? null : new Request(dimension, x, y, z);
    //$$ }
    //$$ private static Double parseAbsolute(String token) {
    //$$     if (token.indexOf('~') >= 0 || token.indexOf('^') >= 0) return null;
    //$$     try {
    //$$         double value = Double.parseDouble(token);
    //$$         return Double.isFinite(value) ? value : null;
    //$$     } catch (NumberFormatException ignored) { return null; }
    //$$ }
    //$$ private static final class Request {
    //$$     private final String dimension;
    //$$     private final double x, y, z;
    //$$     private Request(String dimension, double x, double y, double z) {
    //$$         this.dimension = dimension;
    //$$         this.x = x;
    //$$         this.y = y;
    //$$         this.z = z;
    //$$     }
    //$$ }
    //#endif
}
