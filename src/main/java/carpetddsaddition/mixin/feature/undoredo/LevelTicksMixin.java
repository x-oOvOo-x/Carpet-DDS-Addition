/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncFreeze;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScheduledTickAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.ticks.LevelTicks;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import java.util.List;
//$$ import java.util.function.BiConsumer;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(LevelTicks.class)
//$$ public abstract class LevelTicksMixin<T> {
//$$     @Shadow @Final private List<ScheduledTick<T>> alreadyRunThisTick;
//$$
//$$     @Redirect(method = "runCollectedTicks(Ljava/util/function/BiConsumer;)V",
//$$             at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"))
//$$     @SuppressWarnings("unchecked")
//$$     private void dds$runWithUndoOrigin(BiConsumer<BlockPos, T> output, Object pos, Object type) {
//$$         if (alreadyRunThisTick.isEmpty()) {
//$$             output.accept((BlockPos) pos, (T) type);
//$$             return;
//$$         }
//$$         ScheduledTick<T> tick = alreadyRunThisTick.get(alreadyRunThisTick.size() - 1);
//$$         long recordId = ((UndoOriginAccess) (Object) tick).dds$getUndoOriginId();
//$$         if (recordId == 0L) {
//$$             output.accept((BlockPos) pos, (T) type);
//$$             return;
//$$         }
//$$         if (UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             if (UndoAsyncFreeze.isFrozen(recordId)) {
//$$                 LevelTicks<T> scheduler = (LevelTicks<T>) (Object) this;
//$$                 UndoAsyncFreeze.park(recordId,
//$$                         () -> dds$rescheduleWithPauseCompensation(scheduler, tick, recordId));
//$$             }
//$$             return;
//$$         }
//$$         if (dds$rescheduleWithPauseCompensation((LevelTicks<T>) (Object) this, tick, recordId)) return;
//$$         try (UndoScope ignored = UndoAsyncOrigin.enterRecord(recordId)) {
//$$             output.accept((BlockPos) pos, (T) type);
//$$         }
//$$     }
//$$
//$$     private boolean dds$rescheduleWithPauseCompensation(LevelTicks<T> scheduler, ScheduledTick<T> tick,
//$$                                                         long recordId) {
//$$         if (recordId == 0L) return false;
//$$         UndoScheduledTickAccess timing = (UndoScheduledTickAccess) (Object) tick;
//$$         long accumulatedPause = UndoAsyncFreeze.pauseTicks(recordId);
//$$         long shift = accumulatedPause - timing.dds$getUndoPauseBaseline();
//$$         if (shift <= 0L) return false;
//$$         ScheduledTick<T> shifted = new ScheduledTick<>(tick.type(), tick.pos(), tick.triggerTick() + shift,
//$$                 tick.priority(), tick.subTickOrder());
//$$         ((UndoOriginAccess) (Object) shifted).dds$setUndoOriginId(recordId);
//$$         ((UndoScheduledTickAccess) (Object) shifted).dds$setUndoPauseBaseline(accumulatedPause);
//$$         scheduler.schedule(shifted);
//$$         return true;
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoLevelTicksTarget")
public abstract class LevelTicksMixin {}
//#endif
