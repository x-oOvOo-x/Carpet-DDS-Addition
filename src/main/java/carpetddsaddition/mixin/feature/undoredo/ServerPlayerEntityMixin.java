/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.UndoCause;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoActionRecorder;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoMutationRecorder;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(Player.class)
//$$ public abstract class ServerPlayerEntityMixin {
//$$     @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
//$$     private void dds$beforeAttackEntity(Entity target, CallbackInfo ci) {
//$$         if (!((Object) this instanceof ServerPlayer self)) return;
//$$         UndoActionRecorder.beginPlayerAction(self, UndoCause.ATTACK_ENTITY);
//$$         UndoMutationRecorder.recordEntityBefore(target);
//$$     }
//$$     @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
//$$     private void dds$afterAttackEntity(Entity target, CallbackInfo ci) {
//$$         if (!((Object) this instanceof ServerPlayer self)) return;
//$$         UndoMutationRecorder.recordEntityAfter(target);
//$$         UndoActionRecorder.endPlayerAction(self);
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoServerPlayerEntityTarget")
public abstract class ServerPlayerEntityMixin {}
//#endif
