/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.commandcamera.compat;

//#if MC >= 11601 && MC <= 260200
//$$ import carpetddsaddition.generated.DDSRules;
//$$ import carpetddsaddition.feature.commandcamera.CameraState;
//$$ import carpetddsaddition.feature.commandcamera.CommandCamera;
//$$ import net.minecraft.ChatFormatting;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.damagesource.DamageSource;
//#if MC >= 11904
//$$ import net.minecraft.world.damagesource.DamageTypes;
//#endif
//$$ import net.minecraft.world.level.GameType;
//$$ import net.minecraft.world.phys.Vec3;
//#if MC <= 11802
//$$ import net.minecraft.network.chat.TextComponent;
//#endif
//$$ import java.util.Collections;
//$$ import java.util.UUID;
//#endif

/** Minecraft-version adapter for the DDS safe Camera session. */
public final class CommandCameraCompat {
    //#if MC >= 11601 && MC <= 260200
    //$$ private static final int FOLLOW_ATTACH_DELAY_TICKS = 2;
    //$$ private static final double TELEPORT_EPSILON_SQUARED = 1.0E-6D;
    //#endif
    private CommandCameraCompat() {}

    //#if MC >= 11601 && MC <= 260200
    //$$ public static boolean isEnabled() { return DDSRules.commandCamera(); }
    //$$ public static boolean isCamera(ServerPlayer player) { return CommandCamera.isCamera(player.getUUID()); }
    //$$
    //$$ /** Cancels vanilla out-of-world damage; failed restore preserves the session for retry. */
    //$$ public static boolean handleVoidDamage(ServerPlayer player, DamageSource source) {
    //$$     if (!isCamera(player) || !isVoidDamage(source)) return false;
    //$$     if (back(player) != 0) actionBar(player, "已自动返回 Camera 原点 | Returned to camera origin");
    //$$     return true;
    //$$ }
    //$$ public static int toggle(ServerPlayer player) {
    //$$     if (isCamera(player)) return restore(player, true, true) ? 1 : 0;
    //$$     if (!isEnabled()) {
    //$$         actionBar(player, "commandCamera 规则未开启");
    //$$         return 0;
    //$$     }
    //$$     return enter(player, true) ? 1 : 0;
    //$$ }
    //$$ public static int back(ServerPlayer player) {
    //$$     if (!isCamera(player)) {
    //$$         actionBar(player, "当前不在 DDS Camera 模式");
    //$$         return 0;
    //$$     }
    //$$     if (gameModeOf(player) != GameType.SPECTATOR) {
    //$$         actionBar(player, "Camera 状态异常，请使用 /c 恢复");
    //$$         return 0;
    //$$     }
    //$$     stopFollowing(player, false);
    //$$     return restore(player, false, true) ? 1 : 0;
    //$$ }
    //$$ public static int follow(ServerPlayer player, ServerPlayer target) {
    //$$     if (target == player) {
    //$$         actionBar(player, "不能跟随自己");
    //$$         return 0;
    //$$     }
    //$$     if (!target.isAlive()) {
    //$$         actionBar(player, "目标玩家当前不可跟随");
    //$$         return 0;
    //$$     }
    //$$     boolean enteredNow = false;
    //$$     if (!isCamera(player)) {
    //$$         if (!isEnabled()) {
    //$$             actionBar(player, "commandCamera 规则未开启");
    //$$             return 0;
    //$$         }
    //$$         if (!enter(player, true)) return 0;
    //$$         enteredNow = true;
    //$$     } else {
    //$$         if (!isEnabled()) {
    //$$             actionBar(player, "commandCamera 已关闭，只能返回或退出");
    //$$             return 0;
    //$$         }
    //$$         if (gameModeOf(player) != GameType.SPECTATOR) {
    //$$             actionBar(player, "Camera 状态异常，请使用 /c 恢复");
    //$$             return 0;
    //$$         }
    //$$     }
    //$$     if (beginFollow(player, target)) return 1;
    //$$     if (enteredNow) restore(player, true, false);
    //$$     return 0;
    //$$ }
    //$$
    //$$ private static boolean enter(ServerPlayer player, boolean feedback) {
    //$$     if (!canEnter(player)) return false;
    //$$     UUID playerId = player.getUUID();
    //$$     CameraState state = capture(player);
    //$$     if (!CommandCamera.beginSession(playerId, state)) {
    //$$         actionBar(player, "Camera 会话已存在");
    //$$         return false;
    //$$     }
    //$$     if (!setGameModeCompat(player, GameType.SPECTATOR)) {
    //$$         CommandCamera.removeSession(playerId, state);
    //$$         actionBar(player, "无法进入 Camera 模式");
    //$$         return false;
    //$$     }
    //$$     player.setCamera(player);
    //$$     if (feedback) cameraModeActionBar(player, true);
    //$$     return true;
    //$$ }
    //$$ private static CameraState capture(ServerPlayer player) {
    //$$     Vec3 motion = player.getDeltaMovement();
    //$$     return new CameraState(dimensionId(levelOf(player)), player.getX(), player.getY(), player.getZ(),
    //$$             yawOf(player), pitchOf(player), motion.x, motion.y, motion.z);
    //$$ }
    //$$ private static boolean canEnter(ServerPlayer player) {
    //$$     if (!player.isAlive()) {
    //$$         actionBar(player, "当前状态无法进入 Camera");
    //$$         return false;
    //$$     }
    //$$     if (gameModeOf(player) != GameType.SURVIVAL) {
    //$$         actionBar(player, "只有生存模式玩家可以进入 Camera");
    //$$         return false;
    //$$     }
    //$$     if (!isOnGround(player)) {
    //$$         actionBar(player, "必须站稳在地面上才能进入 Camera");
    //$$         return false;
    //$$     }
    //$$     if (player.getAirSupply() != player.getMaxAirSupply()) {
    //$$         actionBar(player, "必须处于正常空气环境才能进入 Camera");
    //$$         return false;
    //$$     }
    //$$     if (player.isOnFire()) {
    //$$         actionBar(player, "着火时无法进入 Camera");
    //$$         return false;
    //$$     }
    //$$     return true;
    //$$ }
    //$$
    //$$ private static boolean beginFollow(ServerPlayer player, ServerPlayer target) {
    //$$     stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     if (!teleportPlayer(player, levelOf(target), target.getX(), target.getY(), target.getZ(), yawOf(target), pitchOf(target))) {
    //$$         actionBar(player, "无法移动到目标玩家，跟随已取消");
    //$$         return false;
    //$$     }
    //$$     CommandCamera.beginFollow(player.getUUID(), target.getUUID(), FOLLOW_ATTACH_DELAY_TICKS);
    //$$     return true;
    //$$ }
    //$$ public static void tick(ServerPlayer player) {
    //$$     if (!CommandCamera.hasFollowTargets()) return;
    //$$     UUID playerId = player.getUUID(), targetId = CommandCamera.followTarget(playerId);
    //$$     if (targetId == null) return;
    //$$     if (!CommandCamera.isCamera(playerId) || gameModeOf(player) != GameType.SPECTATOR) {
    //$$         CommandCamera.clearFollow(playerId);
    //$$         return;
    //$$     }
    //$$     ServerLevel playerLevel = levelOf(player);
    //$$     ServerPlayer target = playerLevel.getServer().getPlayerList().getPlayer(targetId);
    //$$     if (target == null || !target.isAlive()) {
    //$$         stopFollowing(player, true);
    //$$         return;
    //$$     }
    //$$     ServerLevel targetLevel = levelOf(target);
    //$$     if (playerLevel != targetLevel) {
    //$$         player.setCamera(player);
    //$$         if (!teleportPlayer(player, targetLevel, target.getX(), target.getY(), target.getZ(), yawOf(target), pitchOf(target))) {
    //$$             stopFollowing(player, true);
    //$$             return;
    //$$         }
    //$$         CommandCamera.setFollowAttachDelay(playerId, FOLLOW_ATTACH_DELAY_TICKS);
    //$$         return;
    //$$     }
    //$$     int delay = CommandCamera.followAttachDelay(playerId);
    //$$     if (delay > 0) {
    //$$         CommandCamera.setFollowAttachDelay(playerId, delay - 1);
    //$$         return;
    //$$     }
    //$$     if (delay == 0) {
    //$$         CommandCamera.clearFollowAttachDelay(playerId);
    //$$         player.setCamera(target);
    //$$         return;
    //$$     }
    //$$     if (player.getCamera() != target) player.setCamera(target);
    //$$ }
    //$$ public static boolean handleFollowInput(ServerPlayer player, boolean shiftDown) {
    //$$     if (!CommandCamera.hasFollowTargets() || !CommandCamera.isFollowing(player.getUUID())) return false;
    //$$     if (shiftDown) stopFollowing(player, false);
    //$$     return true;
    //$$ }
    //$$ public static boolean shouldBlockMovement(ServerPlayer player) {
    //$$     return CommandCamera.hasFollowTargets() && CommandCamera.isFollowing(player.getUUID());
    //$$ }
    //$$ public static void stopFollowing(ServerPlayer player, boolean targetUnavailableFeedback) {
    //$$     if (!CommandCamera.clearFollow(player.getUUID())) return;
    //$$     player.setCamera(player);
    //$$     if (targetUnavailableFeedback) actionBar(player, "目标玩家已不可用，已停止跟随");
    //$$ }
    //$$
    //$$ private static boolean restore(ServerPlayer player, boolean exitToSurvival, boolean feedback) {
    //$$     UUID playerId = player.getUUID();
    //$$     CameraState state = CommandCamera.state(playerId);
    //$$     if (state == null) return false;
    //$$     ServerLevel destination = findLevel(player, state.dimensionId());
    //$$     if (destination == null) {
    //$$         if (feedback) actionBar(player, "Camera 原始维度不存在，已保持当前状态");
    //$$         return false;
    //$$     }
    //$$     stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     if (!teleportPlayer(player, destination, state.x(), state.y(), state.z(), state.yaw(), state.pitch())
    //$$             || !isAt(player, destination, state.x(), state.y(), state.z())) {
    //$$         if (feedback) actionBar(player, "无法返回 Camera 原点，已保持当前状态");
    //$$         return false;
    //$$     }
    //$$     player.setDeltaMovement(new Vec3(state.motionX(), state.motionY(), state.motionZ()));
    //$$     if (!exitToSurvival) {
    //$$         if (gameModeOf(player) != GameType.SPECTATOR) {
    //$$             if (feedback) actionBar(player, "Camera 状态异常，请使用 /c 恢复");
    //$$             return false;
    //$$         }
    //$$         return true;
    //$$     }
    //$$     if (gameModeOf(player) != GameType.SURVIVAL && !setGameModeCompat(player, GameType.SURVIVAL)) {
    //$$         if (feedback) actionBar(player, "已返回原点，但无法恢复生存模式");
    //$$         return false;
    //$$     }
    //$$     if (!CommandCamera.removeSession(playerId, state)) return false;
    //$$     if (feedback) cameraModeActionBar(player, false);
    //$$     return true;
    //$$ }
    //$$ static ServerLevel findLevel(ServerPlayer player, String targetDimensionId) {
    //$$     for (ServerLevel level : levelOf(player).getServer().getAllLevels())
    //$$         if (dimensionId(level).equals(targetDimensionId)) return level;
    //$$     return null;
    //$$ }
    //$$ public static void onPlayerDisconnect(ServerPlayer player) { if (isCamera(player)) restore(player, true, false); }
    //$$ private static boolean isVoidDamage(DamageSource source) {
    //#if MC <= 11903
    //$$     return source == DamageSource.OUT_OF_WORLD;
    //#else
    //$$     return source.is(DamageTypes.OUT_OF_WORLD);
    //#endif
    //$$ }
    //$$ static float yawOf(ServerPlayer player) {
    //#if MC <= 11605
    //$$     return player.yRot;
    //#else
    //$$     return player.getYRot();
    //#endif
    //$$ }
    //$$ static float pitchOf(ServerPlayer player) {
    //#if MC <= 11605
    //$$     return player.xRot;
    //#else
    //$$     return player.getXRot();
    //#endif
    //$$ }
    //$$ static GameType gameModeOf(ServerPlayer player) {
    //#if MC <= 12104
    //$$     return player.gameMode.getGameModeForPlayer();
    //#else
    //$$     return player.gameMode();
    //#endif
    //$$ }
    //$$ private static boolean setGameModeCompat(ServerPlayer player, GameType gameType) {
    //#if MC <= 11605
    //$$     player.setGameMode(gameType);
    //$$     return gameModeOf(player) == gameType;
    //#else
    //$$     return player.setGameMode(gameType);
    //#endif
    //$$ }
    //$$ static ServerLevel levelOf(ServerPlayer player) {
    //#if MC <= 11904
    //$$     return player.getLevel();
    //#elseif MC <= 12105
    //$$     return (ServerLevel) player.level();
    //#else
    //$$     return player.level();
    //#endif
    //$$ }
    //$$ private static boolean isOnGround(ServerPlayer player) {
    //#if MC <= 11904
    //$$     return player.isOnGround();
    //#else
    //$$     return player.onGround();
    //#endif
    //$$ }
    //$$ static String dimensionId(ServerLevel level) {
    //#if MC <= 12110
    //$$     return level.dimension().location().toString();
    //#else
    //$$     return level.dimension().identifier().toString();
    //#endif
    //$$ }
    //$$ static boolean teleportPlayer(ServerPlayer player, ServerLevel destination, double x, double y, double z, float yaw, float pitch) {
    //#if MC <= 11903
    //$$     player.teleportTo(destination, x, y, z, yaw, pitch);
    //$$     return isAt(player, destination, x, y, z);
    //#elseif MC <= 12101
    //$$     player.teleportTo(destination, x, y, z, Collections.emptySet(), yaw, pitch);
    //$$     return isAt(player, destination, x, y, z);
    //#else
    //$$     return player.teleportTo(destination, x, y, z, Collections.emptySet(), yaw, pitch, true);
    //#endif
    //$$ }
    //$$ private static boolean isAt(ServerPlayer player, ServerLevel destination, double x, double y, double z) {
    //$$     if (levelOf(player) != destination) return false;
    //$$     double dx = player.getX() - x, dy = player.getY() - y, dz = player.getZ() - z;
    //$$     return dx * dx + dy * dy + dz * dz <= TELEPORT_EPSILON_SQUARED;
    //$$ }
    //$$ private static void cameraModeActionBar(ServerPlayer player, boolean entered) {
    //$$     String text = entered ? "已进入旁观模式 | Entered camera mode" : "已退出旁观模式 | Exited camera mode";
    //#if MC <= 11802
    //$$     Component message = new TextComponent(text).withStyle(ChatFormatting.YELLOW);
    //#else
    //$$     Component message = Component.literal(text).withStyle(ChatFormatting.YELLOW);
    //#endif
    //$$     sendActionBar(player, message);
    //$$ }
    //$$ public static void actionBar(ServerPlayer player, String text) {
    //#if MC <= 11802
    //$$     Component message = new TextComponent(text);
    //#else
    //$$     Component message = Component.literal(text);
    //#endif
    //$$     sendActionBar(player, message);
    //$$ }
    //$$ private static void sendActionBar(ServerPlayer player, Component message) {
    //#if MC <= 12111
    //$$     player.displayClientMessage(message, true);
    //#else
    //$$     player.sendSystemMessage(message, true);
    //#endif
    //$$ }
    //#endif
}
