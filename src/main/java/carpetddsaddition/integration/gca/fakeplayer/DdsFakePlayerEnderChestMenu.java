/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11904 && MC <= 260200
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.core.inventory.DdsEnderMenuFactory;
//$$ import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//$$ import carpetddsaddition.feature.fakeplayerinventory.FakePlayerInventory;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.SimpleContainer;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.ChestMenu;
//$$ import net.minecraft.world.inventory.ContainerData;
//$$ import net.minecraft.world.inventory.MenuType;
//$$ import net.minecraft.world.inventory.SimpleContainerData;
//$$
//$$ public final class DdsFakePlayerEnderChestMenu extends ChestMenu implements DdsInventoryOwnerView, DdsEnderMenuFactory {
//$$     private final EntityPlayerMPFake target;
//$$     private final Container canonical;
//$$     private final ContainerData state;
//$$     private final int rows;
//$$
//$$     public static DdsFakePlayerEnderChestMenu create(int id, Inventory viewer, EntityPlayerMPFake target, ServerPlayer menuViewer) {
//$$         int size = target.getEnderChestInventory().getContainerSize(), rows = size / 9;
//$$         return rows == 3 || rows == 6 ? server(id, viewer, target, menuViewer, rows) : null;
//$$     }
//$$     public static DdsFakePlayerEnderChestMenu client(int id, Inventory viewer, int rows) {
//$$         return new DdsFakePlayerEnderChestMenu(id, viewer, null, new SimpleContainer(rows * 9), new SimpleContainerData(1), rows);
//$$     }
//$$     private static DdsFakePlayerEnderChestMenu server(int id, Inventory viewer, EntityPlayerMPFake target, ServerPlayer menuViewer, int rows) {
//$$         if (!DdsFakePlayerServerNetwork.sendOpenMenu(menuViewer, id, rows == 3 ? DdsFakePlayerPackets.MENU_ENDER_3 : DdsFakePlayerPackets.MENU_ENDER_6)) return null;
//$$         Container ender = target.getEnderChestInventory();
//$$         return new DdsFakePlayerEnderChestMenu(id, viewer, target, ender, new TargetStateData(target), rows);
//$$     }
//$$
//$$     private DdsFakePlayerEnderChestMenu(int id, Inventory viewer, EntityPlayerMPFake target, Container canonical, ContainerData state, int rows) {
//$$         super(rows == 3 ? MenuType.GENERIC_9x3 : MenuType.GENERIC_9x6, id, viewer,
//$$                 new DdsMappedContainer(canonical, identity(rows * 9), target, target == null ? null : canonical), rows);
//$$         this.target = target; this.canonical = target == null ? null : canonical; this.state = state; this.rows = rows;
//$$         addDataSlots(state);
//$$     }
//$$
//$$     public EntityPlayerMPFake target() { return target; }
//$$     public int rows() { return rows; }
//$$     public boolean isSneakingActive() { return state.get(0) != 0; }
//$$     public void predictSneaking(boolean value) { if (target == null) state.set(0, value ? 1 : 0); }
//$$
//$$     @Override public boolean stillValid(Player player) {
//$$         return target == null || FakePlayerInventory.enabled() && DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()
//$$                 && target.isAlive() && player.distanceToSqr(target) <= 64.0D
//$$                 && target.getEnderChestInventory() == canonical && canonical.getContainerSize() == rows * 9;
//$$     }
//$$     @Override public ServerPlayer ddsInventoryOwner() { return target; }
//$$     @Override public Container ddsCanonicalContainer() { return canonical; }
//$$     @Override public AbstractContainerMenu ddsCreateEnderMenu(int id, Inventory viewerInventory, ServerPlayer viewer) {
//$$         return target == null ? null : create(id, viewerInventory, target, viewer);
//$$     }
//$$
//$$     private static int[] identity(int size) { int[] result = new int[size]; for (int i = 0; i < size; i++) result[i] = i; return result; }
//$$     private static final class TargetStateData implements ContainerData {
//$$         private final EntityPlayerMPFake target;
//$$         private TargetStateData(EntityPlayerMPFake target) { this.target = target; }
//$$         @Override public int get(int index) { return index == 0 && target.isShiftKeyDown() ? 1 : 0; }
//$$         @Override public void set(int index, int value) {}
//$$         @Override public int getCount() { return 1; }
//$$     }
//$$ }
//#else
public final class DdsFakePlayerEnderChestMenu { private DdsFakePlayerEnderChestMenu() {} }
//#endif
