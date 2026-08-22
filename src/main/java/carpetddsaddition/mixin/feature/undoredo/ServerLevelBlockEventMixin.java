/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.UndoRedo;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncFreeze;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.BlockEventData;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ServerLevel.class)
//$$ public abstract class ServerLevelBlockEventMixin {
//$$     @Redirect(method = "blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V",
//$$             at = @At(value = "INVOKE",
//$$                     target = "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;add(Ljava/lang/Object;)Z", remap = false))
//$$     private boolean dds$addBlockEventWithUndoOrigin(ObjectLinkedOpenHashSet<BlockEventData> events, Object eventObject) {
//$$         BlockEventData event = (BlockEventData) eventObject;
//$$         if (!events.add(event)) return false;
//$$         UndoOriginAccess access = (UndoOriginAccess) (Object) event;
//$$         if (access.dds$getUndoOriginId() != 0L || !UndoRedo.enabled()) return true;
//$$         long recordId = UndoAsyncFreeze.forcedOriginId();
//$$         if (recordId == 0L) recordId = UndoAsyncOrigin.captureOriginId();
//$$         if (recordId != 0L) access.dds$setUndoOriginId(recordId);
//$$         return true;
//$$     }
//$$
//$$     @Redirect(method = "doBlockEvent(Lnet/minecraft/world/level/BlockEventData;)Z",
//$$             at = @At(value = "INVOKE",
//$$                     target = "Lnet/minecraft/world/level/block/state/BlockState;triggerEvent("
//$$                             + "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z"))
//$$     private boolean dds$runBlockEventWithUndoOrigin(BlockState state, Level level, BlockPos pos,
//$$                                                     int paramA, int paramB, BlockEventData event) {
//$$         long recordId = ((UndoOriginAccess) (Object) event).dds$getUndoOriginId();
//$$         if (recordId == 0L) return state.triggerEvent(level, pos, paramA, paramB);
//$$         if (UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) {
//$$             if (UndoAsyncFreeze.isFrozen(recordId) && level instanceof ServerLevel serverLevel) {
//$$                 BlockPos replayPos = event.pos().immutable();
//$$                 Block replayBlock = event.block();
//$$                 UndoAsyncFreeze.park(recordId, () -> UndoAsyncFreeze.withForcedOrigin(recordId,
//$$                         () -> serverLevel.blockEvent(replayPos, replayBlock, paramA, paramB)));
//$$             }
//$$             return false;
//$$         }
//$$         try (UndoScope ignored = UndoAsyncOrigin.enterRecord(recordId)) {
//$$             return state.triggerEvent(level, pos, paramA, paramB);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerLevelBlockEventTarget")
public abstract class ServerLevelBlockEventMixin {}
//#endif