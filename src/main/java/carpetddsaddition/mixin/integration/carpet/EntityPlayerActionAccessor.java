/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.integration.carpet;

//#if MC >= 11902 && MC <= 260200
//$$ import carpet.helpers.EntityPlayerActionPack;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//$$
//$$ @Mixin(value = EntityPlayerActionPack.Action.class, remap = false)
//$$ public interface EntityPlayerActionAccessor {
//$$     @Accessor("isContinuous") boolean dds$isContinuous();
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.EntityPlayerActionTarget")
public interface EntityPlayerActionAccessor {}
//#endif
