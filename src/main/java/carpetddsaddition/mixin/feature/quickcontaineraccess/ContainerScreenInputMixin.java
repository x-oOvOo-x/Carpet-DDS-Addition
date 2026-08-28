/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessClient;
import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessItems;
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessClientNetwork;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
//#if MC >= 11404 && MC <= 260200
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashSet;
import java.util.Set;
//#if MC >= 12109
//$$ import net.minecraft.client.input.KeyEvent;
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif
//#endif

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenInputMixin {
    //#if MC >= 11404 && MC <= 260200
    @Shadow @Final protected AbstractContainerMenu menu;
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Unique private boolean dds$storageRightDown, dds$creativeStorageGesture;
    @Unique private QuickContainerAccessClient.StorageDragMode dds$storageDragMode;
    @Unique private final Set<Integer> dds$visitedStorageSlots = new HashSet<>();

    //#if MC >= 11602
    //$$ @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$navigateBackOnClose(CallbackInfo ci) {
    //$$     if (QuickContainerAccessClientNetwork.sendNavigateBack(menu)) ci.cancel();
    //$$ }
    //#endif

    //#if MC >= 12109
    //$$ @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$navigateBackOnEscape(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (event.key() == GLFW.GLFW_KEY_ESCAPE && QuickContainerAccessClientNetwork.sendNavigateBack(menu)) cir.setReturnValue(true);
    //$$ }
    //#else
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void dds$navigateBackOnEscape(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && QuickContainerAccessClientNetwork.sendNavigateBack(menu)) cir.setReturnValue(true);
    }
    //#endif

    //#if MC >= 12109
    //$$ @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$mouseClicked(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (dds$handleMouseClicked(event.x(), event.y(), event.button())) cir.setReturnValue(true);
    //$$ }
    //$$ @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$mouseDragged(MouseButtonEvent event, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (dds$handleMouseDragged(event.x(), event.y(), event.button())) cir.setReturnValue(true);
    //$$ }
    //$$ @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$mouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (dds$handleMouseReleased(event.button())) cir.setReturnValue(true);
    //$$ }
    //#else
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void dds$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (dds$handleMouseClicked(mouseX, mouseY, button)) cir.setReturnValue(true);
    }
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void dds$mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (dds$handleMouseDragged(mouseX, mouseY, button)) cir.setReturnValue(true);
    }
    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void dds$mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (dds$handleMouseReleased(button)) cir.setReturnValue(true);
    }
    //#endif

    @Unique private boolean dds$handleMouseClicked(double mouseX, double mouseY, int button) {
        Minecraft minecraft = Minecraft.getInstance(); if (minecraft.player == null) return false;
        boolean creativeScreen = (Object) this instanceof CreativeModeInventoryScreen;
        if (creativeScreen && QuickContainerAccessClientNetwork.creativeStoragePending(menu)) return true;
        if (button == 1) dds$resetGesture();
        if (!QuickContainerAccess.enabled() || button != 1) return false;
        //#if MC >= 11701
        //$$ ItemStack carried = menu.getCarried();
        //#else
        ItemStack carried = minecraft.player.inventory.getCarried();
        //#endif
        Slot slot = dds$getSlot(mouseX, mouseY);
        if (creativeScreen && QuickContainerAccessClient.isCreativePickerScreen()) return false;
        if (carried.isEmpty()) {
            if (creativeScreen) {
                int inventorySlot = QuickContainerAccessClient.creativeInventoryOpenSlot(slot, minecraft.player);
                if (inventorySlot >= 0 && QuickContainerAccessClientNetwork.sendOpenInventorySlot(inventorySlot)) return true;
            } else if (QuickContainerAccessClient.canUseDedicatedOpenSlot(slot, minecraft.player)) {
                int slotId = menu.slots.indexOf(slot);
                if (slotId >= 0 && QuickContainerAccessClientNetwork.sendOpenSlot(menu, slotId)) return true;
            }
        }
        if (creativeScreen) {
            int inventorySlot = QuickContainerAccessClient.creativeInventoryStorageSlot(slot, minecraft.player, carried);
            if (inventorySlot < 0) return false;
            QuickContainerAccessClientNetwork.beginCreativeStorageGesture();
            if (!QuickContainerAccessClientNetwork.sendCreativeStorageSlot(inventorySlot, minecraft.player, menu, carried)) return false;
            if (QuickContainerAccessItems.isStorageHostCandidate(carried)) {
                dds$creativeStorageGesture = dds$storageRightDown = true;
                dds$storageDragMode = slot.getItem().isEmpty()
                        ? QuickContainerAccessClient.StorageDragMode.EXTRACT : QuickContainerAccessClient.StorageDragMode.ABSORB;
                dds$visitedStorageSlots.add(inventorySlot);
            }
            return true;
        }
        if (!QuickContainerAccessClientNetwork.canUseStorageClick()
                || !QuickContainerAccessClient.canUseDedicatedStorageInteraction(slot, minecraft.player, carried)) return false;
        int slotId = menu.slots.indexOf(slot);
        if (slotId < 0 || !QuickContainerAccessClientNetwork.sendStorageClick(menu.containerId, slotId)) return false;
        dds$storageRightDown = true;
        dds$storageDragMode = slot.getItem().isEmpty()
                ? QuickContainerAccessClient.StorageDragMode.EXTRACT : QuickContainerAccessClient.StorageDragMode.ABSORB;
        dds$visitedStorageSlots.add(slotId);
        return true;
    }

    @Unique private boolean dds$handleMouseDragged(double mouseX, double mouseY, int button) {
        if (!dds$storageRightDown || button != 1) return false;
        if (dds$storageDragMode == null) return true;
        Minecraft minecraft = Minecraft.getInstance(); if (minecraft.player == null || !QuickContainerAccess.enabled()) return true;
        //#if MC >= 11701
        //$$ ItemStack carried = menu.getCarried();
        //#else
        ItemStack carried = minecraft.player.inventory.getCarried();
        //#endif
        Slot slot = dds$getSlot(mouseX, mouseY);
        if (dds$creativeStorageGesture) {
            if (!QuickContainerAccessClient.isCreativeSurvivalScreen()) { dds$resetGesture(); return false; }
            int inventorySlot = QuickContainerAccessClient.creativeInventoryStorageSlot(slot, minecraft.player, carried);
            if (inventorySlot < 0 || dds$visitedStorageSlots.contains(inventorySlot)) return true;
            boolean empty = slot.getItem().isEmpty();
            if (dds$storageDragMode == QuickContainerAccessClient.StorageDragMode.ABSORB ? empty : !empty) return true;
            if (QuickContainerAccessClientNetwork.sendCreativeStorageSlot(inventorySlot, minecraft.player, menu, carried))
                dds$visitedStorageSlots.add(inventorySlot);
            return true;
        }
        if (!QuickContainerAccessClientNetwork.canUseStorageClick()) return true;
        if (!QuickContainerAccessClient.canUseDedicatedStorageInteraction(slot, minecraft.player, carried)) return true;
        boolean empty = slot.getItem().isEmpty();
        if (dds$storageDragMode == QuickContainerAccessClient.StorageDragMode.ABSORB ? empty : !empty) return true;
        int slotId = menu.slots.indexOf(slot);
        if (slotId < 0 || dds$visitedStorageSlots.contains(slotId)) return true;
        if (QuickContainerAccessClientNetwork.sendStorageClick(menu.containerId, slotId)) dds$visitedStorageSlots.add(slotId);
        return true;
    }

    @Unique private boolean dds$handleMouseReleased(int button) {
        if (!dds$storageRightDown || button != 1) return false;
        dds$resetGesture(); return true;
    }
    @Unique private void dds$resetGesture() {
        dds$storageRightDown = dds$creativeStorageGesture = false; dds$storageDragMode = null; dds$visitedStorageSlots.clear();
    }
    @Unique private Slot dds$getSlot(double mouseX, double mouseY) {
        double x = mouseX - leftPos, y = mouseY - topPos;
        for (Slot slot : menu.slots)
            if (x >= slot.x && x < slot.x + 16 && y >= slot.y && y < slot.y + 16) return slot;
        return null;
    }
    //#endif
}
