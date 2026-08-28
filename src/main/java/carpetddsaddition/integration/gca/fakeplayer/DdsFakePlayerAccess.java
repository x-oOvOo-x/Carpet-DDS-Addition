/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.feature.fakeplayerinventory.FakePlayerInventory;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.SimpleMenuProvider;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$
//$$ public final class DdsFakePlayerAccess {
//$$     private DdsFakePlayerAccess() {}
//$$
//$$     /** Returns false when DDS must leave the interaction completely to GCA. */
//$$     public static boolean tryOpenEnhanced(ServerPlayer viewer, EntityPlayerMPFake target, boolean enderChest) {
//$$         if (!FakePlayerInventory.enabled() || viewer == null || target == null || viewer.isSpectator() || !target.isAlive()) return false;
//$$         if (enderChest) {
//$$             if (!DdsGcaFakePlayerBridge.canEnhanceEnderChest(viewer)) return false;
//$$             return openEnderChest(viewer, target);
//$$         }
//$$         if (!DdsGcaFakePlayerBridge.canEnhanceInventory(viewer)) return false;
//$$         viewer.openMenu(new SimpleMenuProvider((id, inventory, player) -> DdsFakePlayerMenu.server(id, inventory, target), target.getName()));
//$$         return true;
//$$     }
//$$
//$$     private static boolean openEnderChest(ServerPlayer viewer, EntityPlayerMPFake target) {
//$$         int size = target.getEnderChestInventory().getContainerSize();
//$$         if (size != 27 && size != 54) return false;
//$$         Component title = target.getName();
//$$         viewer.openMenu(new SimpleMenuProvider((id, inventory, player) -> DdsFakePlayerEnderChestMenu.create(id, inventory, target), title));
//$$         return true;
//$$     }
//$$
//$$     public static void onRuleChanged(MinecraftServer server) {
//$$         if (server == null || FakePlayerInventory.enabled()) return;
//$$         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
//$$             AbstractContainerMenu menu = player.containerMenu;
//$$             if (menu instanceof DdsFakePlayerMenu || menu instanceof DdsFakePlayerEnderChestMenu) player.closeContainer();
//$$         }
//$$     }
//$$ }
//#else
import net.minecraft.server.MinecraftServer;
public final class DdsFakePlayerAccess {
    private DdsFakePlayerAccess() {}
    public static void onRuleChanged(MinecraftServer server) {}
}
//#endif
