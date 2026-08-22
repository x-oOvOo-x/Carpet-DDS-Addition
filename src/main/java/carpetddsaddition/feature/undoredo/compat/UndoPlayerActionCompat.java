/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

import carpetddsaddition.feature.undoredo.UndoCause;

//#if MC >= 12109
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.Entity;

//#if MC >= 12111
//$$ import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
//#else
//$$ import net.minecraft.world.entity.vehicle.MinecartTNT;
//#endif

//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.BaseFireBlock;

//#if MC >= 260102
//$$ import net.minecraft.world.level.block.SpreadingSnowyBlock;
//#else
//$$ import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
//#endif

//$$ import net.minecraft.world.phys.AABB;
//$$ import net.minecraft.world.phys.BlockHitResult;
//#endif

public final class UndoPlayerActionCompat {

    private UndoPlayerActionCompat() {
    }


    /*
     * ============================================================
     * Block breaking
     * ============================================================
     */

    //#if MC >= 12109

    //$$ public static void beforeDestroyBlock(
    //$$         ServerPlayer player,
    //$$         BlockPos pos
    //$$ ) {
    //$$     UndoActionRecorder.beginPlayerAction(
    //$$             player,
    //$$             UndoCause.BREAK_BLOCK
    //$$     );
    //$$
    //$$     if (UndoContext.current() == null
    //$$             || UndoManager.isRestoring()
    //$$             || !(player.level()
    //$$             instanceof ServerLevel serverLevel)) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     long recordId =
    //$$             armSupportedTntMinecarts(
    //$$                     serverLevel,
    //$$                     pos,
    //$$                     0L
    //$$             );
    //$$
    //$$     armSupportedEntities(
    //$$             serverLevel,
    //$$             pos,
    //$$             recordId
    //$$     );
    //$$ }
    //$$
    //$$ public static void afterDestroyBlock(
    //$$         ServerPlayer player
    //$$ ) {
    //$$     UndoActionRecorder.endPlayerAction(
    //$$             player
    //$$     );
    //$$ }

    //#endif


    /*
     * ============================================================
     * Block interaction
     * ============================================================
     */

    //#if MC >= 12109

    //$$ public static void beforeUseItemOn(
    //$$         ServerPlayer player,
    //$$         Level level,
    //$$         BlockHitResult hit
    //$$ ) {
    //$$     UndoActionRecorder.beginPlayerAction(
    //$$             player,
    //$$             UndoCause.USE_BLOCK
    //$$     );
    //$$
    //$$     if (!(level
    //$$             instanceof ServerLevel serverLevel)) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     BlockPos clickedPos =
    //$$             hit.getBlockPos();
    //$$
    //$$     UndoMutationRecorder.recordBlockCandidate(
    //$$             serverLevel,
    //$$             clickedPos
    //$$     );
    //$$
    //$$     if (hit.getDirection() == Direction.UP
    //$$             && isSpreadableBlock(
    //$$             serverLevel,
    //$$             clickedPos
    //$$     )) {
    //$$
    //$$         UndoMutationRecorder.recordOcclusionCandidate(
    //$$                 serverLevel,
    //$$                 clickedPos
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ public static void afterUseItemOn(
    //$$         ServerPlayer player,
    //$$         Level level,
    //$$         ItemStack stack,
    //$$         BlockHitResult hit
    //$$ ) {
    //$$     if (level
    //$$             instanceof ServerLevel serverLevel
    //$$             && isFireStarter(stack)) {
    //$$
    //$$         armEntitiesInPlacedFire(
    //$$                 serverLevel,
    //$$                 hit.getBlockPos()
    //$$                         .relative(
    //$$                                 hit.getDirection()
    //$$                         )
    //$$         );
    //$$     }
    //$$
    //$$     UndoActionRecorder.endPlayerAction(
    //$$             player
    //$$     );
    //$$ }

    //#endif


    /*
     * ============================================================
     * Item interaction
     * ============================================================
     */

    //#if MC >= 12109

    //$$ public static void beforeUseItem(
    //$$         ServerPlayer player
    //$$ ) {
    //$$     UndoActionRecorder.beginPlayerAction(
    //$$             player,
    //$$             UndoCause.USE_ITEM
    //$$     );
    //$$ }
    //$$
    //$$ public static void afterUseItem(
    //$$         ServerPlayer player
    //$$ ) {
    //$$     UndoActionRecorder.endPlayerAction(
    //$$             player
    //$$     );
    //$$ }

    //#endif


    /*
     * ============================================================
     * Support-loss causal bridges
     * ============================================================
     */

    //#if MC >= 12109

    //$$ private static long armSupportedTntMinecarts(
    //$$         ServerLevel level,
    //$$         BlockPos brokenPos,
    //$$         long recordId
    //$$ ) {
    //$$     AABB supportArea =
    //$$             new AABB(
    //$$                     brokenPos.getX() - 0.75D,
    //$$                     brokenPos.getY() + 0.25D,
    //$$                     brokenPos.getZ() - 0.75D,
    //$$                     brokenPos.getX() + 1.75D,
    //$$                     brokenPos.getY() + 3.25D,
    //$$                     brokenPos.getZ() + 1.75D
    //$$             );
    //$$
    //$$     long originId =
    //$$             recordId;
    //$$
    //$$     for (MinecartTNT minecart
    //$$             : level.getEntitiesOfClass(
    //$$             MinecartTNT.class,
    //$$             supportArea
    //$$     )) {
    //$$
    //$$         if (minecart.isRemoved()
    //$$                 || minecart.isPrimed()
    //$$                 || !isSupportedByBrokenPosition(
    //$$                 minecart,
    //$$                 brokenPos
    //$$         )) {
    //$$
    //$$             continue;
    //$$         }
    //$$
    //$$         if (originId == 0L) {
    //$$             originId =
    //$$                     UndoAsyncOrigin.captureOriginId();
    //$$
    //$$             if (originId == 0L) {
    //$$                 break;
    //$$             }
    //$$         }
    //$$
    //$$         ((UndoTntMinecartAccess)
    //$$                 (Object) minecart)
    //$$                 .dds$armUndoFromSupport(
    //$$                         originId
    //$$                 );
    //$$     }
    //$$
    //$$     return originId;
    //$$ }
    //$$
    //$$ private static void armSupportedEntities(
    //$$         ServerLevel level,
    //$$         BlockPos brokenPos,
    //$$         long recordId
    //$$ ) {
    //$$     AABB supportArea =
    //$$             new AABB(
    //$$                     brokenPos.getX() - 0.01D,
    //$$                     brokenPos.getY() + 0.99D,
    //$$                     brokenPos.getZ() - 0.01D,
    //$$                     brokenPos.getX() + 1.01D,
    //$$                     brokenPos.getY() + 2.5D,
    //$$                     brokenPos.getZ() + 1.01D
    //$$             );
    //$$
    //$$     long originId =
    //$$             recordId;
    //$$
    //$$     for (Entity entity
    //$$             : level.getEntities(
    //$$             null,
    //$$             supportArea
    //$$     )) {
    //$$
    //$$         if (entity instanceof ServerPlayer
    //$$                 || entity instanceof MinecartTNT
    //$$                 || entity.isRemoved()
    //$$                 || !entity.onGround()
    //$$                 || !entity.getOnPos()
    //$$                 .equals(
    //$$                         brokenPos
    //$$                 )
    //$$                 || !(entity
    //$$                 instanceof UndoSpawnOriginAccess access)) {
    //$$
    //$$             continue;
    //$$         }
    //$$
    //$$         if (originId == 0L) {
    //$$             originId =
    //$$                     UndoAsyncOrigin.captureOriginId();
    //$$
    //$$             if (originId == 0L) {
    //$$                 break;
    //$$             }
    //$$         }
    //$$
    //$$         access.dds$setUndoSpawnOriginId(
    //$$                 originId
    //$$         );
    //$$     }
    //$$ }
    //$$
    //$$ private static boolean isSupportedByBrokenPosition(
    //$$         MinecartTNT minecart,
    //$$         BlockPos brokenPos
    //$$ ) {
    //$$     BlockPos railOrCartPos =
    //$$             minecart
    //$$                     .getCurrentBlockPosOrRailBelow();
    //$$
    //$$     return railOrCartPos.equals(
    //$$             brokenPos
    //$$     )
    //$$             || railOrCartPos
    //$$             .below()
    //$$             .equals(
    //$$                     brokenPos
    //$$             )
    //$$             || minecart
    //$$             .getOnPos()
    //$$             .equals(
    //$$                     brokenPos
    //$$             );
    //$$ }

    //#endif


    /*
     * ============================================================
     * Fire causal bridge
     * ============================================================
     */

    //#if MC >= 12109

    //$$ private static void armEntitiesInPlacedFire(
    //$$         ServerLevel level,
    //$$         BlockPos firePos
    //$$ ) {
    //$$     if (!(level
    //$$             .getBlockState(
    //$$                     firePos
    //$$             )
    //$$             .getBlock()
    //$$             instanceof BaseFireBlock)) {
    //$$
    //$$         return;
    //$$     }
    //$$
    //$$     AABB fireArea =
    //$$             new AABB(
    //$$                     firePos.getX(),
    //$$                     firePos.getY(),
    //$$                     firePos.getZ(),
    //$$                     firePos.getX() + 1.0D,
    //$$                     firePos.getY() + 1.0D,
    //$$                     firePos.getZ() + 1.0D
    //$$             );
    //$$
    //$$     long recordId =
    //$$             0L;
    //$$
    //$$     for (Entity entity
    //$$             : level.getEntities(
    //$$             null,
    //$$             fireArea
    //$$     )) {
    //$$
    //$$         if (entity instanceof ServerPlayer
    //$$                 || entity.isRemoved()
    //$$                 || entity.getRemainingFireTicks() > 0
    //$$                 || !(entity
    //$$                 instanceof UndoSpawnOriginAccess tickAccess)
    //$$                 || !(entity
    //$$                 instanceof UndoBurnOriginAccess burnAccess)) {
    //$$
    //$$             continue;
    //$$         }
    //$$
    //$$         if (recordId == 0L) {
    //$$             recordId =
    //$$                     UndoAsyncOrigin.captureOriginId();
    //$$
    //$$             if (recordId == 0L) {
    //$$                 break;
    //$$             }
    //$$         }
    //$$
    //$$         tickAccess.dds$setUndoSpawnOriginId(
    //$$                 recordId
    //$$         );
    //$$
    //$$         burnAccess.dds$setUndoBurnOriginId(
    //$$                 recordId
    //$$         );
    //$$     }
    //$$ }

    //#endif


    /*
     * ============================================================
     * Small predicates
     * ============================================================
     */

    //#if MC >= 12109

    //$$ private static boolean isFireStarter(
    //$$         ItemStack stack
    //$$ ) {
    //$$     return stack.is(
    //$$             Items.FLINT_AND_STEEL
    //$$     )
    //$$             || stack.is(
    //$$             Items.FIRE_CHARGE
    //$$     );
    //$$ }
    //$$
    //$$ private static boolean isSpreadableBlock(
    //$$         ServerLevel level,
    //$$         BlockPos pos
    //$$ ) {

    //#if MC >= 260102

    //$$     return level
    //$$             .getBlockState(
    //$$                     pos
    //$$             )
    //$$             .getBlock()
    //$$             instanceof SpreadingSnowyBlock;

    //#else

    //$$     return level
    //$$             .getBlockState(
    //$$                     pos
    //$$             )
    //$$             .getBlock()
    //$$             instanceof SpreadingSnowyDirtBlock;

    //#endif

    //$$ }

    //#endif
}
