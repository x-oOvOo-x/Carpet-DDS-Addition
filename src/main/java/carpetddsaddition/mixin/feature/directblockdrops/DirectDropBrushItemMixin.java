/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

//#if MC >= 11904
//$$ import carpetddsaddition.feature.directblockdrops.DirectBlockDrops;
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropContext;
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropTrace;
//$$ import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.item.BrushItem;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.phys.BlockHitResult;
//$$ import net.minecraft.world.phys.HitResult;
//$$ import org.spongepowered.asm.mixin.Mixin;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 11904
//$$ @Mixin(BrushItem.class)
//$$ public abstract class DirectDropBrushItemMixin {
//$$     /** Archaeology loot is released during repeated use, not the initial right click. */
//$$     @WrapMethod(method = "onUseTick")
//$$     private void dds$collectBrushedLoot(Level level, LivingEntity actor, ItemStack stack,
//$$                                         int remaining, Operation<Void> original) {
//$$         if (!DirectBlockDrops.enabled() || !(actor instanceof ServerPlayer)) {
//$$             original.call(level, actor, stack, remaining);
//$$             return;
//$$         }
//$$         HitResult hit = actor.pick(5.0D, 0.0F, false);
//$$         if (!(hit instanceof BlockHitResult)) {
//$$             original.call(level, actor, stack, remaining);
//$$             return;
//$$         }
//$$         BlockPos pos = ((BlockHitResult) hit).getBlockPos();
//$$         Block block = level.getBlockState(pos).getBlock();
    //#if MC >= 12000
    //$$     boolean suspicious = block == Blocks.SUSPICIOUS_SAND || block == Blocks.SUSPICIOUS_GRAVEL;
    //#else
    //$$     boolean suspicious = block == Blocks.SUSPICIOUS_SAND;
    //#endif
//$$         if (!suspicious) {
//$$             original.call(level, actor, stack, remaining);
//$$             return;
//$$         }
//$$         DirectDropTrace.log("archaeology.brush", "pos=" + pos);
//$$         try (DirectDropContext.Scope ignored = DirectDropContext.enter((ServerPlayer) actor)) {
//$$             original.call(level, actor, stack, remaining);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DirectDropBrushTarget")
public abstract class DirectDropBrushItemMixin {}
//#endif
