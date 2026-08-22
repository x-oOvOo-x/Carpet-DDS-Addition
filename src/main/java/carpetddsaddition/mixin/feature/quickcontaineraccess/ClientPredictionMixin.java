/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessClient;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//#if MC >= 11404 && MC <= 260200
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
//#if MC >= 260102
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
import net.minecraft.world.inventory.ClickType;
//#endif
//#if MC < 11701
import net.minecraft.world.item.ItemStack;
//#endif
//#endif

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPredictionMixin {
    //#if MC >= 11404 && MC <= 260200
    //#if MC < 11701
    @Redirect(
            method = "handleInventoryMouseClick",
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
                    + "Lnet/minecraft/world/inventory/ClickType;"
                    + "Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack dds$suppressQuickContainerPrediction(AbstractContainerMenu menu, int slotNum,
                                                            int buttonNum, ClickType clickType, Player player) {
        if (QuickContainerAccessClient.shouldSuppressPrediction(menu, slotNum, buttonNum, clickType, player))
            return ItemStack.EMPTY;
        return menu.clicked(slotNum, buttonNum, clickType, player);
    }
    //#else
    //$$ @Redirect(
    //#if MC >= 260102
    //$$         method = "handleContainerInput",
    //#else
    //$$         method = "handleInventoryMouseClick",
    //#endif
    //$$         at = @At(value = "INVOKE",
    //#if MC >= 260102
    //$$                 target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
    //$$                         + "Lnet/minecraft/world/inventory/ContainerInput;"
    //$$                         + "Lnet/minecraft/world/entity/player/Player;)V"
    //#else
    //$$                 target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;clicked(II"
    //$$                         + "Lnet/minecraft/world/inventory/ClickType;"
    //$$                         + "Lnet/minecraft/world/entity/player/Player;)V"
    //#endif
    //$$         )
    //$$ )
    //$$ private void dds$suppressQuickContainerPrediction(AbstractContainerMenu menu, int slotNum, int buttonNum,
    //#if MC >= 260102
    //$$                                                        ContainerInput clickType,
    //#else
    //$$                                                        ClickType clickType,
    //#endif
    //$$                                                        Player player) {
    //$$     if (QuickContainerAccessClient.shouldSuppressPrediction(menu, slotNum, buttonNum, clickType, player)) return;
    //$$     menu.clicked(slotNum, buttonNum, clickType, player);
    //$$ }
    //#endif
    //#endif
}