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
//$$ import carpetddsaddition.mixin.feature.commandcamera.LivingEntityCameraAccessor;
//$$ import carpetddsaddition.mixin.feature.commandcamera.PlayerListCameraAccessor;
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
    //$$ public static boolean isCamera(ServerPlayer player) {
    //$$     CommandCameraPersistenceCompat.loadIfNeeded(player);
    //$$     return CommandCamera.isCamera(player.getUUID());
    //$$ }
    //$$
    //$$ /** Camera sessions always consume void damage; auto-back never mutates the immutable origin snapshot. */
    //$$ public static boolean handleVoidDamage(ServerPlayer player, DamageSource source) {
    //$$     if (!isCamera(player) || !isVoidDamage(source)) return false;
    //$$     ensureCameraState(player);
    //$$     boolean returned = returnToCameraOrigin(player, false);
    //$$     if (CommandCamera.shouldNotifyVoid(player.getUUID()))
    //$$         actionBar(player, returned ? "已自动返回 Camera 回位点 | Returned to camera safety point"
    //$$                 : "Camera 自动回位失败，会话已保留 | Camera return failed; session preserved");
    //$$     return true;
    //$$ }
    //$$ public static int toggle(ServerPlayer player) {
    //$$     if (isCamera(player)) return exitCamera(player, true) ? 1 : 0;
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
    //$$     return returnToCameraOrigin(player, true) ? 1 : 0;
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
    //$$     if (!isCamera(player)) {
    //$$         if (!isEnabled()) {
    //$$             actionBar(player, "commandCamera 规则未开启");
    //$$             return 0;
    //$$         }
    //$$         if (!enter(player, true)) return 0;
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
    //$$     return beginFollow(player, target) ? 1 : 0;
    //$$ }
    //$$
    //$$ private static boolean enter(ServerPlayer player, boolean feedback) {
    //$$     if (!canEnter(player)) return false;
    //$$     UUID playerId = player.getUUID();
    //$$     CameraState state = capture(player);
    //$$     if (!CommandCamera.beginSession(playerId, state)) {
    //$$         actionBar(player, "Camera 会话已存在或无法持久化");
    //$$         return false;
    //$$     }
    //$$     if (!setGameModeCompat(player, GameType.SPECTATOR)) {
    //$$         actionBar(player, "Camera 会话已保存，将自动重试进入旁观模式");
    //$$         return false;
    //$$     }
    //$$     player.setCamera(player);
    //$$     player.setDeltaMovement(new Vec3(0.0D, 0.0D, 0.0D));
    //$$     if (feedback) cameraModeActionBar(player, true);
    //$$     return true;
    //$$ }
    //$$ private static CameraState capture(ServerPlayer player) {
    //$$     Vec3 motion = player.getDeltaMovement();
    //$$     return new CameraState("survival", dimensionId(levelOf(player)), player.getX(), player.getY(), player.getZ(),
    //$$             yawOf(player), pitchOf(player), motion.x, motion.y, motion.z, (float) Math.max(0.0D, player.fallDistance),
    //$$             player.isFallFlying(), player.getAirSupply(), Math.max(0, player.getRemainingFireTicks()),
    //$$             frozenTicks(player), fallFlyingTicks(player));
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
    //$$     if (player.isPassenger()) {
    //$$         actionBar(player, "骑乘状态下无法进入 Camera");
    //$$         return false;
    //$$     }
    //$$     if (!DDSRules.noCameraSurvivalRestrictions()) {
    //$$         if (!isOnGround(player)) {
    //$$             actionBar(player, "必须站稳在地面上才能进入 Camera");
    //$$             return false;
    //$$         }
    //$$         if (player.getAirSupply() != player.getMaxAirSupply()) {
    //$$             actionBar(player, "必须处于正常空气环境才能进入 Camera");
    //$$             return false;
    //$$         }
    //$$         if (player.isOnFire()) {
    //$$             actionBar(player, "着火时无法进入 Camera");
    //$$             return false;
    //$$         }
    //$$     }
    //$$     return true;
    //$$ }
    //$$
    //$$ private static boolean beginFollow(ServerPlayer player, ServerPlayer target) {
    //$$     stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     if (!teleportPlayer(player, levelOf(target), target.getX(), target.getY(), target.getZ(), yawOf(target), pitchOf(target))) {
    //$$         actionBar(player, "无法移动到目标玩家，Camera 会话已保留");
    //$$         return false;
    //$$     }
    //$$     CommandCamera.beginFollow(player.getUUID(), target.getUUID(), FOLLOW_ATTACH_DELAY_TICKS);
    //$$     return true;
    //$$ }
    //$$ public static void ensureCameraState(ServerPlayer player) {
    //$$     if (!isCamera(player) || !player.isAlive()) return;
    //$$     if (gameModeOf(player) != GameType.SPECTATOR) {
    //$$         CommandCamera.clearFollow(player.getUUID());
    //$$         if (!setGameModeCompat(player, GameType.SPECTATOR)) return;
    //$$         player.setCamera(player);
    //$$     }
    //$$ }
    //$$ public static void tick(ServerPlayer player) {
    //$$     ensureCameraState(player);
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
    //$$ /** Camera-only return path: never restores the Survival snapshot and never rewrites/deletes it. */
    //$$ private static boolean returnToCameraOrigin(ServerPlayer player, boolean feedback) {
    //$$     CameraState state = CommandCamera.state(player.getUUID());
    //$$     if (state == null) return false;
    //$$     stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     ServerLevel destination = findLevel(player, state.dimensionId());
    //$$     if (destination == null) {
    //$$         if (feedback) actionBar(player, "Camera 原始维度不存在，已停止跟随并保留会话");
    //$$         return false;
    //$$     }
    //$$     if (gameModeOf(player) != GameType.SPECTATOR && !setGameModeCompat(player, GameType.SPECTATOR)) {
    //$$         if (feedback) actionBar(player, "无法恢复旁观模式，Camera 会话已保留");
    //$$         return false;
    //$$     }
    //$$     double y = CommandCamera.cameraReturnY(state.y(), minBuildHeight(destination));
    //$$     if (!teleportPlayer(player, destination, state.x(), y, state.z(), state.yaw(), state.pitch())
    //$$             || !isAt(player, destination, state.x(), y, state.z())) {
    //$$         if (feedback) actionBar(player, "无法返回 Camera 回位点，已保持当前状态");
    //$$         return false;
    //$$     }
    //$$     player.setDeltaMovement(new Vec3(0.0D, 0.0D, 0.0D));
    //$$     if (feedback) actionBar(player, y == state.y() ? "已返回 Camera 原点" : "原点低于安全下限，已返回 Camera 安全高度");
    //$$     return true;
    //$$ }
    //$$
    //$$ /** Explicit final exit: playerdata is committed before the DDS session is removed. */
    //$$ private static boolean exitCamera(ServerPlayer player, boolean feedback) {
    //$$     UUID playerId = player.getUUID();
    //$$     CameraState state = CommandCamera.state(playerId);
    //$$     if (state == null) return false;
    //$$     ServerLevel destination = findLevel(player, state.dimensionId());
    //$$     if (destination == null) return failExit(player, feedback, "Camera 原始维度不存在，Session 已保留");
    //$$     stopFollowing(player, false);
    //$$     player.setCamera(player);
    //$$     if (!teleportPlayer(player, destination, state.x(), state.y(), state.z(), state.yaw(), state.pitch())
    //$$             || !isAt(player, destination, state.x(), state.y(), state.z()))
    //$$         return failExit(player, feedback, "无法返回 Camera 原始坐标，Session 已保留");
    //$$     GameType originalGameMode = originalGameMode(state);
    //$$     if (gameModeOf(player) != originalGameMode && !setGameModeCompat(player, originalGameMode))
    //$$         return failExit(player, feedback, "无法恢复原游戏模式，Session 已保留");
    //$$     if (!restoreSurvivalState(player, state))
    //$$         return failExit(player, feedback, "无法完整恢复进入 Camera 前的生存状态，Session 已保留");
    //$$     if (gameModeOf(player) != originalGameMode || player.isFallFlying() != state.fallFlying())
    //$$         return failExit(player, feedback, "Camera 状态校验失败，Session 已保留");
    //$$     if (!savePlayerData(player)) return failExit(player, feedback, "玩家数据保存失败，Session 已保留");
    //$$     if (!CommandCamera.removeSession(playerId, state))
    //$$         return failExit(player, feedback, "Camera Session 提交失败，已保留以便重试");
    //$$     if (feedback) cameraModeActionBar(player, false);
    //$$     return true;
    //$$ }
    //$$ private static boolean restoreSurvivalState(ServerPlayer player, CameraState state) {
    //$$     player.setDeltaMovement(new Vec3(state.motionX(), state.motionY(), state.motionZ()));
    //$$     int maxAir = player.getMaxAirSupply(), savedAir = state.airSupply();
    //$$     player.setAirSupply(savedAir == CameraState.MAX_AIR_SENTINEL ? maxAir : Math.min(savedAir, maxAir));
    //$$     player.setRemainingFireTicks(state.remainingFireTicks());
    //$$     setFrozenTicks(player, state.frozenTicks());
    //$$     player.stopFallFlying();
    //$$     if (state.fallFlying() && !player.tryToStartFallFlying()) return false;
    //$$     setFallFlyingTicks(player, state.fallFlyingTicks());
    //$$     player.fallDistance = state.fallDistance();
    //$$     return player.isFallFlying() == state.fallFlying();
    //$$ }
    //$$ private static boolean failExit(ServerPlayer player, boolean feedback, String message) {
    //$$     CommandCamera.clearFollow(player.getUUID());
    //$$     player.setCamera(player);
    //$$     if (gameModeOf(player) != GameType.SPECTATOR) setGameModeCompat(player, GameType.SPECTATOR);
    //$$     player.setDeltaMovement(new Vec3(0.0D, 0.0D, 0.0D));
    //$$     savePlayerData(player);
    //$$     if (feedback) actionBar(player, message);
    //$$     return false;
    //$$ }
    //$$ private static boolean savePlayerData(ServerPlayer player) {
    //$$     try {
    //$$         ((PlayerListCameraAccessor) levelOf(player).getServer().getPlayerList()).carpetDDSAddition$savePlayer(player);
    //$$         return true;
    //$$     } catch (RuntimeException exception) {
    //$$         System.err.println("[Carpet DDS Addition] Failed to save Camera playerdata: " + exception.getMessage());
    //$$         return false;
    //$$     }
    //$$ }
    //$$ static ServerLevel findLevel(ServerPlayer player, String targetDimensionId) {
    //$$     for (ServerLevel level : levelOf(player).getServer().getAllLevels())
    //$$         if (dimensionId(level).equals(targetDimensionId)) return level;
    //$$     return null;
    //$$ }
    //$$ private static GameType originalGameMode(CameraState state) {
    //$$     switch (state.gameModeId()) {
    //$$         case "creative": return GameType.CREATIVE;
    //$$         case "adventure": return GameType.ADVENTURE;
    //$$         case "spectator": return GameType.SPECTATOR;
    //$$         default: return GameType.SURVIVAL;
    //$$     }
    //$$ }
    //$$ public static void onPlayerDisconnect(ServerPlayer player) { CommandCamera.clearRuntime(player.getUUID()); }
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
    //$$ private static int frozenTicks(ServerPlayer player) {
    //#if MC <= 11605
    //$$     return 0;
    //#else
    //$$     return Math.max(0, player.getTicksFrozen());
    //#endif
    //$$ }
    //$$ private static void setFrozenTicks(ServerPlayer player, int ticks) {
    //#if MC <= 11605
    //$$     // Powder snow freezing does not exist yet.
    //#else
    //$$     player.setTicksFrozen(ticks);
    //#endif
    //$$ }
    //$$ private static int fallFlyingTicks(ServerPlayer player) {
    //$$     return Math.max(0, ((LivingEntityCameraAccessor) player).carpetDDSAddition$getFallFlyingTicks());
    //$$ }
    //$$ private static void setFallFlyingTicks(ServerPlayer player, int ticks) {
    //$$     ((LivingEntityCameraAccessor) player).carpetDDSAddition$setFallFlyingTicks(ticks);
    //$$ }
    //$$ private static int minBuildHeight(ServerLevel level) {
    //#if MC <= 11605
    //$$     return 0;
    //#else
    //$$     return level.getMinBuildHeight();
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
