/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import carpetddsaddition.mixin.feature.undoredo.CompoundContainerAccessor;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.CompoundContainer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import java.util.Collections;
//$$ import java.util.IdentityHashMap;
//$$ import java.util.Set;
//#endif

public final class UndoContainerCompat {
    private UndoContainerCompat() {
    }

    //#if MC >= 12109
    //$$ public static void captureMenuContainersBefore(ServerPlayer player) {
    //$$     captureMenuContainers(player, true);
    //$$ }
    //$$
    //$$ public static void captureMenuContainersAfter(ServerPlayer player) {
    //$$     captureMenuContainers(player, false);
    //$$ }
    //$$
    //$$ public static void captureBefore(Container container) {
    //$$     captureSingle(container, true);
    //$$ }
    //$$
    //$$ public static void captureAfter(Container container) {
    //$$     captureSingle(container, false);
    //$$ }
    //$$
    //$$ private static void captureMenuContainers(
    //$$         ServerPlayer player,
    //$$         boolean before
    //$$ ) {
    //$$     if (UndoContext.current() == null || UndoManager.isRestoring()) {
    //$$         return;
    //$$     }
    //$$
    //$$     Set<Container> visited = Collections.newSetFromMap(
    //$$             new IdentityHashMap<>()
    //$$     );
    //$$
    //$$     for (Slot slot : player.containerMenu.slots) {
    //$$         captureContainer(slot.container, visited, before);
    //$$     }
    //$$ }
    //$$
    //$$ private static void captureSingle(
    //$$         Container container,
    //$$         boolean before
    //$$ ) {
    //$$     if (container == null
    //$$             || UndoContext.current() == null
    //$$             || UndoManager.isRestoring()) {
    //$$         return;
    //$$     }
    //$$
    //$$     Set<Container> visited = Collections.newSetFromMap(
    //$$             new IdentityHashMap<>()
    //$$     );
    //$$     captureContainer(container, visited, before);
    //$$ }
    //$$
    //$$ private static void captureContainer(
    //$$         Container container,
    //$$         Set<Container> visited,
    //$$         boolean before
    //$$ ) {
    //$$     if (container == null || !visited.add(container)) {
    //$$         return;
    //$$     }
    //$$
    //$$     if (container instanceof CompoundContainer compound) {
    //$$         CompoundContainerAccessor accessor =
    //$$                 (CompoundContainerAccessor) compound;
    //$$
    //$$         captureContainer(
    //$$                 accessor.dds$getFirstContainer(),
    //$$                 visited,
    //$$                 before
    //$$         );
    //$$         captureContainer(
    //$$                 accessor.dds$getSecondContainer(),
    //$$                 visited,
    //$$                 before
    //$$         );
    //$$         return;
    //$$     }
    //$$
    //$$     if (container instanceof BlockEntity blockEntity
    //$$             && blockEntity.getLevel() instanceof ServerLevel level) {
    //$$         if (before) {
    //$$             UndoMutationRecorder.recordBlockBefore(
    //$$                     level,
    //$$                     blockEntity.getBlockPos()
    //$$             );
    //$$         }
    //$$         else {
    //$$             UndoMutationRecorder.recordBlockAfter(
    //$$                     level,
    //$$                     blockEntity.getBlockPos()
    //$$             );
    //$$         }
    //$$         return;
    //$$     }
    //$$
    //$$     if (container instanceof Entity entity) {
    //$$         if (before) {
    //$$             UndoMutationRecorder.recordEntityBefore(entity);
    //$$         }
    //$$         else if (entity.isRemoved()) {
    //$$             UndoMutationRecorder.recordEntityRemoved(entity);
    //$$         }
    //$$         else {
    //$$             UndoMutationRecorder.recordEntityAfter(entity);
    //$$         }
    //$$     }
    //$$ }
    //#endif
}

