/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.novillagerwitchconversion;

import carpetddsaddition.feature.novillagerwitchconversion.NoVillagerWitchConversion;
//#if MC < 11600
import net.minecraft.world.entity.global.LightningBolt;
//#else
//$$ import net.minecraft.world.entity.LightningBolt;
//#endif
import net.minecraft.world.entity.EntityType;
//#if MC >= 11602
//$$ import net.minecraft.server.level.ServerLevel;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.world.entity.npc.villager.AbstractVillager;
//$$ import net.minecraft.world.entity.npc.villager.Villager;
//#else
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
//#endif
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    protected VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) { super(entityType, level); }

    //#if MC >= 11602
    //$$ @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    //$$ private void carpetDDSAddition$preventWitchConversion(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
    //$$     if (!NoVillagerWitchConversion.enabled()) return;
    //$$     super.thunderHit(level, lightningBolt);
    //$$     ci.cancel();
    //$$ }
    //#else
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void carpetDDSAddition$preventWitchConversion(LightningBolt lightningBolt, CallbackInfo ci) {
        if (!NoVillagerWitchConversion.enabled()) return;
        super.thunderHit(lightningBolt);
        ci.cancel();
    }
    //#endif
}
