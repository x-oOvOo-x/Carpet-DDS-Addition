/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoScheduledTickCompat;
//$$ import net.minecraft.world.ticks.LevelChunkTicks;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import java.util.function.BiConsumer;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(LevelChunkTicks.class)
//$$ public abstract class LevelChunkTicksMixin<T> {
//$$     @Inject(method = "scheduleUnchecked(Lnet/minecraft/world/ticks/ScheduledTick;)V", at = @At("HEAD"))
//$$     private void dds$attachUndoOriginVanilla(ScheduledTick<T> tick, CallbackInfo ci) {
//$$         UndoScheduledTickCompat.attachOrigin(tick);
//$$     }
//$$
//$$     @ModifyVariable(method = "setOnTickAdded(Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), argsOnly = true)
//$$     private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> dds$wrapOnTickAdded(
//$$             BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> consumer) {
//$$         if (consumer == null) return null;
//$$         return (container, tick) -> {
//$$             UndoScheduledTickCompat.attachOrigin(tick);
//$$             consumer.accept(container, tick);
//$$         };
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoLevelChunkTicksTarget")
public abstract class LevelChunkTicksMixin {}
//#endif