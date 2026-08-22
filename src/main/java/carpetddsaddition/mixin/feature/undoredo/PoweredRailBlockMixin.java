/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoManager;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoTntMinecartAccess;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
    //#if MC >= 12111
    //$$ import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
    //#else
    //$$ import net.minecraft.world.entity.vehicle.MinecartTNT;
    //#endif
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.PoweredRailBlock;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.phys.AABB;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(PoweredRailBlock.class)
//$$ public abstract class PoweredRailBlockMixin {
//$$     @Inject(method = "updateState", at = @At("RETURN"))
//$$     private void dds$captureTntMinecartBeforeActivatorPower(BlockState oldState, Level level, BlockPos pos,
//$$                                                             Block neighbor, CallbackInfo ci) {
//$$         if (UndoContext.current() == null || UndoManager.isRestoring() || !(level instanceof ServerLevel serverLevel)
//$$                 || !oldState.is(Blocks.ACTIVATOR_RAIL) || oldState.getValue(PoweredRailBlock.POWERED)) return;
//$$         BlockState currentState = serverLevel.getBlockState(pos);
//$$         if (!currentState.is(Blocks.ACTIVATOR_RAIL) || !currentState.getValue(PoweredRailBlock.POWERED)) return;
//$$         long recordId = UndoAsyncOrigin.captureOriginId();
//$$         if (recordId == 0L) return;
//$$         AABB railArea = new AABB(pos.getX() - .75D, pos.getY() - .5D, pos.getZ() - .75D,
//$$                 pos.getX() + 1.75D, pos.getY() + 1.75D, pos.getZ() + 1.75D);
//$$         for (MinecartTNT minecart : serverLevel.getEntitiesOfClass(MinecartTNT.class, railArea)) {
//$$             if (minecart.isRemoved() || minecart.isPrimed() || !minecart.getCurrentBlockPosOrRailBelow().equals(pos)) continue;
//$$             ((UndoTntMinecartAccess) (Object) minecart).dds$armUndoFromActivator(recordId);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoPoweredRailTarget")
public abstract class PoweredRailBlockMixin {}
//#endif
