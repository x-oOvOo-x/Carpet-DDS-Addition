/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.directblockdrops;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.directblockdrops.DirectDropScheduledTickOwner;
//$$ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
//$$ import net.minecraft.world.level.ChunkPos;
//$$ import net.minecraft.world.ticks.LevelChunkTicks;
//$$ import net.minecraft.world.ticks.LevelTicks;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(LevelTicks.class)
//$$ public abstract class DirectDropLevelTicksUnloadMixin<T> {
//$$     @Shadow @Final private Long2ObjectMap<LevelChunkTicks<T>> allContainers;
//$$
//$$     @Inject(method = "removeContainer(Lnet/minecraft/world/level/ChunkPos;)V", at = @At("HEAD"))
//$$     private void dds$revokeDirectDropOwnersOnChunkUnload(ChunkPos pos, CallbackInfo ci) {
    //#if MC >= 260000
    //$$         long key = pos.pack();
    //#else
    //$$         long key = pos.toLong();
    //#endif
//$$             LevelChunkTicks<T> container = allContainers.get(key);
//$$         if (container == null) return;
//$$         container.getAll().forEach(tick -> {
//$$             if (((Object) tick) instanceof DirectDropScheduledTickOwner)
//$$                 ((DirectDropScheduledTickOwner) (Object) tick).dds$setDirectDropOwner(null, 0L, 0L);
//$$         });
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.DirectDropLevelTicksUnloadTarget")
public abstract class DirectDropLevelTicksUnloadMixin {}
//#endif
