/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoContext;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoRecord;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//$$ import net.minecraft.world.entity.EntityType;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(EntityType.class)
//$$ public abstract class EntityTypeMixin {
//$$     @Inject(method = "spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;"
//$$             + "Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/core/BlockPos;"
//$$             + "Lnet/minecraft/world/entity/EntitySpawnReason;ZZ)Lnet/minecraft/world/entity/Entity;",
//$$             at = @At("RETURN"))
//$$     private void dds$captureItemSpawnedEntity(ServerLevel level, ItemStack stack, LivingEntity user,
//$$                                               BlockPos pos, EntitySpawnReason reason, boolean tryMoveDown,
//$$                                               boolean movedUp, CallbackInfoReturnable<Entity> cir) {
//$$         Entity entity = cir.getReturnValue();
//$$         if (entity == null) return;
//$$         UndoRecord record = UndoContext.current();
//$$         if (record != null) record.recordFreshEntitySpawned(entity);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoEntityTypeTarget")
public abstract class EntityTypeMixin {}
//#endif
