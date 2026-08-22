/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.feature.enhancednametag.compat;

import carpetddsaddition.feature.enhancednametag.BlockAnnotation;
import carpetddsaddition.feature.enhancednametag.BlockAnnotationStore;
import net.minecraft.server.MinecraftServer;
//#if MC >= 260102
//$$ import net.minecraft.resources.Identifier;
//#endif
//#if MC >= 11904
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.nbt.ListTag;
//$$ import net.minecraft.nbt.Tag;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.saveddata.SavedData;
//$$ import java.util.ArrayList;
//$$ import java.util.List;
//$$ import java.util.UUID;
//#endif
//#if MC >= 11904 && MC < 12105
//$$ import net.minecraft.nbt.NumericTag;
//$$ import net.minecraft.nbt.StringTag;
//#endif
//#if MC >= 12005 && MC < 12105
//$$ import net.minecraft.core.HolderLookup;
//#endif
//#if MC >= 12105
//$$ import com.mojang.serialization.Codec;
//$$ import net.minecraft.util.datafix.DataFixTypes;
//$$ import net.minecraft.world.level.saveddata.SavedDataType;
//#endif

/** Persistent storage for enhancedNameTag block annotations; runtime truth remains BlockAnnotationStore. */
//#if MC >= 11904
//$$ public final class EnhancedNameTagSavedData extends SavedData {
//#else
public final class EnhancedNameTagSavedData {
//#endif
    //#if MC >= 11904
    //$$ private static final String DATA_NAME = "carpet_dds_addition_enhanced_name_tag";
    //$$ private static final int DATA_VERSION = 1;
    //$$ private static final String KEY_DATA_VERSION = "DataVersion", KEY_ANNOTATIONS = "Annotations", KEY_ID = "Id",
    //$$         KEY_DIMENSION = "Dimension", KEY_POS = "Pos", KEY_BLOCK = "Block", KEY_TEXT = "Text";
    //$$ private static MinecraftServer boundServer;
    //$$ private static EnhancedNameTagSavedData activeData;
    //#endif

    //#if MC >= 12105
    //$$ private static final Codec<EnhancedNameTagSavedData> CODEC = CompoundTag.CODEC.xmap(
    //$$         EnhancedNameTagSavedData::decodeFromTag, EnhancedNameTagSavedData::encodeToTag);
    //$$ private static final SavedDataType<EnhancedNameTagSavedData> TYPE = new SavedDataType<>(
    //#if MC >= 260102
    //$$         Identifier.withDefaultNamespace(DATA_NAME),
    //#else
    //$$         DATA_NAME,
    //#endif
    //$$         EnhancedNameTagSavedData::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    //#endif

    private EnhancedNameTagSavedData() {}

    public static void ensureLoaded(MinecraftServer server) {
        //#if MC >= 11904
        //$$ if (boundServer == server && activeData != null) return;
        //$$ // Prevent process-local state leaking across integrated-server world replacement.
        //$$ boundServer = server;
        //$$ activeData = null;
        //$$ BlockAnnotationStore.clearForLoad();
        //$$ ServerLevel overworld = server.overworld();
        //#endif
        //#if MC >= 12105
        //$$ activeData = overworld.getDataStorage().computeIfAbsent(TYPE);
        //#endif
        //#if MC >= 12002 && MC < 12105
        //$$ activeData = overworld.getDataStorage().computeIfAbsent(factory(), DATA_NAME);
        //#endif
        //#if MC >= 11904 && MC < 12002
        //$$ activeData = overworld.getDataStorage().computeIfAbsent(
        //$$         EnhancedNameTagSavedData::loadLegacy, EnhancedNameTagSavedData::new, DATA_NAME);
        //#endif
    }

    public static void markDirty() {
        //#if MC >= 11904
        //$$ if (activeData != null) activeData.setDirty();
        //#endif
    }
    public static void resetRuntimeState() {
        //#if MC >= 11904
        //$$ boundServer = null;
        //$$ activeData = null;
        //#endif
    }

    //#if MC >= 12105
    //$$ private static EnhancedNameTagSavedData decodeFromTag(CompoundTag tag) {
    //$$     EnhancedNameTagSavedData data = new EnhancedNameTagSavedData();
    //$$     loadStoreFromTag(tag);
    //$$     return data;
    //$$ }
    //$$ private static CompoundTag encodeToTag(EnhancedNameTagSavedData data) { return createSaveTag(); }
    //#endif

    //#if MC >= 12005 && MC < 12105
    //$$ private static SavedData.Factory<EnhancedNameTagSavedData> factory() {
    //$$     return new SavedData.Factory<>(EnhancedNameTagSavedData::new, EnhancedNameTagSavedData::loadWithRegistries, null);
    //$$ }
    //$$ private static EnhancedNameTagSavedData loadWithRegistries(CompoundTag tag, HolderLookup.Provider registries) {
    //$$     EnhancedNameTagSavedData data = new EnhancedNameTagSavedData();
    //$$     loadStoreFromTag(tag);
    //$$     return data;
    //$$ }
    //$$ @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
    //$$     copySaveDataInto(tag);
    //$$     return tag;
    //$$ }
    //#endif

    //#if MC >= 12002 && MC < 12005
    //$$ private static SavedData.Factory<EnhancedNameTagSavedData> factory() {
    //$$     return new SavedData.Factory<>(EnhancedNameTagSavedData::new, EnhancedNameTagSavedData::loadFactoryLegacy, null);
    //$$ }
    //$$ private static EnhancedNameTagSavedData loadFactoryLegacy(CompoundTag tag) {
    //$$     EnhancedNameTagSavedData data = new EnhancedNameTagSavedData();
    //$$     loadStoreFromTag(tag);
    //$$     return data;
    //$$ }
    //#endif

    //#if MC >= 11904 && MC < 12002
    //$$ private static EnhancedNameTagSavedData loadLegacy(CompoundTag tag) {
    //$$     EnhancedNameTagSavedData data = new EnhancedNameTagSavedData();
    //$$     loadStoreFromTag(tag);
    //$$     return data;
    //$$ }
    //#endif

    //#if MC >= 11904 && MC < 12005
    //$$ @Override public CompoundTag save(CompoundTag tag) {
    //$$     copySaveDataInto(tag);
    //$$     return tag;
    //$$ }
    //#endif

    //#if MC >= 11904
    //$$ private static CompoundTag createSaveTag() {
    //$$     CompoundTag root = new CompoundTag();
    //$$     copySaveDataInto(root);
    //$$     return root;
    //$$ }
    //$$ private static void copySaveDataInto(CompoundTag root) {
    //$$     root.putInt(KEY_DATA_VERSION, DATA_VERSION);
    //$$     ListTag annotations = new ListTag();
    //$$     for (BlockAnnotation annotation : BlockAnnotationStore.getAll()) {
    //$$         CompoundTag entry = new CompoundTag();
    //$$         entry.putString(KEY_ID, annotation.getId().toString());
    //$$         entry.putString(KEY_DIMENSION, annotation.getDimension());
    //$$         entry.putLong(KEY_POS, annotation.getPos().asLong());
    //$$         entry.putString(KEY_BLOCK, annotation.getBlockId());
    //$$         entry.putString(KEY_TEXT, annotation.getRawText());
    //$$         annotations.add(entry);
    //$$     }
    //$$     root.put(KEY_ANNOTATIONS, annotations);
    //$$ }
    //$$ private static void loadStoreFromTag(CompoundTag root) {
    //$$     List<BlockAnnotation> loaded = new ArrayList<>();
    //$$     Tag annotationsTag = root.get(KEY_ANNOTATIONS);
    //$$     if (annotationsTag instanceof ListTag) {
    //$$         ListTag annotations = (ListTag) annotationsTag;
    //$$         for (int i = 0; i < annotations.size(); i++) {
    //$$             Tag element = annotations.get(i);
    //$$             if (!(element instanceof CompoundTag)) continue;
    //$$             BlockAnnotation annotation = readAnnotation((CompoundTag) element);
    //$$             if (annotation != null) loaded.add(annotation);
    //$$         }
    //$$     }
    //$$     BlockAnnotationStore.replaceFromLoad(loaded);
    //$$ }
    //$$ private static BlockAnnotation readAnnotation(CompoundTag tag) {
    //$$     String idText = readString(tag, KEY_ID), dimension = readString(tag, KEY_DIMENSION),
    //$$             blockId = readString(tag, KEY_BLOCK), text = readString(tag, KEY_TEXT);
    //$$     Long packedPos = readLong(tag, KEY_POS);
    //$$     if (idText == null || dimension == null || packedPos == null || blockId == null || text == null) return null;
    //$$     try {
    //$$         return new BlockAnnotation(UUID.fromString(idText), dimension, BlockPos.of(packedPos.longValue()), blockId, text);
    //$$     } catch (IllegalArgumentException ignored) { return null; }
    //$$ }
    //#endif

    //#if MC >= 12105
    //$$ private static String readString(CompoundTag tag, String key) { return tag.getString(key).orElse(null); }
    //$$ private static Long readLong(CompoundTag tag, String key) { return tag.getLong(key).orElse(null); }
    //#endif
    //#if MC >= 11904 && MC < 12105
    //$$ private static String readString(CompoundTag tag, String key) {
    //$$     Tag value = tag.get(key);
    //$$     return value instanceof StringTag ? ((StringTag) value).getAsString() : null;
    //$$ }
    //$$ private static Long readLong(CompoundTag tag, String key) {
    //$$     Tag value = tag.get(key);
    //$$     return value instanceof NumericTag ? Long.valueOf(((NumericTag) value).getAsLong()) : null;
    //$$ }
    //#endif
}
