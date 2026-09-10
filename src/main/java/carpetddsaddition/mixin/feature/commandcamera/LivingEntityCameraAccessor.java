/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.commandcamera;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11601 && MC <= 260200
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 11601 && MC <= 260200
//$$ @Mixin(LivingEntity.class)
//$$ public interface LivingEntityCameraAccessor {
//$$     @Accessor("fallFlyTicks") int carpetDDSAddition$getFallFlyingTicks();
//$$     @Accessor("fallFlyTicks") void carpetDDSAddition$setFallFlyingTicks(int ticks);
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CommandCameraLivingEntityTarget")
public interface LivingEntityCameraAccessor {}
//#endif
