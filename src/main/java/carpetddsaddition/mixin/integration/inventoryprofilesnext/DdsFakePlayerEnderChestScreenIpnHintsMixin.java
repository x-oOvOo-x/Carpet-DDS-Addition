/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.inventoryprofilesnext;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerEnderChestScreen;
//$$ import org.anti_ad.mc.ipn.api.IPNButton;
//$$ import org.anti_ad.mc.ipn.api.IPNGuiHint;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$
//$$ @IPNGuiHint(button = IPNButton.MOVE_TO_PLAYER, horizontalOffset = 24)
//$$ @IPNGuiHint(button = IPNButton.SORT, horizontalOffset = 24)
//$$ @IPNGuiHint(button = IPNButton.SORT_COLUMNS, horizontalOffset = 24)
//$$ @IPNGuiHint(button = IPNButton.SORT_ROWS, horizontalOffset = 24)
//$$ @IPNGuiHint(button = IPNButton.MOVE_TO_CONTAINER, bottom = 8)
//$$ @Mixin(DdsFakePlayerEnderChestScreen.class)
//$$ public abstract class DdsFakePlayerEnderChestScreenIpnHintsMixin {}
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DdsFakePlayerEnderChestScreenIpnHintsTarget")
public abstract class DdsFakePlayerEnderChestScreenIpnHintsMixin {}
//#endif
