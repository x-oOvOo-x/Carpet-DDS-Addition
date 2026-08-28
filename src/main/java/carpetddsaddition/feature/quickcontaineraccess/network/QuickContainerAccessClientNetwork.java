/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessClient;
import carpetddsaddition.network.DdsClientNetwork;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/** Client network slice for quickContainerAccess. */
public final class QuickContainerAccessClientNetwork {
    private static final long CREATIVE_TIMEOUT_NANOS = 5_000_000_000L;
    private static final Queue<Integer> CREATIVE_QUEUE = new ArrayDeque<>();
    private static int nextTransaction = 1, pendingTransaction = -1, navigationContainer = -1, pendingBack = -1;
    private static long pendingSince;
    private static AbstractContainerMenu pendingMenu;
    private static Screen playerInventoryParent;
    static {
        DdsClientNetwork.registerS2CHandler(QuickContainerAccessPackets.CREATIVE_STORAGE_RESULT_S2C,
                QuickContainerAccessClientNetwork::handleCreativeStorageResult);
        DdsClientNetwork.registerS2CHandler(QuickContainerAccessPackets.NAVIGATION_STATE_S2C,
                QuickContainerAccessClientNetwork::handleNavigationState);
    }
    private QuickContainerAccessClientNetwork() {}

    public static boolean canUseOpenSlot() { return DdsClientNetwork.canSend(QuickContainerAccessPackets.OPEN_SLOT_C2S); }
    public static boolean canUseOpenInventorySlot() { return DdsClientNetwork.canSend(QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S); }
    public static boolean canUseStorageClick() { return DdsClientNetwork.canSend(QuickContainerAccessPackets.STORAGE_CLICK_C2S); }
    public static boolean canUseCreativeStorage() { return DdsClientNetwork.canSend(QuickContainerAccessPackets.CREATIVE_STORAGE_C2S); }
    public static boolean creativeStoragePending(AbstractContainerMenu menu) {
        expireCreativePending();
        if (pendingTransaction < 0) return false;
        if (pendingMenu != menu || !canUseCreativeStorage()) { resetCreativeStorage(); return false; }
        return true;
    }
    public static boolean sendOpenSlot(AbstractContainerMenu menu, int slotId) {
        if (menu == null) return false;
        rememberPlayerInventoryParent(menu);
        return send(QuickContainerAccessPackets.OPEN_SLOT_C2S, menu.containerId, slotId);
    }
    public static boolean sendOpenInventorySlot(int inventorySlot) {
        if (!canUseOpenInventorySlot()) return false;
        rememberPlayerInventoryParent(null);
        CompoundTag data = new CompoundTag(); data.putInt("inventory_slot", inventorySlot);
        return DdsClientNetwork.send(QuickContainerAccessPackets.OPEN_INVENTORY_SLOT_C2S, data);
    }
    public static boolean sendStorageClick(int containerId, int slotId) { return send(QuickContainerAccessPackets.STORAGE_CLICK_C2S, containerId, slotId); }
    public static boolean sendNavigateBack(AbstractContainerMenu menu) {
        if (menu == null || menu.containerId != navigationContainer || !DdsClientNetwork.canSend(QuickContainerAccessPackets.NAVIGATE_BACK_C2S)) return false;
        if (pendingBack == menu.containerId) return true;
        CompoundTag data = new CompoundTag(); data.putInt("container_id", menu.containerId);
        if (!DdsClientNetwork.send(QuickContainerAccessPackets.NAVIGATE_BACK_C2S, data)) return false;
        pendingBack = menu.containerId; return true;
    }
    public static boolean shouldSuppressTransitionClose(int containerId) {
        if (containerId == pendingBack) return true;
        if (containerId == navigationContainer) { navigationContainer = -1; playerInventoryParent = null; }
        return false;
    }

    public static void beginCreativeStorageGesture() { CREATIVE_QUEUE.clear(); expireCreativePending(); }
    public static boolean sendCreativeStorageSlot(int inventorySlot, Player player, AbstractContainerMenu menu, ItemStack carried) {
        if (!canUseCreativeStorage() || player == null || menu == null || carried == null || carried.isEmpty()
                || !QuickContainerAccessClient.isCreativeSurvivalScreen()) { resetCreativeStorage(); return false; }
        expireCreativePending();
        if (pendingTransaction >= 0 && pendingMenu != menu) resetCreativeStorage();
        if (pendingTransaction >= 0) { CREATIVE_QUEUE.offer(inventorySlot); return true; }
        return sendCreativeNow(inventorySlot, player, menu, carried);
    }

    private static boolean sendCreativeNow(int inventorySlot, Player player, AbstractContainerMenu menu, ItemStack carried) {
        if (carried == null || carried.isEmpty()) return false;
        int transaction = nextTransaction; nextTransaction = nextTransaction == Integer.MAX_VALUE ? 1 : nextTransaction + 1;
        CompoundTag data = new CompoundTag(); data.putInt("transaction", transaction); data.putInt("inventory_slot", inventorySlot);
        QuickContainerAccessStackCodec.put(data, "carried", player, carried);
        if (!DdsClientNetwork.send(QuickContainerAccessPackets.CREATIVE_STORAGE_C2S, data)) return false;
        pendingTransaction = transaction; pendingSince = System.nanoTime(); pendingMenu = menu; return true;
    }

    private static void handleCreativeStorageResult(CompoundTag data) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || pendingMenu == null || !QuickContainerAccessClient.isCreativeSurvivalScreen()) {
            resetCreativeStorage(); return;
        }
        int transaction = intValue(data, "transaction"); if (transaction != pendingTransaction) return;
        boolean handled = intValue(data, "handled") == 1;
        if (!handled) { resetCreativeStorage(); return; }
        ItemStack carried = QuickContainerAccessStackCodec.get(data, "carried", minecraft.player);
        AbstractContainerMenu menu = pendingMenu;
        QuickContainerAccessClient.applyCreativeCarried(menu, minecraft.player, carried);
        pendingTransaction = -1; pendingSince = 0L;
        while (!CREATIVE_QUEUE.isEmpty()) {
            int slot = CREATIVE_QUEUE.poll();
            if (!QuickContainerAccessClient.isCreativeSurvivalScreen()) { resetCreativeStorage(); return; }
            if (sendCreativeNow(slot, minecraft.player, menu, carried)) return;
        }
        pendingMenu = null;
    }

    private static void handleNavigationState(CompoundTag data) {
        boolean rejected = intValue(data, "rejected") == 1, openPlayerInventory = intValue(data, "open_player_inventory") == 1;
        int containerId = intValue(data, "container_id");
        navigationContainer = intValue(data, "can_back") == 1 ? containerId : -1;
        if (rejected) {
            pendingBack = -1; playerInventoryParent = null;
            Screen screen = currentScreen(); if (screen != null) screen.onClose();
            return;
        }
        pendingBack = -1;
        if (openPlayerInventory) {
            Minecraft minecraft = Minecraft.getInstance(); Screen parent = playerInventoryParent; playerInventoryParent = null;
            if (minecraft.player != null) {
                minecraft.player.containerMenu = minecraft.player.inventoryMenu;
                setScreen(minecraft, parent != null ? parent : new InventoryScreen(minecraft.player));
            }
        } else if (navigationContainer < 0) playerInventoryParent = null;
    }

    private static void rememberPlayerInventoryParent(AbstractContainerMenu menu) {
        Minecraft minecraft = Minecraft.getInstance(); Screen screen = currentScreen();
        if (minecraft.player != null && screen != null
                && (menu == minecraft.player.inventoryMenu || QuickContainerAccessClient.isCreativeSurvivalScreen())) playerInventoryParent = screen;
    }
    private static Screen currentScreen() {
        //#if MC >= 260200
        //$$ return Minecraft.getInstance().gui.screen();
        //#else
        return Minecraft.getInstance().screen;
        //#endif
    }
    private static void setScreen(Minecraft minecraft, Screen screen) {
        //#if MC >= 260200
        //$$ minecraft.gui.setScreen(screen);
        //#else
        minecraft.setScreen(screen);
        //#endif
    }
    private static void expireCreativePending() {
        if (pendingTransaction >= 0 && System.nanoTime() - pendingSince >= CREATIVE_TIMEOUT_NANOS) resetCreativeStorage();
    }
    private static void resetCreativeStorage() {
        pendingTransaction = -1; pendingSince = 0L; pendingMenu = null; CREATIVE_QUEUE.clear();
    }
    private static int intValue(CompoundTag data, String key) {
        //#if MC >= 12105
        //$$ return data.getIntOr(key, -1);
        //#else
        return data.contains(key) ? data.getInt(key) : -1;
        //#endif
    }
    private static boolean send(String packet, int containerId, int slotId) {
        if (!DdsClientNetwork.canSend(packet)) return false;
        CompoundTag data = new CompoundTag(); data.putInt("container_id", containerId); data.putInt("slot_id", slotId);
        return DdsClientNetwork.send(packet, data);
    }
}
