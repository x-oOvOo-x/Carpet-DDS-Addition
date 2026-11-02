/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.platform.minecraft.recipe;

//#if MC >= 260300
//$$ import net.minecraft.core.Holder;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//$$
//$$ @Mixin(Holder.Reference.class)
//$$ public interface HolderReferenceInvoker {
//$$     @Invoker("bindValue") void carpetDDSAddition$bindValue(Object value);
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets="carpetddsaddition.disabled.HolderReferenceTarget")
public interface HolderReferenceInvoker {}
//#endif
