/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import carpet.helpers.EntityPlayerActionPack.ActionType;
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.SimpleContainer;
//$$ import net.minecraft.world.entity.EquipmentSlot;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.ContainerData;
//$$ import net.minecraft.world.inventory.InventoryMenu;
//$$ import net.minecraft.world.inventory.SimpleContainerData;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import net.minecraft.world.item.ItemStack;
//$$
//$$ public final class DdsFakePlayerMenu extends AbstractContainerMenu {
//$$     public static final int FAKE_STORAGE_START = 5, FAKE_HOTBAR_START = 32, FAKE_STORAGE_END = 41;
//$$     public static final int VIEWER_MAIN_START = 41, VIEWER_HOTBAR_END = 77;
//$$     private static final int[] STORAGE_MAPPING = createStorageMapping();
//$$     private static final int[] EQUIPMENT_MAPPING = {39, 38, 37, 36, 40}; // head, chest, legs, feet, offhand
//$$     private final EntityPlayerMPFake target;
//$$     private final ContainerData state;
//$$
//$$     public static DdsFakePlayerMenu client(int containerId, Inventory viewerInventory) {
//$$         return new DdsFakePlayerMenu(containerId, viewerInventory, null, new SimpleContainer(41), new SimpleContainerData(5));
//$$     }
//$$
//$$     public static DdsFakePlayerMenu server(int containerId, Inventory viewerInventory, EntityPlayerMPFake target) {
//$$         return new DdsFakePlayerMenu(containerId, viewerInventory, target, target.getInventory(), new TargetStateData(target));
//$$     }
//$$
//$$     private DdsFakePlayerMenu(int containerId, Inventory viewerInventory, EntityPlayerMPFake target, Container fakeInventory, ContainerData state) {
//$$         super(DdsFakePlayerMenus.type(), containerId);
//$$         this.target = target; this.state = state;
//$$         addFakeEquipmentSlots(new DdsMappedContainer(fakeInventory, EQUIPMENT_MAPPING), target);
//$$         addInventorySlots(new DdsMappedContainer(fakeInventory, STORAGE_MAPPING), 84, 142);
//$$         addInventorySlots(viewerInventory, 184, 242);
//$$         addDataSlots(state);
//$$     }
//$$
//$$     private void addFakeEquipmentSlots(Container equipment, EntityPlayerMPFake owner) {
//$$         addSlot(new DdsFakePlayerEquipmentSlot(equipment, owner, EquipmentSlot.HEAD, 0, 8, 8, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
//$$         addSlot(new DdsFakePlayerEquipmentSlot(equipment, owner, EquipmentSlot.CHEST, 1, 8, 26, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
//$$         addSlot(new DdsFakePlayerEquipmentSlot(equipment, owner, EquipmentSlot.LEGS, 2, 8, 44, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
//$$         addSlot(new DdsFakePlayerEquipmentSlot(equipment, owner, EquipmentSlot.FEET, 3, 8, 62, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
//$$         addSlot(new DdsFakePlayerEquipmentSlot(equipment, owner, EquipmentSlot.OFFHAND, 4, 77, 62, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
//$$     }
//$$
//$$     private void addInventorySlots(Container inventory, int mainY, int hotbarY) {
//$$         for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++)
//$$             addSlot(new Slot(inventory, column + (row + 1) * 9, 8 + column * 18, mainY + row * 18));
//$$         for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, hotbarY));
//$$     }
//$$
//$$     @Override public boolean stillValid(Player player) { return target == null || target.isAlive() && player.distanceToSqr(target) <= 64.0D; }
//$$
//$$     @Override public ItemStack quickMoveStack(Player player, int slotIndex) {
//$$         if (slotIndex < 0 || slotIndex >= slots.size()) return ItemStack.EMPTY;
//$$         Slot slot = slots.get(slotIndex);
//$$         if (!slot.hasItem()) return ItemStack.EMPTY;
//$$         ItemStack stack = slot.getItem(), original = stack.copy();
//$$         if (slotIndex >= VIEWER_MAIN_START) {
//$$             int equipmentSlot = findFakeEquipmentSlot(target != null ? target : player, stack);
//$$             if (equipmentSlot >= 0 && !getSlot(equipmentSlot).hasItem() && getSlot(equipmentSlot).mayPlace(stack)) {
//$$                 if (!moveItemStackTo(stack, equipmentSlot, equipmentSlot + 1, false)) return ItemStack.EMPTY;
//$$             } else if (!moveItemStackTo(stack, FAKE_STORAGE_START, FAKE_STORAGE_END, false)) return ItemStack.EMPTY;
//$$         } else if (!moveItemStackTo(stack, VIEWER_MAIN_START, VIEWER_HOTBAR_END, true)) return ItemStack.EMPTY;
//$$         if (stack.isEmpty()) clearMovedSlot(slot, original); else slot.setChanged();
//$$         if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
//$$         slot.onTake(player, stack);
//$$         return original;
//$$     }
//$$
//$$     private static void clearMovedSlot(Slot slot, ItemStack original) {
//$$         if (slot instanceof DdsFakePlayerEquipmentSlot) ((DdsFakePlayerEquipmentSlot) slot).setFromQuickMove(ItemStack.EMPTY, original);
//$$         else slot.set(ItemStack.EMPTY);
//$$     }
//$$
//$$     private static int findFakeEquipmentSlot(Player owner, ItemStack stack) {
//$$         EquipmentSlot equipmentSlot = DdsFakePlayerCarpetCompat.equipmentSlotForItem(owner, stack);
//$$         switch (equipmentSlot) {
//$$             case HEAD: return 0; case CHEST: return 1; case LEGS: return 2; case FEET: return 3; case OFFHAND: return 4;
//$$             default: return DdsFakePlayerCarpetCompat.isFood(stack) ? 4 : -1;
//$$         }
//$$     }
//$$
//$$     public int selectedFakeHotbarSlot() { return state.get(0); }
//$$     public int attackMode() { return state.get(1); }
//$$     public int attackInterval() { return state.get(2); }
//$$     public int useMode() { return state.get(3); }
//$$     public int useInterval() { return state.get(4); }
//$$     public boolean isAttackIntervalActive() { return attackMode() == DdsFakePlayerActions.MODE_INTERVAL; }
//$$     public boolean isAttackContinuousActive() { return attackMode() == DdsFakePlayerActions.MODE_CONTINUOUS; }
//$$     public boolean isUseIntervalActive() { return useMode() == DdsFakePlayerActions.MODE_INTERVAL; }
//$$     public boolean isUseContinuousActive() { return useMode() == DdsFakePlayerActions.MODE_CONTINUOUS; }
//$$     public void predictAttackInterval(int interval) { predictInterval(1, 2, interval); }
//$$     public void predictAttackContinuous() { predictMode(1, DdsFakePlayerActions.MODE_CONTINUOUS); }
//$$     public void predictUseInterval(int interval) { predictInterval(3, 4, interval); }
//$$     public void predictUseContinuous() { predictMode(3, DdsFakePlayerActions.MODE_CONTINUOUS); }
//$$     public void predictStopAttack() { predictMode(1, DdsFakePlayerActions.MODE_NONE); }
//$$     public void predictStopUse() { predictMode(3, DdsFakePlayerActions.MODE_NONE); }
//$$
//$$     public void predictStopAll() {
//$$         if (target == null) { state.set(1, DdsFakePlayerActions.MODE_NONE); state.set(3, DdsFakePlayerActions.MODE_NONE); }
//$$     }
//$$     public void predictSelectedHotbar(int slot) { if (target == null && slot >= 0 && slot <= 8) state.set(0, slot); }
//$$     private void predictInterval(int modeIndex, int valueIndex, int interval) {
//$$         if (target == null) { state.set(modeIndex, DdsFakePlayerActions.MODE_INTERVAL); state.set(valueIndex, DdsFakePlayerActions.clampInterval(interval)); }
//$$     }
//$$     private void predictMode(int index, int mode) { if (target == null) state.set(index, mode); }
//$$     public EntityPlayerMPFake target() { return target; }
//$$
//$$     private static int[] createStorageMapping() {
//$$         int[] mapping = new int[36];
//$$         for (int i = 0; i < mapping.length; i++) mapping[i] = i;
//$$         return mapping;
//$$     }
//$$
//$$     private static final class TargetStateData implements ContainerData {
//$$         private final EntityPlayerMPFake target;
//$$         private final EntityPlayerActionPack actionPack;
//$$         private TargetStateData(EntityPlayerMPFake target) { this.target = target; actionPack = DdsFakePlayerCarpetCompat.actionPack(target); }
//$$         @Override public int get(int index) {
//$$             if (index == 0) return DdsFakePlayerCarpetCompat.selectedHotbarSlot(target);
//$$             switch (index) {
//$$                 case 1: return DdsFakePlayerActions.mode(actionPack, ActionType.ATTACK);
//$$                 case 2: return DdsFakePlayerActions.interval(actionPack, ActionType.ATTACK);
//$$                 case 3: return DdsFakePlayerActions.mode(actionPack, ActionType.USE);
//$$                 case 4: return DdsFakePlayerActions.interval(actionPack, ActionType.USE);
//$$                 default: return 0;
//$$             }
//$$         }
//$$         @Override public void set(int index, int value) {}
//$$         @Override public int getCount() { return 5; }
//$$     }
//$$ }
//#else
public final class DdsFakePlayerMenu { private DdsFakePlayerMenu() {} }
//#endif
