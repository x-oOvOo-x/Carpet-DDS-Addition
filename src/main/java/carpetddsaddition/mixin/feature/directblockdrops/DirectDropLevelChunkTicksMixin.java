/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropAttribution;
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropScheduledTickOwner;
//$$ import net.minecraft.world.level.block.Block;
//$$ import net.minecraft.world.ticks.LevelChunkTicks;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(LevelChunkTicks.class)
//$$ public abstract class DirectDropLevelChunkTicksMixin<T> {
//$$     // scheduleUnchecked is reached only after vanilla's deduplication accepts the tick.
//$$     @Inject(method = "scheduleUnchecked(Lnet/minecraft/world/ticks/ScheduledTick;)V", at = @At("HEAD"))
//$$     private void dds$attachAcceptedDirectDropTick(ScheduledTick<T> tick, CallbackInfo ci) {
//$$         if (!(tick.type() instanceof Block)) return;
//$$         DirectDropAttribution.Owner owner = DirectDropAttribution.ownerForSchedule(tick.pos(), (Block) tick.type());
//$$         if (owner != null) ((DirectDropScheduledTickOwner) (Object) tick)
//$$                 .dds$setDirectDropOwner(owner.player(), owner.startTick(), owner.generation());
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DirectDropLevelChunkTicksTarget")
public abstract class DirectDropLevelChunkTicksMixin {}
//#endif
