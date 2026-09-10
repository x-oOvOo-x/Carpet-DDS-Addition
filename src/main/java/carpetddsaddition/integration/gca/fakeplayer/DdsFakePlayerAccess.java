/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11904 && MC <= 260200
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
//$$         return viewer.openMenu(new SimpleMenuProvider((id, inventory, player) ->
//$$                 DdsFakePlayerMenu.server(id, inventory, target, (ServerPlayer) player), target.getName())).isPresent();
//$$     }
//$$
//$$     private static boolean openEnderChest(ServerPlayer viewer, EntityPlayerMPFake target) {
//$$         int size = target.getEnderChestInventory().getContainerSize();
//$$         if (size != 27 && size != 54) return false;
//$$         Component title = target.getName();
//$$         return viewer.openMenu(new SimpleMenuProvider((id, inventory, player) ->
//$$                 DdsFakePlayerEnderChestMenu.create(id, inventory, target, (ServerPlayer) player), title)).isPresent();
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
