/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.enhancednametag.compat;

import carpetddsaddition.feature.enhancednametag.BlockAnnotation;
import carpetddsaddition.feature.enhancednametag.BlockAnnotationStore;
import carpetddsaddition.feature.enhancednametag.EnhancedNameTag;
import net.minecraft.server.MinecraftServer;

//#if MC >= 11904
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.phys.BlockHitResult;
//$$ import net.minecraft.world.phys.HitResult;
//$$ import java.util.HashMap;
//$$ import java.util.HashSet;
//$$ import java.util.List;
//$$ import java.util.Map;
//$$ import java.util.Set;
//$$ import java.util.UUID;
//#endif

/** Tracks at most one client-only block label per player; visibility remains active when editing is disabled. */
public final class BlockAnnotationTracker {
    //#if MC >= 11904
    //$$ private static MinecraftServer boundServer;
    //$$ private static final Map<UUID, UUID> VISIBLE_BY_PLAYER = new HashMap<>();
    //$$ private static final Set<UUID> ONLINE_PLAYERS = new HashSet<>();
    //#endif
    private BlockAnnotationTracker() {}

    public static void tick(MinecraftServer server) {
        //#if MC >= 11904
        //$$ tickSupported(server);
        //#endif
    }
    public static void resetRuntimeState() {
        //#if MC >= 11904
        //$$ boundServer = null;
        //$$ VISIBLE_BY_PLAYER.clear();
        //$$ ONLINE_PLAYERS.clear();
        //$$ VirtualTextDisplayCompat.resetRuntimeState();
        //#endif
    }

    //#if MC >= 11904
    //$$ private static void tickSupported(MinecraftServer server) {
    //$$     if (boundServer != server) {
    //$$         resetRuntimeState();
    //$$         boundServer = server;
    //$$     }
    //$$     if (server.getTickCount() % EnhancedNameTag.TRACK_INTERVAL_TICKS != 0) return;
    //$$     if (BlockAnnotationStore.isEmpty() && VISIBLE_BY_PLAYER.isEmpty()) return;
    //$$     List<ServerPlayer> players = server.getPlayerList().getPlayers();
    //$$     ONLINE_PLAYERS.clear();
    //$$     for (ServerPlayer player : players) {
    //$$         ONLINE_PLAYERS.add(player.getUUID());
    //$$         updatePlayer(player);
    //$$     }
    //$$     VISIBLE_BY_PLAYER.keySet().retainAll(ONLINE_PLAYERS);
    //$$     VirtualTextDisplayCompat.retainPlayers(ONLINE_PLAYERS);
    //$$ }
    //$$ private static void updatePlayer(ServerPlayer player) {
    //$$     UUID playerId = player.getUUID(), previousId = VISIBLE_BY_PLAYER.get(playerId);
    //$$     Level level = EnhancedNameTagCompat.getLevel(player);
    //$$     String dimension = EnhancedNameTagCompat.getDimensionId(level);
    //$$     BlockAnnotation target = BlockAnnotationStore.hasDimension(dimension)
    //$$             ? getTargetAnnotation(player, level, dimension) : null;
    //$$     UUID desiredId = target == null ? null : target.getId();
    //$$     if (sameId(previousId, desiredId)) return;
    //$$     if (previousId != null) VirtualTextDisplayCompat.hide(player);
    //$$     if (target != null) {
    //$$         VirtualTextDisplayCompat.show(player, target);
    //$$         VISIBLE_BY_PLAYER.put(playerId, target.getId());
    //$$     } else VISIBLE_BY_PLAYER.remove(playerId);
    //$$ }
    //$$ private static BlockAnnotation getTargetAnnotation(ServerPlayer player, Level level, String dimension) {
    //$$     HitResult hit = player.pick(EnhancedNameTag.TARGET_DISTANCE, 1.0F, false);
    //$$     if (hit.getType() != HitResult.Type.BLOCK) return null;
    //$$     BlockPos pos = ((BlockHitResult) hit).getBlockPos();
    //$$     BlockAnnotation annotation = BlockAnnotationStore.get(dimension, pos);
    //$$     if (annotation == null) return null;
    //$$     // Compare block type only; ordinary BlockState changes must not erase a valid annotation.
    //$$     BlockState currentState = level.getBlockState(pos);
    //$$     String currentBlockId = BuiltInRegistries.BLOCK.getKey(currentState.getBlock()).toString();
    //$$     if (!currentBlockId.equals(annotation.getBlockId())) {
    //$$         BlockAnnotationStore.remove(annotation.getId());
    //$$         return null;
    //$$     }
    //$$     return annotation;
    //$$ }
    //$$ private static boolean sameId(UUID first, UUID second) { return first == null ? second == null : first.equals(second); }
    //#endif
}
