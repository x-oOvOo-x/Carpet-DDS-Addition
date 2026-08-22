/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoOriginAccess;
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScheduledTickAccess;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ScheduledTick.class)
//$$ public abstract class ScheduledTickMixin implements UndoOriginAccess, UndoScheduledTickAccess {
//$$     @Unique private long dds$undoOriginId;
//$$     @Unique private long dds$undoPauseBaseline;
//$$     @Override public long dds$getUndoOriginId() { return dds$undoOriginId; }
//$$     @Override public void dds$setUndoOriginId(long recordId) { dds$undoOriginId = recordId; }
//$$     @Override public long dds$getUndoPauseBaseline() { return dds$undoPauseBaseline; }
//$$     @Override public void dds$setUndoPauseBaseline(long pauseTicks) { dds$undoPauseBaseline = pauseTicks; }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoScheduledTickTarget")
public abstract class ScheduledTickMixin {}
//#endif
