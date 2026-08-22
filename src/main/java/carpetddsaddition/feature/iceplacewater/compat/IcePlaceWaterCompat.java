/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.iceplacewater.compat;

import carpetddsaddition.feature.iceplacewater.IcePlaceWater;
import net.minecraft.core.BlockPos;
//#if MC >= 12111
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import net.minecraft.network.protocol.game.ClientboundSoundPacket;
//#endif
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//#if MC >= 11602
//$$ import net.minecraft.world.item.context.UseOnContext;
//#else
import net.minecraft.world.item.UseOnContext;
//#endif
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
//#if MC >= 11700
//$$ import net.minecraft.world.level.block.LayeredCauldronBlock;
//#else
import net.minecraft.world.level.block.CauldronBlock;
//#endif
import net.minecraft.world.level.block.state.BlockState;

/** Minecraft-version adapter and interaction implementation for icePlaceWater. */
@SuppressWarnings("deprecation")
public final class IcePlaceWaterCompat {
    private IcePlaceWaterCompat() {}

    /** Returns null to keep vanilla BlockItem handling. */
    public static InteractionResult tryPlace(BlockItem item, UseOnContext context) {
        Player player = context.getPlayer();
        if ((Object) item != Items.ICE || !IcePlaceWater.enabled() || player == null || player.isShiftKeyDown()) return null;
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        if (fillWaterCauldron(level, clickedPos)) {
            consumeIce(player, stack);
            playWaterBucketEmpty(player, clickedPos);
            return InteractionResult.SUCCESS;
        }

        BucketItem waterBucket = (BucketItem) Items.WATER_BUCKET;
        BlockPos placedPos = clickedPos;
        boolean placed = emptyWaterBucket(waterBucket, player, level, placedPos);
        if (!placed) {
            placedPos = clickedPos.relative(context.getClickedFace());
            placed = emptyWaterBucket(waterBucket, player, level, placedPos);
        }
        if (!placed) return InteractionResult.FAIL;
        consumeIce(player, stack);
        playWaterBucketEmpty(player, placedPos);
        return InteractionResult.SUCCESS;
    }

    private static boolean fillWaterCauldron(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        //#if MC >= 11700
        //$$ if (state.getBlock() != Blocks.CAULDRON && state.getBlock() != Blocks.WATER_CAULDRON) return false;
        //$$ level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState()
        //$$         .setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL));
        //$$ return true;
        //#else
        if (state.getBlock() != Blocks.CAULDRON) return false;
        level.setBlockAndUpdate(pos, state.setValue(CauldronBlock.LEVEL, 3));
        return true;
        //#endif
    }

    private static boolean emptyWaterBucket(BucketItem bucket, Player player, Level level, BlockPos pos) {
        //#if MC >= 11700
        //$$ return bucket.emptyContents(player, level, pos, null);
        //#else
        return bucket.emptyBucket(player, level, pos, null);
        //#endif
    }

    private static void consumeIce(Player player, ItemStack stack) { if (!player.isCreative()) stack.shrink(1); }

    private static void playWaterBucketEmpty(Player player, BlockPos pos) {
        if (!(player instanceof ServerPlayer)) return;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        //#if MC >= 12111
        //$$ serverPlayer.connection.send(new ClientboundSoundPacket(
        //$$         BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BUCKET_EMPTY), SoundSource.BLOCKS,
        //$$         pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.0F, 1.0F, 0L));
        //#else
        serverPlayer.playNotifySound(SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        //#endif
    }
}
