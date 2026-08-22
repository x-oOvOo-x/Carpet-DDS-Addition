/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContainerCompat;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoHopperAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.item.ItemEntity;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.entity.Hopper;
//$$ import net.minecraft.world.level.block.entity.HopperBlockEntity;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import java.util.function.BooleanSupplier;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(HopperBlockEntity.class)
//$$ public abstract class HopperBlockEntityMixin implements UndoHopperAccess {
//$$     @Unique private static final ThreadLocal<UndoScope> dds$undoTransferScope = new ThreadLocal<>();
//$$     @Unique private long dds$undoOriginId, dds$undoTransferDeadline;
//$$
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) {
//$$         dds$undoOriginId = recordId;
//$$         if (recordId == 0L) dds$undoTransferDeadline = 0L;
//$$     }
//$$     @Override public long dds$getUndoTransferDeadline() { return dds$undoTransferDeadline; }
//$$     @Override public void dds$setUndoTransferDeadline(long gameTime) { dds$undoTransferDeadline = gameTime; }
//$$
//$$     @Inject(method = "tryMoveItems", at = @At("HEAD"))
//$$     private static void dds$beforeTryMoveItems(Level level, BlockPos pos, BlockState state,
//$$                                                HopperBlockEntity hopper, BooleanSupplier canMoveItems,
//$$                                                CallbackInfoReturnable<Boolean> cir) {
//$$         dds$undoTransferScope.remove();
//$$         UndoHopperAccess access = (UndoHopperAccess) (Object) hopper;
//$$         if (UndoContext.current() == null && !UndoManager.isRestoring()) {
//$$             long recordId = access.dds$getUndoOriginId();
//$$             if (recordId != 0L) {
//$$                 long deadline = access.dds$getUndoTransferDeadline();
//$$                 if (deadline == 0L) {
//$$                     deadline = level.getGameTime() + IDLE_GRACE_TICKS;
//$$                     access.dds$setUndoTransferDeadline(deadline);
//$$                 }
//$$                 if (level.getGameTime() > deadline) dds$expireOrigin(hopper, access, recordId);
//$$                 else if (UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) access.dds$setUndoOriginId(0L);
//$$                 else {
//$$                     UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$                     if (scope.isActive()) dds$undoTransferScope.set(scope);
//$$                     else access.dds$setUndoOriginId(0L);
//$$                 }
//$$             }
//$$         }
//$$         if (UndoContext.current() != null && !UndoManager.isRestoring()) UndoContainerCompat.captureBefore(hopper);
//$$     }
//$$
//$$     @Inject(method = "tryMoveItems", at = @At("RETURN"))
//$$     private static void dds$afterTryMoveItems(Level level, BlockPos pos, BlockState state,
//$$                                               HopperBlockEntity hopper, BooleanSupplier canMoveItems,
//$$                                               CallbackInfoReturnable<Boolean> cir) {
//$$         try {
//$$             if (UndoContext.current() != null && !UndoManager.isRestoring()) {
//$$                 if (cir.getReturnValue()) {
//$$                     UndoHopperAccess access = (UndoHopperAccess) (Object) hopper;
//$$                     if (access.dds$getUndoOriginId() != 0L)
//$$                         access.dds$setUndoTransferDeadline(level.getGameTime() + IDLE_GRACE_TICKS);
//$$                 }
//$$                 UndoContainerCompat.captureAfter(hopper);
//$$             }
//$$         } finally {
//$$             UndoScope scope = dds$undoTransferScope.get();
//$$             dds$undoTransferScope.remove();
//$$             if (scope != null) scope.close();
//$$         }
//$$     }
//$$
//$$     @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;"
//$$             + "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;",
//$$             at = @At("HEAD"))
//$$     private static void dds$beforeContainerTransfer(Container source, Container destination, ItemStack stack,
//$$                                                     Direction direction, CallbackInfoReturnable<ItemStack> cir) {
//$$         UndoContainerCompat.captureBefore(source);
//$$         UndoContainerCompat.captureBefore(destination);
//$$     }
//$$     @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;"
//$$             + "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;",
//$$             at = @At("RETURN"))
//$$     private static void dds$afterContainerTransfer(Container source, Container destination, ItemStack stack,
//$$                                                    Direction direction, CallbackInfoReturnable<ItemStack> cir) {
//$$         UndoContainerCompat.captureAfter(source);
//$$         UndoContainerCompat.captureAfter(destination);
//$$     }
//$$
//$$     @Inject(method = "tryTakeInItemFromSlot", at = @At("HEAD"))
//$$     private static void dds$beforeTakeFromContainer(Hopper hopper, Container source, int slot, Direction direction,
//$$                                                     CallbackInfoReturnable<Boolean> cir) {
//$$         UndoContainerCompat.captureBefore(source);
//$$         if (hopper instanceof Container destination) UndoContainerCompat.captureBefore(destination);
//$$     }
//$$     @Inject(method = "tryTakeInItemFromSlot", at = @At("RETURN"))
//$$     private static void dds$afterTakeFromContainer(Hopper hopper, Container source, int slot, Direction direction,
//$$                                                    CallbackInfoReturnable<Boolean> cir) {
//$$         UndoContainerCompat.captureAfter(source);
//$$         if (hopper instanceof Container destination) UndoContainerCompat.captureAfter(destination);
//$$     }
//$$
//$$     @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z",
//$$             at = @At("HEAD"))
//$$     private static void dds$beforeItemEntityPickup(Container destination, ItemEntity itemEntity,
//$$                                                    CallbackInfoReturnable<Boolean> cir) {
//$$         UndoContainerCompat.captureBefore(destination);
//$$         UndoMutationRecorder.recordEntityBefore(itemEntity);
//$$     }
//$$     @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z",
//$$             at = @At("RETURN"))
//$$     private static void dds$afterItemEntityPickup(Container destination, ItemEntity itemEntity,
//$$                                                   CallbackInfoReturnable<Boolean> cir) {
//$$         UndoContainerCompat.captureAfter(destination);
//$$         if (itemEntity.isRemoved()) UndoMutationRecorder.recordEntityRemoved(itemEntity);
//$$         else UndoMutationRecorder.recordEntityAfter(itemEntity);
//$$     }
//$$
//$$     @Unique
//$$     private static void dds$expireOrigin(HopperBlockEntity hopper, UndoHopperAccess access, long recordId) {
//$$         if (UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId) || !(hopper.getLevel() instanceof ServerLevel)) {
//$$             access.dds$setUndoOriginId(0L);
//$$             return;
//$$         }
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$         if (!scope.isActive()) {
//$$             access.dds$setUndoOriginId(0L);
//$$             return;
//$$         }
//$$         try (scope) {
//$$             access.dds$setUndoOriginId(0L);
//$$             UndoContainerCompat.captureAfter(hopper);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoHopperBlockEntityTarget")
public abstract class HopperBlockEntityMixin {}
//#endif
