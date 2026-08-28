/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.inventoryprofilesnext;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerScreen;
//$$ import org.anti_ad.mc.ipn.api.IPNButton;
//$$ import org.anti_ad.mc.ipn.api.IPNGuiHint;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$
//$$ @IPNGuiHint(button = IPNButton.MOVE_TO_PLAYER, top = 44)
//$$ @IPNGuiHint(button = IPNButton.SORT, top = 44)
//$$ @IPNGuiHint(button = IPNButton.SORT_COLUMNS, top = 44)
//$$ @IPNGuiHint(button = IPNButton.SORT_ROWS, top = 44)
//$$ @Mixin(DdsFakePlayerScreen.class)
//$$ public abstract class DdsFakePlayerScreenIpnHintsMixin {}
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DdsFakePlayerScreenIpnHintsTarget")
public abstract class DdsFakePlayerScreenIpnHintsMixin {}
//#endif
