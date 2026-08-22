/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.HashSet;
import java.util.Set;
//#if MC >= 12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif
//#endif

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenInputMixin {
    //#if MC >= 11404 && MC <= 260200
    @Shadow @Final protected AbstractContainerMenu menu;
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Unique private boolean dds$storageRightDown;
    @Unique private boolean dds$absorbDragging;
    @Unique private final Set<Integer> dds$visitedAbsorbSlots = new HashSet<>();

    //#if MC >= 12109
    //$$ @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$mouseClicked(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (dds$handleMouseClicked(event.x(), event.y(), event.button())) cir.setReturnValue(true);
    //$$ }
    //$$ @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    //$$ private void dds$mouseDragged(MouseButtonEvent event, double dragX, double dragY,
    //$$                               CallbackInfoReturnable<Boolean> cir) {
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

    @Unique
    private boolean dds$handleMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) dds$resetGesture();
        if (!QuickContainerAccess.enabled() || !QuickContainerAccessClientNetwork.canUseStorageClick()
                || button != 1 || (Object) this instanceof CreativeModeInventoryScreen) return false;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return false;
        //#if MC >= 11701
        //$$ ItemStack carried = menu.getCarried();
        //#else
        ItemStack carried = minecraft.player.inventory.getCarried();
        //#endif
        if (!QuickContainerAccessItems.isStorageHostCandidate(carried)) return false;
        Slot slot = dds$getSlot(mouseX, mouseY);
        if (slot == null) return false;
        //#if MC >= 11701
        //$$ if (slot.container != minecraft.player.getInventory()) return false;
        //#else
        if (slot.container != minecraft.player.inventory) return false;
        //#endif
        int slotId = menu.slots.indexOf(slot);
        if (slotId < 0 || !QuickContainerAccessClientNetwork.sendStorageClick(menu.containerId, slotId)) return false;
        dds$storageRightDown = true;
        dds$absorbDragging = !slot.getItem().isEmpty();
        dds$visitedAbsorbSlots.clear();
        if (dds$absorbDragging) dds$visitedAbsorbSlots.add(slotId);
        return true;
    }

    @Unique
    private boolean dds$handleMouseDragged(double mouseX, double mouseY, int button) {
        if (!dds$storageRightDown || button != 1) return false;
        if (!dds$absorbDragging) return true;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !QuickContainerAccess.enabled()
                || !QuickContainerAccessClientNetwork.canUseStorageClick()) return true;
        //#if MC >= 11701
        //$$ ItemStack carried = menu.getCarried();
        //#else
        ItemStack carried = minecraft.player.inventory.getCarried();
        //#endif
        if (!QuickContainerAccessItems.isStorageHostCandidate(carried)) return true;
        Slot slot = dds$getSlot(mouseX, mouseY);
        if (slot == null) return true;
        //#if MC >= 11701
        //$$ if (slot.container != minecraft.player.getInventory()) return true;
        //#else
        if (slot.container != minecraft.player.inventory) return true;
        //#endif
        int slotId = menu.slots.indexOf(slot);
        if (slotId < 0 || !dds$visitedAbsorbSlots.add(slotId) || slot.getItem().isEmpty()) return true;
        QuickContainerAccessClientNetwork.sendStorageClick(menu.containerId, slotId);
        return true;
    }

    @Unique
    private boolean dds$handleMouseReleased(int button) {
        if (!dds$storageRightDown || button != 1) return false;
        dds$resetGesture();
        return true;
    }

    @Unique
    private void dds$resetGesture() {
        dds$storageRightDown = dds$absorbDragging = false;
        dds$visitedAbsorbSlots.clear();
    }

    @Unique
    private Slot dds$getSlot(double mouseX, double mouseY) {
        double x = mouseX - leftPos, y = mouseY - topPos;
        for (Slot slot : menu.slots)
            if (x >= slot.x && x < slot.x + 16 && y >= slot.y && y < slot.y + 16) return slot;
        return null;
    }
    //#endif
}