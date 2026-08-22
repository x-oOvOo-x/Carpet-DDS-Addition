/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.explosionnoblockdamage;

import carpetddsaddition.feature.explosionnoblockdamage.ExplosionNoBlockDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
//#if MC >= 11903
//$$ import net.minecraft.world.level.Level;
//#else
import net.minecraft.world.level.Explosion;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    //#if MC >= 11903
    //$$ @ModifyVariable(method = "explode", at = @At("HEAD"), argsOnly = true)
    //$$ private Level.ExplosionInteraction carpetDDSAddition$modifyExplosionInteraction(Level.ExplosionInteraction original, Entity entity) {
    //$$     return ExplosionNoBlockDamage.shouldProtectBlocks(entity) ? Level.ExplosionInteraction.NONE : original;
    //$$ }
    //#else
    @ModifyVariable(method = "explode", at = @At("HEAD"), argsOnly = true)
    private Explosion.BlockInteraction carpetDDSAddition$modifyExplosionInteractionLegacy(Explosion.BlockInteraction original, Entity entity) {
        return ExplosionNoBlockDamage.shouldProtectBlocks(entity) ? Explosion.BlockInteraction.NONE : original;
    }
    //#endif
}
