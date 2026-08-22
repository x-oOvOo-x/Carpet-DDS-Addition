/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$
//$$ final class DdsMappedContainer implements Container {
//$$     private final Container delegate;
//$$     private final int[] indices;
//$$     DdsMappedContainer(Container delegate, int[] indices) { this.delegate = delegate; this.indices = indices.clone(); }
//$$     @Override public int getContainerSize() { return indices.length; }
//$$     @Override public boolean isEmpty() { for (int index : indices) if (!delegate.getItem(index).isEmpty()) return false; return true; }
//$$     @Override public ItemStack getItem(int slot) { return delegate.getItem(indices[slot]); }
//$$     @Override public ItemStack removeItem(int slot, int count) { return delegate.removeItem(indices[slot], count); }
//$$     @Override public ItemStack removeItemNoUpdate(int slot) { return delegate.removeItemNoUpdate(indices[slot]); }
//$$     @Override public void setItem(int slot, ItemStack stack) { delegate.setItem(indices[slot], stack); }
//$$     @Override public int getMaxStackSize() { return delegate.getMaxStackSize(); }
//#if MC >= 12006
//$$     @Override public int getMaxStackSize(ItemStack stack) { return delegate.getMaxStackSize(stack); }
//#endif
//$$     @Override public void setChanged() { delegate.setChanged(); }
//$$     @Override public boolean stillValid(Player player) { return delegate.stillValid(player); }
//$$     @Override public boolean canPlaceItem(int slot, ItemStack stack) { return delegate.canPlaceItem(indices[slot], stack); }
//$$     @Override public void clearContent() { for (int index : indices) delegate.setItem(index, ItemStack.EMPTY); }
//$$ }
//#else
public final class DdsMappedContainer { private DdsMappedContainer() {} }
//#endif
