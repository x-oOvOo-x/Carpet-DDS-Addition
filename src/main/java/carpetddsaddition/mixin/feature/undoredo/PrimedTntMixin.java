/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoAsyncOrigin;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScope;
//$$ import net.minecraft.world.damagesource.DamageSource;
//$$ import net.minecraft.world.entity.Entity;
//$$ import net.minecraft.world.entity.item.PrimedTnt;
//$$ import net.minecraft.world.level.ExplosionDamageCalculator;
//$$ import net.minecraft.world.level.Level;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(PrimedTnt.class)
//$$ public abstract class PrimedTntMixin {
//$$     @Redirect(method = "explode()V", at = @At(value = "INVOKE", target =
//$$             "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;"
//$$             + "Lnet/minecraft/world/damagesource/DamageSource;"
//$$             + "Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZ"
//$$             + "Lnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
//$$     private void dds$explodeWithUndoOrigin(Level level, Entity source, DamageSource damageSource,
//$$                                            ExplosionDamageCalculator damageCalculator, double x, double y,
//$$                                            double z, float power, boolean fire,
//$$                                            Level.ExplosionInteraction interaction) {
//$$         long recordId = ((UndoOriginAccess) (Object) this).dds$getUndoOriginId();
//$$         try (UndoScope ignored = UndoAsyncOrigin.enterRecord(recordId)) {
//$$             level.explode(source, damageSource, damageCalculator, x, y, z, power, fire, interaction);
//$$         }
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoPrimedTntTarget")
public abstract class PrimedTntMixin {}
//#endif
