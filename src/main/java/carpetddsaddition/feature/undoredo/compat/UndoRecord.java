/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoCause;

//#if MC >= 12109
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.entity.Entity;
//$$ import java.util.UUID;
//#endif

public final class UndoRecord {
    //#if MC >= 12109
    //$$ private final long id;
    //$$ private final UUID owner;
    //$$ private final UndoCause cause;
    //$$ private final UndoBlockChanges blocks;
    //$$ private final UndoEntityChanges entities;
    //$$
    //$$ public UndoRecord(long id, UUID owner, UndoCause cause) {
    //$$     this.id = id;
    //$$     this.owner = owner;
    //$$     this.cause = cause;
    //$$     blocks = new UndoBlockChanges(id);
    //$$     entities = new UndoEntityChanges(id);
    //$$ }
    //$$
    //$$ public long id() { return id; }
    //$$ public UUID owner() { return owner; }
    //$$ public UndoCause cause() { return cause; }
    //$$ public boolean isEmpty() { return blocks.isEmpty() && entities.isEmpty(); }
    //$$ public void recordBlockCandidate(ServerLevel level, BlockPos pos) { blocks.recordCandidate(level, pos); }
    //$$ public void recordOcclusionCandidate(ServerLevel level, BlockPos pos) { blocks.recordOcclusionCandidate(level, pos); }
    //$$ public void recordBlockBefore(ServerLevel level, BlockPos pos) { blocks.recordBefore(level, pos); }
    //$$ public void recordBlockAfter(ServerLevel level, BlockPos pos) { blocks.recordAfter(level, pos); }
    //$$ public boolean confirmBlockEntityChange(ServerLevel level, BlockPos pos) { return blocks.confirmBlockEntityChange(level, pos); }
    //$$ public void recordEntityBefore(Entity entity) { entities.recordBefore(entity); }
    //$$ public void recordEntityAfter(Entity entity) { entities.recordAfter(entity); }
    //$$ public void recordEntityRemoved(UUID uuid) { entities.recordRemoved(uuid); }
    //$$ public void recordEntitySpawned(Entity entity) { entities.recordSpawned(entity); }
    //$$ public void recordFreshEntitySpawned(Entity entity) { entities.recordFreshSpawned(entity); }
    //$$ public void discardTentativeBlocks() { blocks.discardTentative(); }
    //$$ public void discardUnchangedBlocks(MinecraftServer server) { blocks.discardUnchanged(server); }
    //$$ public void discardUnchangedEntities(MinecraftServer server) { entities.discardUnchanged(server); }
    //$$ public void captureAfter(MinecraftServer server) {
    //$$     blocks.captureAfter(server);
    //$$     entities.captureAfter(server);
    //$$ }
    //$$ public boolean hasAfterSnapshot() { return blocks.hasAfterSnapshot() && entities.hasAfterSnapshot(); }
    //$$ public int pruneUndoConflicts(MinecraftServer server) {
    //$$     return hasAfterSnapshot() ? blocks.pruneConflicts(server, true) + entities.pruneConflicts(server, true) : 0;
    //$$ }
    //$$ public int pruneRedoConflicts(MinecraftServer server) {
    //$$     return hasAfterSnapshot() ? blocks.pruneConflicts(server, false) + entities.pruneConflicts(server, false) : 0;
    //$$ }
    //$$ // Preserve restore order: blocks -> entities -> neighbour updates.
    //$$ public void restoreBefore(MinecraftServer server) {
    //$$     blocks.restoreBeforeRaw(server);
    //$$     entities.restoreBefore(server);
    //$$     blocks.postProcessBefore(server);
    //$$ }
    //$$ public void restoreAfter(MinecraftServer server) {
    //$$     if (!hasAfterSnapshot()) return;
    //$$     blocks.restoreAfterRaw(server);
    //$$     entities.restoreAfter(server);
    //$$     blocks.postProcessAfter(server);
    //$$ }
    //$$ public long estimatedBytes() { return 112L + blocks.estimatedBytes() + entities.estimatedBytes(); }
    //#else
    private UndoRecord() {}
    //#endif
}
