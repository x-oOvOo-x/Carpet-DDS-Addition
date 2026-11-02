/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11904 && MC <= 260300
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$
//$$ /** Denied GCA sneak-Ender intents must never fall back to an inventory controller. */
//$$ public final class DdsGcaDeniedEnderIntentGuard {
//$$     private DdsGcaDeniedEnderIntentGuard() {}
//$$
//$$     public static boolean shouldConsume(ServerPlayer viewer, EntityPlayerMPFake target) {
//$$         return viewer != null && target != null && !viewer.isSpectator()
//$$                 && target.isAlive() && viewer.isShiftKeyDown()
//$$                 && DdsGcaFakePlayerBridge.shouldSuppressSneakControllerFallback(viewer);
//$$     }
//$$ }
//#else
public final class DdsGcaDeniedEnderIntentGuard {
    private DdsGcaDeniedEnderIntentGuard() {}
}
//#endif
