/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.carpet.gca;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsCanonicalSlotMapping;
//$$ import carpetddsaddition.core.inventory.DdsDirectSlotAccess;
//$$ import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$
//$$ @Pseudo
//$$ @Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.PlayerContainer", remap = false)
//$$ public abstract class GcaPlayerContainerContextMixin implements DdsInventoryOwnerView, DdsDirectSlotAccess, DdsCanonicalSlotMapping {
//$$     @Shadow protected ServerPlayer player;
//$$     @Override public ServerPlayer ddsInventoryOwner() { return player; }
//$$     @Override public Container ddsCanonicalContainer() {
//$$         return getClass().getName().endsWith("PlayerEnderChestContainer") ? player.getEnderChestInventory() : null;
//$$     }
//$$     @Override public boolean ddsAllowsDirectSlotAccess(int slot) {
//$$         String name = getClass().getName();
//$$         if (name.endsWith("PlayerInventoryContainer")) return slot == 7 || slot >= 18 && slot <= 53;
//$$         return ddsCanonicalSlot(slot) >= 0;
//$$     }
//$$     @Override public int ddsCanonicalSlot(int providerSlot) {
//$$         if (!getClass().getName().endsWith("PlayerEnderChestContainer") || providerSlot < 27) return -1;
//$$         int canonical = providerSlot - 27;
//$$         return canonical < player.getEnderChestInventory().getContainerSize() ? canonical : -1;
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.GcaPlayerContainerContextTarget")
public abstract class GcaPlayerContainerContextMixin {}
//#endif
