/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.util.ProblemReporter;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//$$ import net.minecraft.world.entity.EntityType;
//$$ import net.minecraft.world.entity.LivingEntity;
//#if MC >= 12111
//$$ import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
//#else
//$$ import net.minecraft.world.entity.vehicle.MinecartTNT;
//#endif
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import net.minecraft.world.level.storage.TagValueOutput;
//$$ import java.util.UUID;
//#endif

public final class EntitySnapshot {
    //#if MC >= 12109
    //$$ private static final EntitySnapshot MISSING = new EntitySnapshot(false, null, null, null, 0L, 0L, 0L, 0L, false, null, false);
    //$$ private final boolean exists;
    //$$ private final ResourceKey<Level> dimension;
    //$$ private final EntityType<?> type;
    //$$ private final CompoundTag data;
    //$$ private final long undoOriginId, undoSpawnOriginId, undoBurnOriginId, undoMutationId;
    //$$ private final boolean terminalDeath;
    //$$ private final CreeperRuntimeState creeperRuntime;
    //$$ private final boolean tntMinecartPrimed;
    //$$
    //$$ private EntitySnapshot(boolean exists, ResourceKey<Level> dimension, EntityType<?> type, CompoundTag data,
    //$$                        long undoOriginId, long undoSpawnOriginId, long undoBurnOriginId, long undoMutationId,
    //$$                        boolean terminalDeath, CreeperRuntimeState creeperRuntime, boolean tntMinecartPrimed) {
    //$$     this.exists = exists;
    //$$     this.dimension = dimension;
    //$$     this.type = type;
    //$$     this.data = data;
    //$$     this.undoOriginId = undoOriginId;
    //$$     this.undoSpawnOriginId = undoSpawnOriginId;
    //$$     this.undoBurnOriginId = undoBurnOriginId;
    //$$     this.undoMutationId = undoMutationId;
    //$$     this.terminalDeath = terminalDeath;
    //$$     this.creeperRuntime = creeperRuntime;
    //$$     this.tntMinecartPrimed = tntMinecartPrimed;
    //$$ }
    //$$
    //$$ public static EntitySnapshot missing() { return MISSING; }
    //$$ public static EntitySnapshot capture(Entity entity) {
    //$$     if (!(entity.level() instanceof ServerLevel level) || entity instanceof ServerPlayer) return MISSING;
    //$$     return new EntitySnapshot(true, level.dimension(), entity.getType(), saveEntityData(level, entity),
    //$$             undoOriginId(entity), undoSpawnOriginId(entity), undoBurnOriginId(entity), undoMutationId(entity),
    //$$             isTerminalDeath(entity), CreeperRuntimeState.capture(entity),
    //$$             entity instanceof MinecartTNT minecart && minecart.isPrimed());
    //$$ }
    //$$ public static EntitySnapshot capture(MinecraftServer server, UUID uuid) {
    //$$     Entity entity = findEntity(server, uuid);
    //$$     return entity == null ? MISSING : capture(entity);
    //$$ }
    //$$ public static long currentUndoOriginId(MinecraftServer server, UUID uuid) { return undoOriginId(findEntity(server, uuid)); }
    //$$ public static long currentUndoMutationId(MinecraftServer server, UUID uuid) { return undoMutationId(findEntity(server, uuid)); }
    //$$
    //$$ public boolean matchesCurrent(MinecraftServer server, UUID uuid) {
    //$$     Entity current = findEntity(server, uuid, dimension);
    //$$     return exists ? matchesEntityState(current) : current == null;
    //$$ }
    //$$ public boolean matchesEntityState(Entity current) {
    //$$     if (!exists || current == null || current instanceof ServerPlayer || !(current.level() instanceof ServerLevel level)
    //$$             || current.getType() != type || !level.dimension().equals(dimension)) return false;
    //$$     if (undoOriginId(current) != undoOriginId || isTerminalDeath(current) != terminalDeath) return false;
    //$$     CreeperRuntimeState currentCreeper = CreeperRuntimeState.capture(current);
    //$$     return sameCreeperGameplayState(currentCreeper) && data.equals(saveEntityData(level, current));
    //$$ }
    //$$ private boolean sameCreeperGameplayState(CreeperRuntimeState other) {
    //$$     return creeperRuntime == null || other == null ? creeperRuntime == other : creeperRuntime.sameGameplayState(other);
    //$$ }
    //$$ public boolean matchesCurrentMutationState(MinecraftServer server, UUID uuid) {
    //$$     Entity current = findEntity(server, uuid, dimension);
    //$$     if (!exists) return current == null;
    //$$     if (terminalDeath && current == null) return true;
    //$$     if (current == null || current instanceof ServerPlayer || !(current.level() instanceof ServerLevel level)
    //$$             || current.getType() != type || !level.dimension().equals(dimension)) return false;
    //$$     if (terminalDeath && !isTerminalDeath(current)) return false;
    //$$     return undoMutationId(current) == undoMutationId;
    //$$ }
    //$$ public void restoreUndoMutationMarker(MinecraftServer server, UUID uuid) {
    //$$     Entity current = findEntity(server, uuid, dimension);
    //$$     if (exists && current != null && current.getType() == type && current.level() instanceof ServerLevel level
    //$$             && level.dimension().equals(dimension) && current instanceof UndoMutationAccess access)
    //$$         access.dds$setUndoMutationId(undoMutationId);
    //$$ }
    //$$
    //$$ public void restore(MinecraftServer server, UUID uuid) {
    //$$     Entity current = findEntity(server, uuid, dimension);
    //$$     if (!exists || terminalDeath) {
    //$$         if (current != null && !(current instanceof ServerPlayer)) current.discard();
    //$$         return;
    //$$     }
    //$$     ServerLevel targetLevel = server.getLevel(dimension);
    //$$     if (targetLevel == null || type == null || data == null || current instanceof ServerPlayer) return;
    //$$     if (current instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) {
    //$$         current.discard();
    //$$         current = null;
    //$$     }
    //$$     if (current != null && (current.level() != targetLevel || current.getType() != type)) {
    //$$         current.discard();
    //$$         current = null;
    //$$     }
    //$$     // MinecartTNT keeps client fuse runtime state; primed -> unprimed must recreate to stop stale flashing.
    //$$     if (current != null && !tntMinecartPrimed && current instanceof MinecartTNT minecart && minecart.isPrimed()) {
    //$$         current.discard();
    //$$         current = null;
    //$$     }
    //$$     if (current == null) {
    //$$         Entity recreated = type.create(targetLevel, EntitySpawnReason.COMMAND);
    //$$         if (recreated == null) return;
    //$$         load(recreated, targetLevel, uuid);
    //$$         targetLevel.addFreshEntity(recreated);
    //$$         return;
    //$$     }
    //$$     load(current, targetLevel, uuid);
    //$$ }
    //$$ private void load(Entity entity, ServerLevel level, UUID uuid) {
    //$$     entity.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data.copy()));
    //$$     entity.setUUID(uuid);
    //$$     if (entity instanceof UndoOriginAccess access) access.dds$setUndoOriginId(undoOriginId);
    //$$     if (entity instanceof UndoSpawnOriginAccess access) access.dds$setUndoSpawnOriginId(undoSpawnOriginId);
    //$$     if (entity instanceof UndoBurnOriginAccess access) access.dds$setUndoBurnOriginId(undoBurnOriginId);
    //$$     if (entity instanceof UndoMutationAccess access) access.dds$setUndoMutationId(undoMutationId);
    //$$     if (creeperRuntime != null) creeperRuntime.restore(entity);
    //$$ }
    //$$
    //$$ private static CompoundTag saveEntityData(ServerLevel level, Entity entity) {
    //$$     TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
    //$$     entity.saveWithoutId(output);
    //$$     return output.buildResult();
    //$$ }
    //$$ private static long undoOriginId(Entity entity) {
    //$$     return entity instanceof UndoOriginAccess access ? access.dds$getUndoOriginId() : 0L;
    //$$ }
    //$$ private static long undoSpawnOriginId(Entity entity) {
    //$$     return entity instanceof UndoSpawnOriginAccess access ? access.dds$getUndoSpawnOriginId() : 0L;
    //$$ }
    //$$ private static long undoBurnOriginId(Entity entity) {
    //$$     return entity instanceof UndoBurnOriginAccess access ? access.dds$getUndoBurnOriginId() : 0L;
    //$$ }
    //$$ private static long undoMutationId(Entity entity) {
    //$$     return entity instanceof UndoMutationAccess access ? access.dds$getUndoMutationId() : 0L;
    //$$ }
    //$$ private static boolean isTerminalDeath(Entity entity) {
    //$$     return entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying();
    //$$ }
    //$$
    //$$ private static Entity findEntity(MinecraftServer server, UUID uuid, ResourceKey<Level> preferredDimension) {
    //$$     ServerLevel preferredLevel = preferredDimension == null ? null : server.getLevel(preferredDimension);
    //$$     if (preferredLevel != null) {
    //$$         Entity entity = preferredLevel.getEntity(uuid);
    //$$         if (entity != null) return entity;
    //$$     }
    //$$     for (ServerLevel level : server.getAllLevels()) {
    //$$         if (level == preferredLevel) continue;
    //$$         Entity entity = level.getEntity(uuid);
    //$$         if (entity != null) return entity;
    //$$     }
    //$$     return null;
    //$$ }
    //$$ private static Entity findEntity(MinecraftServer server, UUID uuid) { return findEntity(server, uuid, null); }
    //$$ public long estimatedBytes() { return exists ? 121L + data.sizeInBytes() + (creeperRuntime == null ? 0L : 32L) : 24L; }
    //$$
    //$$ private static final class CreeperRuntimeState {
    //$$     private final long ignitionOriginId;
    //$$     private final boolean ignited;
    //$$     private final int oldSwell, swell, swellDir;
    //$$
    //$$     private CreeperRuntimeState(long ignitionOriginId, boolean ignited, int oldSwell, int swell, int swellDir) {
    //$$         this.ignitionOriginId = ignitionOriginId;
    //$$         this.ignited = ignited;
    //$$         this.oldSwell = oldSwell;
    //$$         this.swell = swell;
    //$$         this.swellDir = swellDir;
    //$$     }
    //$$     private static CreeperRuntimeState capture(Entity entity) {
    //$$         if (!(entity instanceof UndoCreeperAccess access)) return null;
    //$$         return new CreeperRuntimeState(access.dds$getUndoIgnitionOriginId(), access.dds$isIgnited(),
    //$$                 access.dds$getOldSwell(), access.dds$getSwell(), access.dds$getSwellDir());
    //$$     }
    //$$     private void restore(Entity entity) {
    //$$         if (!(entity instanceof UndoCreeperAccess access)) return;
    //$$         access.dds$setUndoIgnitionOriginId(ignitionOriginId);
    //$$         access.dds$setIgnited(ignited);
    //$$         access.dds$setOldSwell(oldSwell);
    //$$         access.dds$setSwell(swell);
    //$$         access.dds$setSwellDir(swellDir);
    //$$     }
    //$$     private boolean sameGameplayState(CreeperRuntimeState other) {
    //$$         return ignited == other.ignited && oldSwell == other.oldSwell && swell == other.swell && swellDir == other.swellDir;
    //$$     }
    //$$ }
    //#else
    private EntitySnapshot() {}
    //#endif
}
