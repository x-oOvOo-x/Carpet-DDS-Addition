/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.EquipmentSlot;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.inventory.InventoryMenu;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import net.minecraft.world.item.ItemStack;
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#elseif MC >= 12104
//$$ import net.minecraft.resources.ResourceLocation;
//#else
//$$ import com.mojang.datafixers.util.Pair;
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//$$
//$$ final class DdsFakePlayerEquipmentSlot extends Slot {
//$$     private final LivingEntity owner;
//$$     private final EquipmentSlot equipmentSlot;
//#if MC >= 12111
//$$     private final Identifier emptyIcon;
//#else
//$$     private final ResourceLocation emptyIcon;
//#endif
//$$
//$$     DdsFakePlayerEquipmentSlot(Container container, LivingEntity owner, EquipmentSlot equipmentSlot, int index, int x, int y,
//#if MC >= 12111
//$$             Identifier emptyIcon
//#else
//$$             ResourceLocation emptyIcon
//#endif
//$$     ) {
//$$         super(container, index, x, y);
//$$         this.owner = owner; this.equipmentSlot = equipmentSlot; this.emptyIcon = emptyIcon;
//$$     }
//$$
//$$     @Override public void set(ItemStack stack) { setFromQuickMove(stack, getItem().copy()); }
//$$     void setFromQuickMove(ItemStack stack, ItemStack previous) { if (owner != null) owner.onEquipItem(equipmentSlot, previous, stack); super.set(stack); }
//$$     private boolean isArmorSlot() { return equipmentSlot != EquipmentSlot.OFFHAND; }
//$$     @Override public int getMaxStackSize() { return isArmorSlot() ? 1 : super.getMaxStackSize(); }
//$$
//$$     @Override public boolean mayPlace(ItemStack stack) {
//$$         return !isArmorSlot() ? super.mayPlace(stack) : owner == null || DdsFakePlayerCarpetCompat.canPlaceInEquipmentSlot(owner, stack, equipmentSlot);
//$$     }
//$$
//$$     @Override public boolean isActive() {
//$$         return !isArmorSlot() ? super.isActive() : owner == null || DdsFakePlayerCarpetCompat.canUseEquipmentSlot(owner, equipmentSlot);
//$$     }
//$$
//#if MC >= 12111
//$$     @Override public Identifier getNoItemIcon() { return emptyIcon; }
//#elseif MC >= 12104
//$$     @Override public ResourceLocation getNoItemIcon() { return emptyIcon; }
//#else
//$$     @Override public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() { return Pair.of(InventoryMenu.BLOCK_ATLAS, emptyIcon); }
//#endif
//$$ }
//#else
public final class DdsFakePlayerEquipmentSlot { private DdsFakePlayerEquipmentSlot() {} }
//#endif
