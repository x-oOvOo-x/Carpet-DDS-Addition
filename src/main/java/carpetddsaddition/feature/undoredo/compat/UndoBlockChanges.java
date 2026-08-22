/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
//$$ import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
//$$ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.resources.ResourceKey;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.Level;
//$$ import java.util.Iterator;
//$$ import java.util.LinkedHashMap;
//$$ import java.util.Map;
//#endif

/** Block mutations owned by one Undo record. Raw restore stays separate from neighbour post-processing. */
final class UndoBlockChanges {
    //#if MC >= 12109
    //$$ private final long recordId;
    //$$ private final Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> before = new LinkedHashMap<>();
    //$$ private final Map<ResourceKey<Level>, LongOpenHashSet> tentative = new LinkedHashMap<>();
    //$$ private final Map<ResourceKey<Level>, LongOpenHashSet> occlusion = new LinkedHashMap<>();
    //$$ private Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> after;
    //$$
    //$$ UndoBlockChanges(long recordId) { this.recordId = recordId; }
    //$$ boolean isEmpty() {
    //$$     for (Long2ObjectOpenHashMap<BlockSnapshot> values : before.values()) if (!values.isEmpty()) return false;
    //$$     return true;
    //$$ }
    //$$ boolean hasAfterSnapshot() { return after != null; }
    //$$
    //$$ void recordCandidate(ServerLevel level, BlockPos pos) {
    //$$     ResourceKey<Level> dimension = level.dimension();
    //$$     Long2ObjectOpenHashMap<BlockSnapshot> blocks = before.computeIfAbsent(dimension, ignored -> new Long2ObjectOpenHashMap<>());
    //$$     long key = pos.asLong();
    //$$     if (blocks.containsKey(key)) return;
    //$$     blocks.put(key, BlockSnapshot.capture(level, pos));
    //$$     tentative.computeIfAbsent(dimension, ignored -> new LongOpenHashSet()).add(key);
    //$$ }
    //$$ void recordOcclusionCandidate(ServerLevel level, BlockPos pos) {
    //$$     recordCandidate(level, pos);
    //$$     occlusion.computeIfAbsent(level.dimension(), ignored -> new LongOpenHashSet()).add(pos.asLong());
    //$$ }
    //$$ void recordBefore(ServerLevel level, BlockPos pos) {
    //$$     ResourceKey<Level> dimension = level.dimension();
    //$$     Long2ObjectOpenHashMap<BlockSnapshot> blocks = before.computeIfAbsent(dimension, ignored -> new Long2ObjectOpenHashMap<>());
    //$$     long key = pos.asLong();
    //$$     blocks.computeIfAbsent(key, ignored -> BlockSnapshot.capture(level, pos));
    //$$     removeTentative(dimension, key);
    //$$ }
    //$$ void recordAfter(ServerLevel level, BlockPos pos) {
    //$$     if (after == null) return;
    //$$     ResourceKey<Level> dimension = level.dimension();
    //$$     Long2ObjectOpenHashMap<BlockSnapshot> existing = before.get(dimension);
    //$$     long key = pos.asLong();
    //$$     if (existing == null || !existing.containsKey(key)) return;
    //$$     after.computeIfAbsent(dimension, ignored -> new Long2ObjectOpenHashMap<>()).put(key, BlockSnapshot.capture(level, pos));
    //$$ }
    //$$
    //$$ boolean confirmBlockEntityChange(ServerLevel level, BlockPos pos) {
    //$$     ResourceKey<Level> dimension = level.dimension();
    //$$     Long2ObjectOpenHashMap<BlockSnapshot> blocks = before.get(dimension);
    //$$     long key = pos.asLong();
    //$$     if (blocks == null || !blocks.containsKey(key)) return false;
    //$$     removeTentative(dimension, key);
    //$$     return true;
    //$$ }
    //$$ void discardTentative() {
    //$$     for (Map.Entry<ResourceKey<Level>, LongOpenHashSet> entry : tentative.entrySet()) {
    //$$         ResourceKey<Level> dimension = entry.getKey();
    //$$         Long2ObjectOpenHashMap<BlockSnapshot> blocks = before.get(dimension);
    //$$         if (blocks == null) continue;
    //$$         LongOpenHashSet occlusionValues = occlusion.get(dimension);
    //$$         for (long key : entry.getValue()) {
    //$$             boolean keepOcclusion = occlusionValues != null && occlusionValues.contains(key)
    //$$                     && blocks.containsKey(BlockPos.of(key).above().asLong());
    //$$             if (keepOcclusion) continue;
    //$$             blocks.remove(key);
    //$$             if (occlusionValues != null) occlusionValues.remove(key);
    //$$             if (after != null) {
    //$$                 Long2ObjectOpenHashMap<BlockSnapshot> afterValues = after.get(dimension);
    //$$                 if (afterValues != null) afterValues.remove(key);
    //$$             }
    //$$         }
    //$$     }
    //$$     tentative.clear();
    //$$     removeEmptyBeforeDimensions();
    //$$     removeEmptyOcclusionDimensions();
    //$$     removeEmptyAfterDimensions();
    //$$ }
    //$$ void discardUnchanged(MinecraftServer server) {
    //$$     for (Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> dimensionEntry : before.entrySet()) {
    //$$         ServerLevel level = server.getLevel(dimensionEntry.getKey());
    //$$         if (level == null) continue;
    //$$         LongOpenHashSet occlusionValues = occlusion.get(dimensionEntry.getKey());
    //$$         dimensionEntry.getValue().long2ObjectEntrySet().removeIf(entry ->
    //$$                 (occlusionValues == null || !occlusionValues.contains(entry.getLongKey()))
    //$$                         && entry.getValue().matchesCurrent(level, BlockPos.of(entry.getLongKey())));
    //$$     }
    //$$     removeEmptyBeforeDimensions();
    //$$ }
    //$$
    //$$ void captureAfter(MinecraftServer server) {
    //$$     Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> captured = new LinkedHashMap<>();
    //$$     for (Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> dimensionEntry : before.entrySet()) {
    //$$         ServerLevel level = server.getLevel(dimensionEntry.getKey());
    //$$         if (level == null) continue;
    //$$         Long2ObjectOpenHashMap<BlockSnapshot> values = new Long2ObjectOpenHashMap<>();
    //$$         for (Long2ObjectMap.Entry<BlockSnapshot> entry : dimensionEntry.getValue().long2ObjectEntrySet())
    //$$             values.put(entry.getLongKey(), BlockSnapshot.capture(level, BlockPos.of(entry.getLongKey())));
    //$$         captured.put(dimensionEntry.getKey(), values);
    //$$     }
    //$$     after = captured;
    //$$ }
    //$$
    //$$ int pruneConflicts(MinecraftServer server, boolean compareAgainstAfter) {
    //$$     if (after == null) return 0;
    //$$     int removed = 0;
    //$$     Iterator<Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>>> dimensions = before.entrySet().iterator();
    //$$     while (dimensions.hasNext()) {
    //$$         Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> dimensionEntry = dimensions.next();
    //$$         ResourceKey<Level> dimension = dimensionEntry.getKey();
    //$$         ServerLevel level = server.getLevel(dimension);
    //$$         Long2ObjectOpenHashMap<BlockSnapshot> beforeValues = dimensionEntry.getValue(), afterValues = after.get(dimension);
    //$$         LongOpenHashSet occlusionValues = occlusion.get(dimension);
    //$$         Iterator<Long2ObjectMap.Entry<BlockSnapshot>> entries = beforeValues.long2ObjectEntrySet().iterator();
    //$$         while (entries.hasNext()) {
    //$$             Long2ObjectMap.Entry<BlockSnapshot> entry = entries.next();
    //$$             long key = entry.getLongKey();
    //$$             BlockPos pos = BlockPos.of(key);
    //$$             if (compareAgainstAfter && level != null && afterValues != null
    //$$                     && BlockSnapshot.currentUndoOriginId(level, pos) == recordId)
    //$$                 afterValues.put(key, BlockSnapshot.capture(level, pos));
    //$$             BlockSnapshot expected = compareAgainstAfter ? afterValues == null ? null : afterValues.get(key) : entry.getValue();
    //$$             boolean conflict = level == null || expected == null || !expected.matchesCurrent(level, pos);
    //$$             if (conflict && compareAgainstAfter && level != null && afterValues != null && occlusionValues != null
    //$$                     && occlusionValues.contains(key) && entry.getValue().isOcclusionDegradedToCurrent(level, pos)) {
    //$$                 BlockSnapshot causalAbove = afterValues.get(pos.above().asLong());
    //$$                 if (causalAbove != null && causalAbove.matchesCurrent(level, pos.above())) {
    //$$                     afterValues.put(key, BlockSnapshot.capture(level, pos));
    //$$                     conflict = false;
    //$$                 }
    //$$             }
    //$$             if (!conflict) continue;
    //$$             entries.remove();
    //$$             if (afterValues != null) afterValues.remove(key);
    //$$             if (occlusionValues != null) occlusionValues.remove(key);
    //$$             removed++;
    //$$         }
    //$$         if (beforeValues.isEmpty()) dimensions.remove();
    //$$         if (afterValues != null && afterValues.isEmpty()) after.remove(dimension);
    //$$         if (occlusionValues != null && occlusionValues.isEmpty()) occlusion.remove(dimension);
    //$$     }
    //$$     return removed;
    //$$ }
    //$$
    //$$ void restoreBeforeRaw(MinecraftServer server) { restoreRaw(server, before); }
    //$$ void restoreAfterRaw(MinecraftServer server) { if (after != null) restoreRaw(server, after); }
    //$$ void postProcessBefore(MinecraftServer server) { postProcess(server, before); }
    //$$ void postProcessAfter(MinecraftServer server) { if (after != null) postProcess(server, after); }
    //$$ private static void restoreRaw(MinecraftServer server,
    //$$                                Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> valuesByLevel) {
    //$$     for (Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> dimensionEntry : valuesByLevel.entrySet()) {
    //$$         ServerLevel level = server.getLevel(dimensionEntry.getKey());
    //$$         if (level == null) continue;
    //$$         for (Long2ObjectMap.Entry<BlockSnapshot> entry : dimensionEntry.getValue().long2ObjectEntrySet())
    //$$             entry.getValue().restore(level, BlockPos.of(entry.getLongKey()));
    //$$     }
    //$$ }
    //$$ private static void postProcess(MinecraftServer server,
    //$$                                 Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> valuesByLevel) {
    //$$     for (Map.Entry<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> dimensionEntry : valuesByLevel.entrySet()) {
    //$$         ServerLevel level = server.getLevel(dimensionEntry.getKey());
    //$$         if (level == null) continue;
    //$$         for (long key : dimensionEntry.getValue().keySet()) {
    //$$             BlockPos pos = BlockPos.of(key);
    //$$             level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
    //$$         }
    //$$     }
    //$$ }
    //$$
    //$$ long estimatedBytes() {
    //$$     long result = estimateSnapshots(before);
    //$$     for (LongOpenHashSet values : occlusion.values()) result += 32L + 8L * values.size();
    //$$     return result + (after == null ? 0L : estimateSnapshots(after));
    //$$ }
    //$$ private static long estimateSnapshots(Map<ResourceKey<Level>, Long2ObjectOpenHashMap<BlockSnapshot>> values) {
    //$$     long result = 0L;
    //$$     for (Long2ObjectOpenHashMap<BlockSnapshot> blocks : values.values()) {
    //$$         result += 64L;
    //$$         for (BlockSnapshot snapshot : blocks.values()) result += 16L + snapshot.estimatedBytes();
    //$$     }
    //$$     return result;
    //$$ }
    //$$
    //$$ private void removeTentative(ResourceKey<Level> dimension, long key) {
    //$$     LongOpenHashSet values = tentative.get(dimension);
    //$$     if (values == null) return;
    //$$     values.remove(key);
    //$$     if (values.isEmpty()) tentative.remove(dimension);
    //$$ }
    //$$ private void removeEmptyBeforeDimensions() { before.entrySet().removeIf(entry -> entry.getValue().isEmpty()); }
    //$$ private void removeEmptyOcclusionDimensions() { occlusion.entrySet().removeIf(entry -> entry.getValue().isEmpty()); }
    //$$ private void removeEmptyAfterDimensions() {
    //$$     if (after != null) after.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    //$$ }
    //#endif
}
