/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import java.util.ArrayList;
//$$ import java.util.List;
//#endif

public final class PlayerInventorySnapshot {
    //#if MC >= 12109
    //$$ private final List<ItemStack> items;
    //$$ private final int selectedSlot;
    //$$ private final ItemStack carried;
    //$$
    //$$ private PlayerInventorySnapshot(
    //$$         List<ItemStack> items,
    //$$         int selectedSlot,
    //$$         ItemStack carried
    //$$ ) {
    //$$     this.items = items;
    //$$     this.selectedSlot = selectedSlot;
    //$$     this.carried = carried;
    //$$ }
    //$$
    //$$ public static PlayerInventorySnapshot capture(ServerPlayer player) {
    //$$     Inventory inventory = player.getInventory();
    //$$     List<ItemStack> items = new ArrayList<>(inventory.getContainerSize());
    //$$     for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
    //$$         items.add(inventory.getItem(slot).copy());
    //$$     }
    //$$
    //$$     return new PlayerInventorySnapshot(
    //$$             items,
    //$$             inventory.getSelectedSlot(),
    //$$             player.containerMenu.getCarried().copy()
    //$$     );
    //$$ }
    //$$
    //$$ public boolean matchesCurrent(ServerPlayer player) {
    //$$     Inventory inventory = player.getInventory();
    //$$     if (inventory.getContainerSize() != items.size()
    //$$             || inventory.getSelectedSlot() != selectedSlot
    //$$             || !ItemStack.matches(
    //$$                     player.containerMenu.getCarried(),
    //$$                     carried
    //$$             )) {
    //$$         return false;
    //$$     }
    //$$
    //$$     for (int slot = 0; slot < items.size(); ++slot) {
    //$$         if (!ItemStack.matches(
    //$$                 inventory.getItem(slot),
    //$$                 items.get(slot)
    //$$         )) {
    //$$             return false;
    //$$         }
    //$$     }
    //$$     return true;
    //$$ }
    //$$
    //$$ public void restore(ServerPlayer player) {
    //$$     Inventory inventory = player.getInventory();
    //$$     int size = Math.min(inventory.getContainerSize(), items.size());
    //$$
    //$$     for (int slot = 0; slot < size; ++slot) {
    //$$         inventory.setItem(slot, items.get(slot).copy());
    //$$     }
    //$$     for (int slot = size; slot < inventory.getContainerSize(); ++slot) {
    //$$         inventory.setItem(slot, ItemStack.EMPTY);
    //$$     }
    //$$
    //$$     if (selectedSlot >= 0 && selectedSlot < Inventory.getSelectionSize()) {
    //$$         inventory.setSelectedSlot(selectedSlot);
    //$$     }
    //$$     inventory.setChanged();
    //$$
    //$$     player.containerMenu.setCarried(carried.copy());
    //$$     player.inventoryMenu.broadcastFullState();
    //$$     if (player.containerMenu != player.inventoryMenu) {
    //$$         player.containerMenu.broadcastFullState();
    //$$     }
    //$$ }
    //$$
    //$$ public long estimatedBytes() {
    //$$     long result = 80L + 24L * items.size();
    //$$     for (ItemStack stack : items) {
    //$$         if (!stack.isEmpty()) result += 128L;
    //$$     }
    //$$     if (!carried.isEmpty()) result += 128L;
    //$$     return result;
    //$$ }
    //#else
    private PlayerInventorySnapshot() {
    }
    //#endif
}
