/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.minihud;

//#if MC >= 12111 && MC <= 260200
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import java.lang.reflect.Method;
//$$
//$$ /** MiniHUD reads raw player inventory order (hotbar first); normalize its detached preview copy to GUI order. */
//$$ @Pseudo
//$$ @Mixin(targets = "fi.dy.masa.minihud.renderer.InventoryOverlayHandler", remap = false)
//$$ public abstract class MiniHudInventoryPreviewMixin {
//$$     private static Class<?> dds$contextClass;
//$$     private static Method dds$inventoryMethod, dds$entityMethod;
//$$
//$$     @Inject(method = "getTargetInventoryFromEntity", at = @At("RETURN"), require = 0, remap = false)
//$$     private void dds$normalizePlayerPreview(CallbackInfoReturnable<?> cir) {
//$$         Object context = cir.getReturnValue();
//$$         if (context == null) return;
//$$         try {
//$$             Class<?> type = context.getClass();
//$$             if (dds$contextClass != type || dds$inventoryMethod == null || dds$entityMethod == null) {
//$$                 Method inventoryMethod = type.getMethod("inv"), entityMethod = type.getMethod("entity");
//$$                 dds$contextClass = type; dds$inventoryMethod = inventoryMethod; dds$entityMethod = entityMethod;
//$$             }
//$$             Object entity = dds$entityMethod.invoke(context), value = dds$inventoryMethod.invoke(context);
//$$             if (!(entity instanceof Player) || !(value instanceof Container) || value instanceof Inventory) return;
//$$             Container inventory = (Container) value;
//$$             if (inventory.getContainerSize() != 36) return;
//$$             ItemStack[] copy = new ItemStack[36];
//$$             for (int i = 0; i < 36; i++) copy[i] = inventory.getItem(i);
//$$             for (int i = 0; i < 27; i++) inventory.setItem(i, copy[i + 9]);
//$$             for (int i = 0; i < 9; i++) inventory.setItem(27 + i, copy[i]);
//$$             inventory.setChanged();
//$$         } catch (ReflectiveOperationException | LinkageError ignored) {}
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.MiniHudInventoryPreviewTarget")
public abstract class MiniHudInventoryPreviewMixin {}
//#endif