/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import carpet.patches.EntityPlayerMPFake;
//#if MC >= 11904
//$$ import carpet.fakes.ServerPlayerInterface;
//#else
//$$ import carpet.fakes.ServerPlayerEntityInterface;
//#endif
//#if MC >= 12005
//$$ import net.minecraft.core.component.DataComponents;
//#endif
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.EquipmentSlot;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import java.lang.reflect.InvocationTargetException;
//$$ import java.lang.reflect.Method;
//$$
//$$ final class DdsFakePlayerCarpetCompat {
//$$     private DdsFakePlayerCarpetCompat() {}
//$$
//$$     static EntityPlayerActionPack actionPack(ServerPlayer player) {
//#if MC >= 11904
//$$         return ((ServerPlayerInterface) player).getActionPack();
//#else
//$$         return ((ServerPlayerEntityInterface) player).getActionPack();
//#endif
//$$     }
//$$
//$$     static int selectedHotbarSlot(ServerPlayer player) {
//#if MC >= 12105
//$$         return player.getInventory().getSelectedSlot();
//#else
//$$         return player.getInventory().selected;
//#endif
//$$     }
//$$
//$$     static EquipmentSlot equipmentSlotForItem(Player player, ItemStack stack) {
//#if MC >= 12101
//$$         return player.getEquipmentSlotForItem(stack);
//#else
//$$         return LivingEntity.getEquipmentSlotForItem(stack);
//#endif
//$$     }
//$$
//$$     static boolean isFood(ItemStack stack) {
//#if MC >= 12005
//$$         return stack.has(DataComponents.FOOD);
//#else
//$$         return stack.isEdible();
//#endif
//$$     }
//$$
//$$     static boolean canPlaceInEquipmentSlot(LivingEntity owner, ItemStack stack, EquipmentSlot slot) {
//#if MC >= 12103
//$$         return owner.isEquippableInSlot(stack, slot);
//#elseif MC >= 12101
//$$         return owner.getEquipmentSlotForItem(stack) == slot;
//#else
//$$         return LivingEntity.getEquipmentSlotForItem(stack) == slot;
//#endif
//$$     }
//$$
//$$     static boolean canUseEquipmentSlot(LivingEntity owner, EquipmentSlot slot) {
//#if MC >= 12006
//$$         return owner.canUseSlot(slot);
//#else
//$$         return true;
//#endif
//$$     }
//$$
//$$     static void disconnectFake(EntityPlayerMPFake target) {
//$$         try { FakeKillHolder.METHOD.invoke(target, Component.literal("Killed")); }
//$$         catch (IllegalAccessException exception) { throw new IllegalStateException("Unable to access Carpet fake-player kill(Component)", exception); }
//$$         catch (InvocationTargetException exception) {
//$$             Throwable cause = exception.getCause();
//$$             if (cause instanceof RuntimeException) throw (RuntimeException) cause;
//$$             throw new IllegalStateException("Carpet fake-player disconnect failed", cause);
//$$         }
//$$     }
//$$
//$$     private static Method resolveFakeKillComponentMethod() {
//$$         try { return EntityPlayerMPFake.class.getMethod("kill", Component.class); }
//$$         catch (NoSuchMethodException exception) { throw new IllegalStateException("Unable to locate Carpet fake-player kill(Component)", exception); }
//$$     }
//$$
//$$     private static final class FakeKillHolder { private static final Method METHOD = resolveFakeKillComponentMethod(); }
//$$ }
//#else
public final class DdsFakePlayerCarpetCompat { private DdsFakePlayerCarpetCompat() {} }
//#endif
