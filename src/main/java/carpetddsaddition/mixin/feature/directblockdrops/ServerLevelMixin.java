/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

import carpetddsaddition.feature.directblockdrops.DirectDropAttribution;
import carpetddsaddition.feature.directblockdrops.DirectDropTrace;
import carpetddsaddition.feature.directblockdrops.compat.DirectDropEntityCompat;
import carpetddsaddition.feature.directblockdrops.DirectDropContext;
import carpetddsaddition.feature.directblockdrops.DirectDropFallingContext;
import carpetddsaddition.feature.directblockdrops.DirectDropFallingOwner;
import carpetddsaddition.feature.directblockdrops.DirectDropScheduledTickOwner;
import carpetddsaddition.platform.minecraft.tick.DdsExecutingScheduledTick;
import carpetddsaddition.feature.directblockdrops.PendingDirectDropTicks;
import carpetddsaddition.feature.directblockdrops.PendingDirectDropSupportCascade;
import carpetddsaddition.feature.directblockdrops.ScheduledDirectDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
//#if MC < 11800
import net.minecraft.world.level.TickNextTickData;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    // Only a fully inserted stack cancels vanilla world spawning.
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$redirectDirectBlockDrop(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (entity instanceof FallingBlockEntity) {
            FallingBlockEntity falling = (FallingBlockEntity) entity;
            if (!(falling.getBlockState().getBlock() instanceof ScaffoldingBlock)) return;
            DirectDropAttribution.Owner owner = ScheduledDirectDropContext.ownerForFalling(level, DirectDropEntityCompat.position(entity));
            if (owner == null && !ScheduledDirectDropContext.hasTick()) {
                ServerPlayer direct = DirectDropContext.getPlayer();
                if (DirectDropAttribution.valid(direct, level, -1L))
                    owner = new DirectDropAttribution.Owner(direct, level.getGameTime());
            }
            DirectDropTrace.log("scaffolding.falling", "pos=" + DirectDropEntityCompat.position(entity)
                    + " attributed=" + (owner != null) + " tick=" + ScheduledDirectDropContext.hasTick());
            if (owner != null) ((DirectDropFallingOwner) falling)
                    .dds$setDirectDropOwner(owner.player(), level, owner.startTick(), owner.generation());
            return;
        }
        if (!(entity instanceof ItemEntity)) return;
        ItemEntity item = (ItemEntity) entity;
        // An active synchronous player scope already identifies the actor;
        // session/lifetime validation is reserved for delayed provenance.
        ServerPlayer player = DirectDropContext.getPlayer();
        if (player == null)
            player = ScheduledDirectDropContext.ownerForChainItem(
                    level, DirectDropEntityCompat.position(item), item.getItem().getItem());
        if (player == null) player = DirectDropFallingContext.ownerForItem(level, item);
        ItemStack stack = item.getItem();
        if (DirectDropTrace.enabled())
            DirectDropTrace.log("item.spawn", "item=" + stack.getItem() + " count=" + stack.getCount()
                    + " pos=" + DirectDropEntityCompat.position(item)
                    + " direct=" + (DirectDropContext.getPlayer() != null)
                    + " tick=" + ScheduledDirectDropContext.hasTick()
                    + " owner=" + (player != null));
        if (player == null || stack.isEmpty()) return;
        int before = stack.getCount();
        //#if MC >= 11700
        //$$ player.getInventory().add(stack);
        //#else
        player.inventory.add(stack);
        //#endif
        DirectDropTrace.log("item.insert", "item=" + stack.getItem()
                + " before=" + before + " remaining=" + stack.getCount());
        if (stack.isEmpty()) cir.setReturnValue(true);
    }

    //#if MC >= 12109
    //$$ @WrapMethod(method = "tickBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V")
    //$$ private void dds$scopedAcceptedTick(BlockPos pos, Block block, Operation<Void> original) {
    //$$     ServerLevel level = (ServerLevel) (Object) this;
    //$$     Object tick = DdsExecutingScheduledTick.current();
    //$$     DirectDropScheduledTickOwner source = tick instanceof DirectDropScheduledTickOwner
    //$$             ? (DirectDropScheduledTickOwner) tick : null;
    //$$     DirectDropAttribution.Owner fallback = source == null || source.dds$getDirectDropPlayer() == null
    //$$             ? PendingDirectDropSupportCascade.consume(level, pos, block) : null;
    //$$     ServerPlayer player = fallback != null ? fallback.player()
    //$$             : source == null ? null : source.dds$getDirectDropPlayer();
    //$$     long start = fallback != null ? fallback.startTick()
    //$$             : source == null ? 0L : source.dds$getDirectDropStartTick();
    //$$     long generation = fallback != null ? fallback.generation()
    //$$             : source == null ? 0L : source.dds$getDirectDropGeneration();
    //$$     try (ScheduledDirectDropContext.TickScope ignored =
    //$$                  ScheduledDirectDropContext.openTick(level, pos, block, player, start, generation)) {
    //$$         original.call(pos, block);
    //$$         PendingDirectDropSupportCascade.recordAfterTick(level, pos, block,
    //$$                 player, start, generation);
    //$$     }
    //$$ }
    //#elseif MC >= 11800
    //$$ @WrapMethod(method = "tickBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V")
    //$$ private void dds$scopedLegacyTick(BlockPos pos, Block block, Operation<Void> original) {
    //$$     ServerLevel level = (ServerLevel) (Object) this;
    //$$     DirectDropAttribution.Owner owner = PendingDirectDropTicks.consumeOwner(level, pos, block);
    //$$     if (owner == null) owner = PendingDirectDropSupportCascade.consume(level, pos, block);
    //$$     ServerPlayer player = owner == null ? null : owner.player();
    //$$     long start = owner == null ? 0L : owner.startTick();
    //$$     long generation = owner == null ? 0L : owner.generation();
    //$$     try (ScheduledDirectDropContext.TickScope ignored =
    //$$                  ScheduledDirectDropContext.openTick(level, pos, block, player, start, generation)) {
    //$$         original.call(pos, block);
    //$$         PendingDirectDropSupportCascade.recordAfterTick(level, pos, block,
    //$$                 player, start, generation);
    //$$     }
    //$$ }
    //#else
    @WrapMethod(method = "tickBlock(Lnet/minecraft/world/level/TickNextTickData;)V")
    private void dds$scopedLegacyTick(TickNextTickData<Block> tick, Operation<Void> original) {
        ServerLevel level = (ServerLevel) (Object) this;
        BlockPos pos = tick.pos;
        Block block = tick.getType();
        DirectDropAttribution.Owner owner = PendingDirectDropTicks.consumeOwner(level, pos, block);
        if (owner == null) owner = PendingDirectDropSupportCascade.consume(level, pos, block);
        ServerPlayer player = owner == null ? null : owner.player();
        long start = owner == null ? 0L : owner.startTick();
        long generation = owner == null ? 0L : owner.generation();
        try (ScheduledDirectDropContext.TickScope ignored =
                     ScheduledDirectDropContext.openTick(level, pos, block, player, start, generation)) {
            original.call(tick);
            PendingDirectDropSupportCascade.recordAfterTick(level, pos, block,
                    player, start, generation);
        }
    }
    //#endif
}
