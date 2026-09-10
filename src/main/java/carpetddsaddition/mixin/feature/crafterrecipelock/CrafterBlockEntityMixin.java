/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.crafterrecipelock;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12004
//$$ import carpetddsaddition.feature.crafterrecipelock.CrafterRecipeLockAccess;
//$$ import carpetddsaddition.feature.crafterrecipelock.compat.CrafterRecipeLockCompat;
//$$ import net.minecraft.world.level.block.entity.CrafterBlockEntity;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.ValueInput;
//$$ import net.minecraft.world.level.storage.ValueOutput;
//#elseif MC >= 12005
//$$ import net.minecraft.core.HolderLookup;
//$$ import net.minecraft.nbt.CompoundTag;
//#elseif MC >= 12004
//$$ import net.minecraft.nbt.CompoundTag;
//#endif

//#if MC >= 12004
//$$ @Mixin(CrafterBlockEntity.class)
//$$ public abstract class CrafterBlockEntityMixin implements CrafterRecipeLockAccess {
//$$     @Unique private static final String DDS_LOCKED = "dds_recipe_locked";
//$$     @Unique private static final String DDS_TEMPLATE = "dds_recipe_template";
//$$     @Unique private static final String DDS_RECIPE = "dds_recipe_id";
//$$     @Unique private boolean dds$recipeLocked;
//$$     @Unique private int dds$recipeTemplateMask;
//$$     @Unique private String dds$recipeId = "";
//$$
//$$     @Override public boolean dds$isRecipeLocked() { return dds$recipeLocked; }
//$$     @Override public int dds$getRecipeTemplateMask() { return dds$recipeTemplateMask; }
//$$     @Override public String dds$getRecipeId() { return dds$recipeId; }
//$$
//$$     @Override
//$$     public void dds$setRecipeLock(boolean locked, int templateMask, String recipeId) {
//$$         dds$recipeLocked = locked;
//$$         dds$recipeTemplateMask = locked ? templateMask & 0x1FF : 0;
//$$         dds$recipeId = locked && recipeId != null ? recipeId : "";
//$$         ((CrafterBlockEntity)(Object)this).setChanged();
//$$     }
//$$
//$$     @Inject(method = "getRedstoneSignal", at = @At("HEAD"), cancellable = true)
//$$     private void dds$lockedComparator(CallbackInfoReturnable<Integer> cir) {
//$$         if (dds$recipeLocked)
//$$             cir.setReturnValue(CrafterRecipeLockCompat.comparatorSignal((CrafterBlockEntity)(Object)this, this));
//$$     }
//$$
//#if MC >= 12106
//$$     @Inject(method = "loadAdditional", at = @At("RETURN"))
//$$     private void dds$loadRecipeLock(ValueInput input, CallbackInfo ci) {
//$$         dds$recipeLocked = input.getBooleanOr(DDS_LOCKED, false);
//$$         dds$recipeTemplateMask = dds$recipeLocked ? input.getIntOr(DDS_TEMPLATE, 0) & 0x1FF : 0;
//$$         dds$recipeId = dds$recipeLocked ? input.getStringOr(DDS_RECIPE, "") : "";
//$$     }
//$$
//$$     @Inject(method = "saveAdditional", at = @At("RETURN"))
//$$     private void dds$saveRecipeLock(ValueOutput output, CallbackInfo ci) {
//$$         if (!dds$recipeLocked) return;
//$$         output.putBoolean(DDS_LOCKED, true);
//$$         output.putInt(DDS_TEMPLATE, dds$recipeTemplateMask);
//$$         output.putString(DDS_RECIPE, dds$recipeId);
//$$     }
//$$
//#elseif MC >= 12105
//$$     @Inject(method = "loadAdditional", at = @At("RETURN"))
//$$     private void dds$loadRecipeLock(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
//$$         dds$recipeLocked = tag.getBooleanOr(DDS_LOCKED, false);
//$$         dds$recipeTemplateMask = dds$recipeLocked ? tag.getIntOr(DDS_TEMPLATE, 0) & 0x1FF : 0;
//$$         dds$recipeId = dds$recipeLocked ? tag.getStringOr(DDS_RECIPE, "") : "";
//$$     }
//$$
//$$     @Inject(method = "saveAdditional", at = @At("RETURN"))
//$$     private void dds$saveRecipeLock(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
//$$         if (!dds$recipeLocked) return;
//$$         tag.putBoolean(DDS_LOCKED, true);
//$$         tag.putInt(DDS_TEMPLATE, dds$recipeTemplateMask);
//$$         tag.putString(DDS_RECIPE, dds$recipeId);
//$$     }
//$$
//#elseif MC >= 12005
//$$     @Inject(method = "loadAdditional", at = @At("RETURN"))
//$$     private void dds$loadRecipeLock(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
//$$         dds$recipeLocked = tag.getBoolean(DDS_LOCKED);
//$$         dds$recipeTemplateMask = dds$recipeLocked ? tag.getInt(DDS_TEMPLATE) & 0x1FF : 0;
//$$         dds$recipeId = dds$recipeLocked ? tag.getString(DDS_RECIPE) : "";
//$$     }
//$$
//$$     @Inject(method = "saveAdditional", at = @At("RETURN"))
//$$     private void dds$saveRecipeLock(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
//$$         if (!dds$recipeLocked) return;
//$$         tag.putBoolean(DDS_LOCKED, true);
//$$         tag.putInt(DDS_TEMPLATE, dds$recipeTemplateMask);
//$$         tag.putString(DDS_RECIPE, dds$recipeId);
//$$     }
//$$
//#else
//$$     @Inject(method = "load", at = @At("RETURN"))
//$$     private void dds$loadRecipeLock(CompoundTag tag, CallbackInfo ci) {
//$$         dds$recipeLocked = tag.getBoolean(DDS_LOCKED);
//$$         dds$recipeTemplateMask = dds$recipeLocked ? tag.getInt(DDS_TEMPLATE) & 0x1FF : 0;
//$$         dds$recipeId = dds$recipeLocked ? tag.getString(DDS_RECIPE) : "";
//$$     }
//$$
//$$     @Inject(method = "saveAdditional", at = @At("RETURN"))
//$$     private void dds$saveRecipeLock(CompoundTag tag, CallbackInfo ci) {
//$$         if (!dds$recipeLocked) return;
//$$         tag.putBoolean(DDS_LOCKED, true);
//$$         tag.putInt(DDS_TEMPLATE, dds$recipeTemplateMask);
//$$         tag.putString(DDS_RECIPE, dds$recipeId);
//$$     }
//#endif
//$$
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockBlockEntityTarget")
public abstract class CrafterBlockEntityMixin {}
//#endif