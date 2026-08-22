/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.directblockdrops.PendingDirectDropTicks;
import carpetddsaddition.feature.directblockdrops.ScheduledDirectDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
//#if MC < 11800
import net.minecraft.world.level.TickNextTickData;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    // Inventory.add mutates the stack; only a fully inserted drop cancels world spawning.
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$redirectDirectBlockDrop(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = DirectDropContext.getPlayer();
        if (player == null || !(entity instanceof ItemEntity)) return;
        ItemStack stack = ((ItemEntity) entity).getItem();
        if (stack.isEmpty()) return;
        //#if MC >= 11700
        //$$ player.getInventory().add(stack);
        //#else
        player.inventory.add(stack);
        //#endif
        if (stack.isEmpty()) cir.setReturnValue(true);
    }

    //#if MC >= 11800
    //$$ @Inject(method = "tickBlock", at = @At("HEAD"))
    //$$ private void carpetDDSAddition$beginAttributedBlockTick(BlockPos pos, Block block, CallbackInfo ci) {
    //$$     ServerLevel level = (ServerLevel) (Object) this;
    //$$     ServerPlayer player = PendingDirectDropTicks.consume(level, pos, block);
    //$$     if (player != null) ScheduledDirectDropContext.beginTick(level, pos, block, player);
    //$$ }
    //$$
    //$$ @Inject(method = "tickBlock", at = @At("RETURN"))
    //$$ private void carpetDDSAddition$endAttributedBlockTick(BlockPos pos, Block block, CallbackInfo ci) {
    //$$     ScheduledDirectDropContext.endTick((ServerLevel) (Object) this, pos, block);
    //$$ }
    //#else
    @Inject(method = "tickBlock", at = @At("HEAD"))
    private void carpetDDSAddition$beginAttributedBlockTickLegacy(TickNextTickData<Block> tick, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        BlockPos pos = tick.pos;
        Block block = tick.getType();
        ServerPlayer player = PendingDirectDropTicks.consume(level, pos, block);
        if (player != null) ScheduledDirectDropContext.beginTick(level, pos, block, player);
    }

    @Inject(method = "tickBlock", at = @At("RETURN"))
    private void carpetDDSAddition$endAttributedBlockTickLegacy(TickNextTickData<Block> tick, CallbackInfo ci) {
        ScheduledDirectDropContext.endTick((ServerLevel) (Object) this, tick.pos, tick.getType());
    }
    //#endif
}
