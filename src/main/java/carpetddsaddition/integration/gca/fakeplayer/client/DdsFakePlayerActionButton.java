/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.client;

//#if MC >= 11902 && MC <= 260200
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import java.util.function.BooleanSupplier;
//$$
//$$ final class DdsFakePlayerActionButton {
//$$     static final int WIDTH = 20, HEIGHT = 18;
//$$     final int x, y;
//$$     final ItemStack icon;
//$$     private final BooleanSupplier selected;
//$$     private final Runnable action;
//$$     private final Component inactiveTooltip, activeTooltip;
//$$
//$$     DdsFakePlayerActionButton(int x, int y, ItemStack icon, BooleanSupplier selected, Runnable action, Component inactiveTooltip, Component activeTooltip) {
//$$         this.x = x; this.y = y; this.icon = icon; this.selected = selected; this.action = action;
//$$         this.inactiveTooltip = inactiveTooltip; this.activeTooltip = activeTooltip;
//$$     }
//$$
//$$     boolean contains(double mouseX, double mouseY) { return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT; }
//$$     boolean isSelected() { return selected.getAsBoolean(); }
//$$     Component tooltip() { return isSelected() ? activeTooltip : inactiveTooltip; }
//$$     void press() { action.run(); }
//$$ }
//#else
public final class DdsFakePlayerActionButton { private DdsFakePlayerActionButton() {} }
//#endif
