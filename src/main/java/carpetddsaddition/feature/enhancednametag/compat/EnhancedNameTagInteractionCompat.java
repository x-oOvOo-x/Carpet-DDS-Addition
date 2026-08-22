/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.enhancednametag.compat;

import carpetddsaddition.feature.enhancednametag.BlockAnnotationStore;
import carpetddsaddition.feature.enhancednametag.EnhancedNameTag;
//#if MC >= 11904
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.Items;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.state.BlockState;
//#endif

/** Version-gated interaction adapter keeping Enhanced Name Tag Mixins as thin hooks. */
public final class EnhancedNameTagInteractionCompat {
    private EnhancedNameTagInteractionCompat() {}

    //#if MC >= 11904
    //$$ public static boolean tryUseOnEntity(ItemStack stack, Player player, LivingEntity target) {
    //$$     if (!EnhancedNameTag.editingEnabled() || !EnhancedNameTagCompat.hasCustomName(stack)) return false;
    //$$     if (!EnhancedNameTagCompat.getLevel(player).isClientSide()) {
    //$$         target.setCustomName(EnhancedNameText.parse(EnhancedNameTagCompat.getCustomNameString(stack)));
    //$$         if (!player.getAbilities().instabuild) stack.shrink(1);
    //$$     }
    //$$     return true;
    //$$ }
    //$$
    //$$ public static boolean tryUseOnBlock(ServerPlayer player, Level level, ItemStack stack, BlockPos pos) {
    //$$     if (!EnhancedNameTag.editingEnabled() || !player.isShiftKeyDown() || !stack.is(Items.NAME_TAG)
    //$$             || !EnhancedNameTagCompat.hasCustomName(stack)) return false;
    //$$     BlockState state = level.getBlockState(pos);
    //$$     if (state.isAir()) return false;
    //$$     BlockAnnotationStore.put(EnhancedNameTagCompat.getDimensionId(level), pos,
    //$$             BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString(), EnhancedNameTagCompat.getCustomNameString(stack));
    //$$     if (!player.getAbilities().instabuild) stack.shrink(1);
    //$$     return true;
    //$$ }
    //$$
    //$$ public static void onBlockDestroyed(ServerLevel level, BlockPos pos, boolean destroyed) {
    //$$     if (destroyed) BlockAnnotationStore.remove(EnhancedNameTagCompat.getDimensionId(level), pos);
    //$$ }
    //#endif
}
