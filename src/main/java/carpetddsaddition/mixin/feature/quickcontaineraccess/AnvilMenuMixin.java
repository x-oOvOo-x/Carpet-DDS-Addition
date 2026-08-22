/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 */
package carpetddsaddition.mixin.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
//#if MC >= 11502 && MC <= 260200
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import java.util.function.BiConsumer;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    //#if MC >= 11502 && MC <= 260200
    @Redirect(
            method = "onTake",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"),
            require = 0
    )
    private void dds$portableAnvilDamage(ContainerLevelAccess access, BiConsumer<Level, BlockPos> vanillaAction) {
        if (!QuickContainerAccessCompat.handlePortableAnvilUse((AbstractContainerMenu) (Object) this))
            access.execute(vanillaAction);
    }
    //#endif
}
