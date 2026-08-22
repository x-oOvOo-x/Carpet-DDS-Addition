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
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.BlockEntityTicker;
//$$ import net.minecraft.world.level.block.entity.BlockEntityType;
//$$ import net.minecraft.world.level.block.piston.MovingPistonBlock;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(MovingPistonBlock.class)
//$$ public abstract class MovingPistonBlockMixin {
//$$     @Inject(method = "getTicker(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;"
//$$             + "Lnet/minecraft/world/level/block/entity/BlockEntityType;)Lnet/minecraft/world/level/block/entity/BlockEntityTicker;",
//$$             at = @At("RETURN"), cancellable = true)
//$$     private <T extends BlockEntity> void dds$wrapPistonTicker(Level level, BlockState blockState, BlockEntityType<T> type,
//$$                                                              CallbackInfoReturnable<BlockEntityTicker<T>> cir) {
//$$         BlockEntityTicker<T> original = cir.getReturnValue();
//$$         if (original == null) return;
//$$         cir.setReturnValue((tickLevel, pos, tickState, blockEntity) -> {
//$$             long recordId = blockEntity instanceof UndoOriginAccess access ? access.dds$getUndoOriginId() : 0L;
//$$             if (UndoAsyncFreeze.isFrozen(recordId)) return;
//$$             try (UndoScope ignored = UndoAsyncOrigin.enterRecord(recordId)) {
//$$                 original.tick(tickLevel, pos, tickState, blockEntity);
//$$             }
//$$         });
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoMovingPistonBlockTarget")
public abstract class MovingPistonBlockMixin {}
//#endif
