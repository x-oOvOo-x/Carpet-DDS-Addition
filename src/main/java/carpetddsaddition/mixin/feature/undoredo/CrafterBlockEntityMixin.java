/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.CrafterBlock;
//$$ import net.minecraft.world.level.block.entity.CrafterBlockEntity;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(CrafterBlockEntity.class)
//$$ public abstract class CrafterBlockEntityMixin implements UndoOriginAccess {
//$$     @Unique private long dds$undoOriginId;
//$$     @Unique private static final ThreadLocal<UndoScope> dds$undoTickScope = new ThreadLocal<>();
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) { dds$undoOriginId = recordId; }
//$$
//$$     @Inject(method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
//$$             + "Lnet/minecraft/world/level/block/state/BlockState;"
//$$             + "Lnet/minecraft/world/level/block/entity/CrafterBlockEntity;)V", at = @At("HEAD"))
//$$     private static void dds$beforeCrafterTick(Level level, BlockPos pos, BlockState state,
//$$                                               CrafterBlockEntity blockEntity, CallbackInfo ci) {
//$$         dds$undoTickScope.remove();
//$$         if (UndoManager.isRestoring() || !(level instanceof ServerLevel serverLevel)) return;
//$$         UndoOriginAccess access = (UndoOriginAccess) (Object) blockEntity;
//$$         long recordId = access.dds$getUndoOriginId();
//$$         if (recordId == 0L || UndoAsyncOrigin.shouldSuppressAsyncOrigin(recordId)) return;
//$$         UndoScope scope = UndoAsyncOrigin.enterRecord(recordId);
//$$         if (!scope.isActive()) return;
//$$         dds$undoTickScope.set(scope);
//$$         UndoMutationRecorder.recordBlockBefore(serverLevel, pos);
//$$     }
//$$
//$$     @Inject(method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
//$$             + "Lnet/minecraft/world/level/block/state/BlockState;"
//$$             + "Lnet/minecraft/world/level/block/entity/CrafterBlockEntity;)V", at = @At("RETURN"))
//$$     private static void dds$afterCrafterTick(Level level, BlockPos pos, BlockState state,
//$$                                              CrafterBlockEntity blockEntity, CallbackInfo ci) {
//$$         UndoScope scope = dds$undoTickScope.get();
//$$         if (scope == null) return;
//$$         try {
//$$             if (UndoManager.isRestoring() || !(level instanceof ServerLevel serverLevel)) return;
//$$             UndoOriginAccess access = (UndoOriginAccess) (Object) blockEntity;
//$$             BlockState currentState = level.getBlockState(pos);
//$$             boolean stillCrafting = currentState.hasProperty(CrafterBlock.CRAFTING)
//$$                     && currentState.getValue(CrafterBlock.CRAFTING);
//$$             if (!stillCrafting) access.dds$setUndoOriginId(0L);
//$$             UndoMutationRecorder.recordBlockAfter(serverLevel, pos);
//$$         } finally {
//$$             dds$undoTickScope.remove();
//$$             scope.close();
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCrafterBlockEntityTarget")
public abstract class CrafterBlockEntityMixin {}
//#endif
