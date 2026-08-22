/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import net.minecraft.world.CompoundContainer;
//$$ import net.minecraft.world.Container;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(CompoundContainer.class)
//$$ public interface CompoundContainerAccessor {
//$$     @Accessor("container1") Container dds$getFirstContainer();
//$$     @Accessor("container2") Container dds$getSecondContainer();
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoCompoundContainerTarget")
public interface CompoundContainerAccessor {}
//#endif
