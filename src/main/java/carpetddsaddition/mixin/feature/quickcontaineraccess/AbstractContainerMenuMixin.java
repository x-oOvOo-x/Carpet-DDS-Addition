/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    //#if MC >= 11404 && MC <= 260200
    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void dds$validateQuickContainerSession(CallbackInfo ci) {
        QuickContainerAccessCompat.validateMenu((AbstractContainerMenu) (Object) this);
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void dds$finishQuickContainerSession(Player player, CallbackInfo ci) {
        QuickContainerAccessCompat.onMenuRemoved((AbstractContainerMenu) (Object) this, player);
    }
    //#endif
}
