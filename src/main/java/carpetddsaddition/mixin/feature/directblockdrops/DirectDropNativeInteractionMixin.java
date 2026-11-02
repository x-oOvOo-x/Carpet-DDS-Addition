/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectBlockDrops;
import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.directblockdrops.DirectDropTrace;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerGameMode.class)
public abstract class DirectDropNativeInteractionMixin {
    /**
     * Scope vanilla item-producing block interactions, without changing their
     * loot, durability, block state or DDS crop/replant behavior.
     */
    @WrapMethod(method = "useItemOn")
    private InteractionResult dds$collectVanillaBerries(
            //#if MC >= 11601
            //$$ ServerPlayer player,
            //#else
            Player player,
            //#endif
            Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult,
            Operation<InteractionResult> original) {
        if (!(player instanceof ServerPlayer) || !DirectBlockDrops.enabled()
                || !dds$isNativeDropInteraction(
                        level.getBlockState(hitResult.getBlockPos()).getBlock(), stack))
            return original.call(player, level, stack, hand, hitResult);
        DirectDropTrace.log("block.interact", "pos=" + hitResult.getBlockPos()
                + " item=" + stack.getItem());
        try (DirectDropContext.Scope ignored = DirectDropContext.enter((ServerPlayer) player)) {
            return original.call(player, level, stack, hand, hitResult);
        }
    }

    private static boolean dds$isNativeDropInteraction(Block block, ItemStack held) {
        if (block == Blocks.SWEET_BERRY_BUSH) return true;
        //#if MC >= 11700
        //$$ if (block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT) return true;
        //#endif
        if (held.getItem() != Items.SHEARS) return false;
        if (block == Blocks.PUMPKIN) return true;
        //#if MC >= 11500
        return block == Blocks.BEEHIVE || block == Blocks.BEE_NEST;
        //#else
        //$$ return false;
        //#endif
    }

}
