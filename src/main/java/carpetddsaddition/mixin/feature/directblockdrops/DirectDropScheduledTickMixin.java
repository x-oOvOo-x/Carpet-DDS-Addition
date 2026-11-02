/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropScheduledTickOwner;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ScheduledTick.class)
//$$ public abstract class DirectDropScheduledTickMixin implements DirectDropScheduledTickOwner {
//$$     @Unique private ServerPlayer dds$directDropPlayer;
//$$     @Unique private long dds$directDropStartTick;
//$$     @Unique private long dds$directDropGeneration;
//$$     @Override public ServerPlayer dds$getDirectDropPlayer() { return dds$directDropPlayer; }
//$$     @Override public long dds$getDirectDropStartTick() { return dds$directDropStartTick; }
//$$     @Override public long dds$getDirectDropGeneration() { return dds$directDropGeneration; }
//$$     @Override public void dds$setDirectDropOwner(ServerPlayer player, long startTick, long generation) {
//$$         dds$directDropPlayer = player;
//$$         dds$directDropStartTick = startTick;
//$$         dds$directDropGeneration = generation;
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DirectDropScheduledTickTarget")
public abstract class DirectDropScheduledTickMixin {}
//#endif
