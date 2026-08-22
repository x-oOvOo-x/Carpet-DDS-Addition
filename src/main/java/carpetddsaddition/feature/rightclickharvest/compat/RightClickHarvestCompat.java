/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.rightclickharvest.compat;

import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.rightclickharvest.RightClickHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RightClickHarvestCompat {
    private RightClickHarvestCompat() {}

    public static boolean tryHarvest(Player player, Level level, BlockPos pos, ItemStack tool) {
        if (!RightClickHarvest.enabled() || player.isSpectator() || !(level instanceof ServerLevel)) return false;
        BlockState state = level.getBlockState(pos);
        Item replantItem = getReplantItem(state.getBlock());
        if (replantItem == null) return false;
        IntegerProperty ageProperty = getAgeProperty(state);
        if (ageProperty == null || state.getValue(ageProperty) < Collections.max(ageProperty.getPossibleValues())) return false;
        List<ItemStack> drops = new ArrayList<ItemStack>(Block.getDrops(state, (ServerLevel) level, pos, null, player, tool));
        boolean replant = consumeReplantItem(drops, replantItem);
        updateCrop(level, pos, state, ageProperty, replant);
        // directBlockDrops only changes the destination of already-calculated drops.
        if (player instanceof ServerPlayer) {
            DirectDropContext.push((ServerPlayer) player);
            try { dropItems(level, pos, drops); }
            finally { DirectDropContext.pop(); }
        } else dropItems(level, pos, drops);
        return true;
    }

    private static Item getReplantItem(Block block) {
        if (block == Blocks.WHEAT) return Items.WHEAT_SEEDS;
        if (block == Blocks.CARROTS) return Items.CARROT;
        if (block == Blocks.POTATOES) return Items.POTATO;
        if (block == Blocks.BEETROOTS) return Items.BEETROOT_SEEDS;
        if (block == Blocks.NETHER_WART) return Items.NETHER_WART;
        return block == Blocks.COCOA ? Items.COCOA_BEANS : null;
    }

    private static IntegerProperty getAgeProperty(BlockState state) {
        for (Property<?> property : state.getProperties())
            if (property instanceof IntegerProperty && "age".equals(property.getName())) return (IntegerProperty) property;
        return null;
    }

    private static boolean consumeReplantItem(List<ItemStack> drops, Item replantItem) {
        for (ItemStack stack : drops) if (!stack.isEmpty() && stack.getItem() == replantItem) {
            stack.shrink(1);
            return true;
        }
        return false;
    }

    private static void updateCrop(Level level, BlockPos pos, BlockState state, IntegerProperty ageProperty, boolean replant) {
        BlockState newState = replant ? state.setValue(ageProperty, Collections.min(ageProperty.getPossibleValues())) : Blocks.AIR.defaultBlockState();
        level.setBlock(pos, newState, 3);
        SoundType sound = state.getSoundType();
        level.playSound(null, pos, replant ? sound.getPlaceSound() : sound.getBreakSound(), SoundSource.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }

    private static void dropItems(Level level, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack stack : drops) if (!stack.isEmpty()) Block.popResource(level, pos, stack);
    }
}
