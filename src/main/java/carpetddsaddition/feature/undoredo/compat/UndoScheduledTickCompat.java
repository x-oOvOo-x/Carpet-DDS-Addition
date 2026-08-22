/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.undoredo.compat;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.UndoRedo;
//$$ import net.minecraft.world.ticks.ScheduledTick;
//#endif

/**
 * Scheduled-tick integration for Undo/Redo.
 *
 * Minecraft and optimization-mod Mixins should only report a successfully
 * scheduled tick here. Origin ownership and asynchronous pause state are
 * maintained centrally by this compatibility layer.
 */
public final class UndoScheduledTickCompat {

    private UndoScheduledTickCompat() {
    }

    //#if MC >= 12109

    //$$ public static void attachOrigin(
    //$$         ScheduledTick<?> tick
    //$$ ) {
    //$$     UndoOriginAccess originAccess =
    //$$             (UndoOriginAccess) (Object) tick;
    //$$
    //$$     /*
    //$$      * The same tick may pass through more than one compatibility
    //$$      * hook. Origin assignment must therefore be idempotent.
    //$$      */
    //$$     if (originAccess.dds$getUndoOriginId() != 0L
    //$$             || !UndoRedo.enabled()) {
    //$$         return;
    //$$     }
    //$$
    //$$     long recordId =
    //$$             UndoAsyncFreeze.forcedOriginId();
    //$$
    //$$     if (recordId == 0L) {
    //$$         recordId =
    //$$                 UndoAsyncOrigin.captureOriginId();
    //$$     }
    //$$
    //$$     if (recordId == 0L) {
    //$$         return;
    //$$     }
    //$$
    //$$     originAccess.dds$setUndoOriginId(
    //$$             recordId
    //$$     );
    //$$
    //$$     ((UndoScheduledTickAccess) (Object) tick)
    //$$             .dds$setUndoPauseBaseline(
    //$$                     UndoAsyncFreeze.pauseTicks(
    //$$                             recordId
    //$$                     )
    //$$             );
    //$$ }

    //#endif
}