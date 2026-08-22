/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.world.entity.Entity;
//$$ import java.util.Iterator;
//$$ import java.util.LinkedHashMap;
//$$ import java.util.Map;
//$$ import java.util.UUID;
//#endif

final class UndoEntityChanges {
    //#if MC >= 12109
    //$$ private final long recordId;
    //$$ private final Map<UUID, EntitySnapshot> before = new LinkedHashMap<>();
    //$$ private Map<UUID, EntitySnapshot> after;
    //$$
    //$$ UndoEntityChanges(long recordId) { this.recordId = recordId; }
    //$$ boolean isEmpty() { return before.isEmpty(); }
    //$$ boolean hasAfterSnapshot() { return after != null; }
    //$$
    //$$ void recordBefore(Entity entity) {
    //$$     before.computeIfAbsent(entity.getUUID(), ignored -> EntitySnapshot.capture(entity));
    //$$ }
    //$$ void recordAfter(Entity entity) {
    //$$     UUID uuid = entity.getUUID();
    //$$     EntitySnapshot previous = before.get(uuid);
    //$$     if (previous == null || previous.matchesEntityState(entity)) return;
    //$$     if (entity instanceof UndoMutationAccess access) access.dds$setUndoMutationId(recordId);
    //$$     if (after != null) after.put(uuid, EntitySnapshot.capture(entity));
    //$$ }
    //$$ void recordRemoved(UUID uuid) {
    //$$     if (after != null && before.containsKey(uuid)) after.put(uuid, EntitySnapshot.missing());
    //$$ }
    //$$ void recordSpawned(Entity entity) {
    //$$     before.putIfAbsent(entity.getUUID(), EntitySnapshot.missing());
    //$$     recordAfter(entity);
    //$$ }
    //$$ void recordFreshSpawned(Entity entity) {
    //$$     before.put(entity.getUUID(), EntitySnapshot.missing());
    //$$     recordAfter(entity);
    //$$ }
    //$$ void discardUnchanged(MinecraftServer server) {
    //$$     Iterator<Map.Entry<UUID, EntitySnapshot>> entries = before.entrySet().iterator();
    //$$     while (entries.hasNext()) {
    //$$         Map.Entry<UUID, EntitySnapshot> entry = entries.next();
    //$$         EntitySnapshot snapshot = entry.getValue();
    //$$         if (!snapshot.matchesCurrent(server, entry.getKey())) continue;
    //$$         snapshot.restoreUndoMutationMarker(server, entry.getKey());
    //$$         entries.remove();
    //$$     }
    //$$ }
    //$$ void captureAfter(MinecraftServer server) {
    //$$     Map<UUID, EntitySnapshot> captured = new LinkedHashMap<>();
    //$$     for (UUID uuid : before.keySet()) captured.put(uuid, EntitySnapshot.capture(server, uuid));
    //$$     after = captured;
    //$$ }
    //$$ int pruneConflicts(MinecraftServer server, boolean compareAgainstAfter) {
    //$$     if (after == null) return 0;
    //$$     int removed = 0;
    //$$     Iterator<Map.Entry<UUID, EntitySnapshot>> entries = before.entrySet().iterator();
    //$$     while (entries.hasNext()) {
    //$$         Map.Entry<UUID, EntitySnapshot> entry = entries.next();
    //$$         UUID uuid = entry.getKey();
    //$$         EntitySnapshot expected = compareAgainstAfter ? after.get(uuid) : entry.getValue();
    //$$         if (expected != null && expected.matchesCurrentMutationState(server, uuid)) {
    //$$             if (compareAgainstAfter) after.put(uuid, EntitySnapshot.capture(server, uuid));
    //$$             continue;
    //$$         }
    //$$         entries.remove();
    //$$         after.remove(uuid);
    //$$         removed++;
    //$$     }
    //$$     return removed;
    //$$ }
    //$$ void restoreBefore(MinecraftServer server) { restore(server, before); }
    //$$ void restoreAfter(MinecraftServer server) { if (after != null) restore(server, after); }
    //$$ long estimatedBytes() { return estimate(before) + (after == null ? 0L : estimate(after)); }
    //$$ private static void restore(MinecraftServer server, Map<UUID, EntitySnapshot> values) {
    //$$     for (Map.Entry<UUID, EntitySnapshot> entry : values.entrySet()) entry.getValue().restore(server, entry.getKey());
    //$$ }
    //$$ private static long estimate(Map<UUID, EntitySnapshot> values) {
    //$$     long result = 64L;
    //$$     for (EntitySnapshot snapshot : values.values()) result += 48L + snapshot.estimatedBytes();
    //$$     return result;
    //$$ }
    //#endif
}
