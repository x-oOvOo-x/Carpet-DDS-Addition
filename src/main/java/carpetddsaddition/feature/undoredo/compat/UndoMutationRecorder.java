/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoRedo;

//#if MC >= 12109
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.Entity;
//#endif

public final class UndoMutationRecorder {

    private UndoMutationRecorder() {
    }


    /*
     * ============================================================
     * Block mutations
     * ============================================================
     */

    //#if MC >= 12109

    //$$ public static void recordBlockCandidate(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {
    //$$     if (!canRecordMutation()) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.recordBlockCandidate(
    //$$                 level,
    //$$                 pos.immutable()
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void recordOcclusionCandidate(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {
    //$$     if (!canRecordMutation()) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.recordOcclusionCandidate(
    //$$                 level,
    //$$                 pos.immutable()
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void recordBlockBefore(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {
    //$$     if (!canRecordMutation()) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.recordBlockBefore(
    //$$                 level,
    //$$                 pos.immutable()
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void recordBlockAfter(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {
    //$$     if (!canRecordMutation()) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.recordBlockAfter(
    //$$                 level,
    //$$                 pos.immutable()
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void confirmBlockEntityChange(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {
    //$$     if (!canRecordMutation()) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.confirmBlockEntityChange(
    //$$                 level,
    //$$                 pos.immutable()
    //$$         );
    //$$     }
    //$$ }

    //#endif


    /*
     * ============================================================
     * Entity mutations
     * ============================================================
     */

    //#if MC >= 12109

    //$$ public static void recordEntityBefore(
    //$$         Entity entity
    //$$ ) {
    //$$     if (!canRecordEntity(entity)) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record == null) {
    //$$         return;
    //$$     }
    //$$
    //$$     if (!(entity.level()
    //$$             instanceof ServerLevel level)
    //$$             || level.getEntity(
    //$$             entity.getUUID()
    //$$     ) != entity) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     record.recordEntityBefore(
    //$$             entity
    //$$     );
    //$$ }
    //$$
    //$$ public static void recordEntityAfter(
    //$$         Entity entity
    //$$ ) {
    //$$     if (!canRecordEntity(entity)) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record == null) {
    //$$         return;
    //$$     }
    //$$
    //$$     if (!(entity.level()
    //$$             instanceof ServerLevel level)
    //$$             || level.getEntity(
    //$$             entity.getUUID()
    //$$     ) != entity) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     record.recordEntityAfter(
    //$$             entity
    //$$     );
    //$$ }
    //$$
    //$$ public static void recordEntityRemoved(
    //$$         Entity entity
    //$$ ) {
    //$$     if (!canRecordEntity(entity)) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record != null) {
    //$$         record.recordEntityRemoved(
    //$$                 entity.getUUID()
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void recordEntitySpawned(
    //$$         Entity entity
    //$$ ) {
    //$$     if (!canRecordEntity(entity)) {
    //$$         return;
    //$$     }
    //$$
    //$$     UndoRecord record =
    //$$             UndoContext.current();
    //$$
    //$$     if (record == null) {
    //$$         return;
    //$$     }
    //$$
    //$$     if (!(entity.level()
    //$$             instanceof ServerLevel level)
    //$$             || level.getEntity(
    //$$             entity.getUUID()
    //$$     ) != entity) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     record.recordEntitySpawned(
    //$$             entity
    //$$     );
    //$$ }

    //#endif


    /*
     * ============================================================
     * Recording guards
     * ============================================================
     */

    //#if MC >= 12109

    //$$ private static boolean canRecordMutation() {
    //$$     return UndoRedo.enabled()
    //$$             && !UndoRuntimeState.isRestoring();
    //$$ }
    //$$
    //$$ private static boolean canRecordEntity(
    //$$         Entity entity
    //$$ ) {
    //$$     return canRecordMutation()
    //$$             && !(entity instanceof ServerPlayer);
    //$$ }

    //#endif
}