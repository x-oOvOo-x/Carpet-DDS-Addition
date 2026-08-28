/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.carpet.gca.client;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerEnderChestMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerMenus;
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerEnderChestScreen;
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerScreen;
//$$ import net.minecraft.client.Minecraft;
//$$ import net.minecraft.client.gui.screens.MenuScreens;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.inventory.MenuType;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ @Mixin(MenuScreens.class)
//$$ public abstract class MenuScreensMixin {
//$$     @Inject(method = "create", at = @At("HEAD"), cancellable = true)
//$$     private static void dds$createFakePlayerScreen(MenuType type, Minecraft minecraft, int containerId,
//$$                                                    Component title, CallbackInfo ci) {
//$$         int enderRows = DdsFakePlayerMenus.enderRows(type);
//$$         if (type != DdsFakePlayerMenus.type() && enderRows == 0) return;
//$$         if (minecraft.player == null) { ci.cancel(); return; }
//$$         if (enderRows != 0) {
//$$             DdsFakePlayerEnderChestMenu menu = DdsFakePlayerEnderChestMenu.client(containerId, minecraft.player.getInventory(), enderRows);
//$$             minecraft.player.containerMenu = menu;
//$$             setScreen(minecraft, new DdsFakePlayerEnderChestScreen(menu, minecraft.player.getInventory(), title));
//$$         } else {
//$$             DdsFakePlayerMenu menu = DdsFakePlayerMenu.client(containerId, minecraft.player.getInventory());
//$$             minecraft.player.containerMenu = menu;
//$$             setScreen(minecraft, new DdsFakePlayerScreen(menu, minecraft.player.getInventory(), title));
//$$         }
//$$         ci.cancel();
//$$     }
//$$
//$$     private static void setScreen(Minecraft minecraft, net.minecraft.client.gui.screens.Screen screen) {
//#if MC >= 260200
//$$         minecraft.gui.setScreen(screen);
//#else
//$$         minecraft.setScreen(screen);
//#endif
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DdsFakePlayerMenuScreensTarget")
public abstract class MenuScreensMixin {}
//#endif
