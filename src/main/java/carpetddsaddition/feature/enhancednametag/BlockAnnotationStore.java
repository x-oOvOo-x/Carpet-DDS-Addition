/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.enhancednametag;

import carpetddsaddition.feature.enhancednametag.compat.EnhancedNameTagSavedData;
import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Exact dimension+BlockPos runtime index; persistent storage is owned by EnhancedNameTagSavedData. */
public final class BlockAnnotationStore {
    private static final Map<String, Map<BlockPos, BlockAnnotation>> BY_DIMENSION = new HashMap<>();
    private static final Map<UUID, BlockAnnotation> BY_ID = new HashMap<>();
    private BlockAnnotationStore() {}

    public static BlockAnnotation put(String dimension, BlockPos pos, String blockId, String rawText) {
        BlockAnnotation annotation = new BlockAnnotation(UUID.randomUUID(), dimension, pos, blockId, rawText);
        index(annotation);
        EnhancedNameTagSavedData.markDirty();
        return annotation;
    }
    public static BlockAnnotation get(String dimension, BlockPos pos) {
        Map<BlockPos, BlockAnnotation> dimensionMap = BY_DIMENSION.get(dimension);
        return dimensionMap == null ? null : dimensionMap.get(pos);
    }
    public static BlockAnnotation get(UUID id) { return BY_ID.get(id); }
    public static boolean hasDimension(String dimension) {
        Map<BlockPos, BlockAnnotation> dimensionMap = BY_DIMENSION.get(dimension);
        return dimensionMap != null && !dimensionMap.isEmpty();
    }
    public static boolean remove(UUID id) {
        BlockAnnotation annotation = BY_ID.get(id);
        if (annotation == null) return false;
        removeInternal(annotation);
        EnhancedNameTagSavedData.markDirty();
        return true;
    }
    public static boolean remove(String dimension, BlockPos pos) {
        BlockAnnotation annotation = get(dimension, pos);
        if (annotation == null) return false;
        removeInternal(annotation);
        EnhancedNameTagSavedData.markDirty();
        return true;
    }
    public static List<BlockAnnotation> getAll() { return new ArrayList<>(BY_ID.values()); }
    public static boolean isEmpty() { return BY_ID.isEmpty(); }
    /** Clears runtime state for a new load without marking SavedData dirty. */
    public static void clearForLoad() {
        BY_DIMENSION.clear();
        BY_ID.clear();
    }
    /** Replaces runtime indexes from persistence without marking SavedData dirty. */
    public static void replaceFromLoad(Collection<BlockAnnotation> annotations) {
        BY_DIMENSION.clear();
        BY_ID.clear();
        for (BlockAnnotation annotation : annotations) index(annotation);
    }
    private static void index(BlockAnnotation annotation) {
        String dimension = annotation.getDimension();
        BlockPos pos = annotation.getPos().immutable();
        Map<BlockPos, BlockAnnotation> dimensionMap = BY_DIMENSION.computeIfAbsent(dimension, ignored -> new HashMap<>());
        BlockAnnotation previous = dimensionMap.put(pos, annotation);
        if (previous != null && !previous.getId().equals(annotation.getId())) BY_ID.remove(previous.getId());
        BY_ID.put(annotation.getId(), annotation);
    }
    private static void removeInternal(BlockAnnotation annotation) {
        BY_ID.remove(annotation.getId());
        Map<BlockPos, BlockAnnotation> dimensionMap = BY_DIMENSION.get(annotation.getDimension());
        if (dimensionMap == null) return;
        dimensionMap.remove(annotation.getPos());
        if (dimensionMap.isEmpty()) BY_DIMENSION.remove(annotation.getDimension());
    }
}
