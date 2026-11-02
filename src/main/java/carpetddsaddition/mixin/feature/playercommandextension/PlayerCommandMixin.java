/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.playercommandextension;

import carpet.commands.PlayerCommand;
import carpet.helpers.EntityPlayerActionPack;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

/**
 * Completes Carpet's six ActionType commands with an exact stop child.
 *
 * <p>The handler deliberately reuses PlayerCommand.manipulation so commandPlayer,
 * target lookup and cantManipulate semantics remain Carpet-owned. start(type, null)
 * removes only the selected action and runs Carpet's normal stop cleanup.</p>
 */
@Mixin(value = PlayerCommand.class, remap = false)
public abstract class PlayerCommandMixin {
    @Shadow
    private static Command<CommandSourceStack> manipulation(Consumer<EntityPlayerActionPack> action) {
        throw new AssertionError();
    }

    @Inject(method = "makeActionCommand", at = @At("RETURN"))
    private static void dds$addPreciseStop(
            String actionName,
            EntityPlayerActionPack.ActionType type,
            CallbackInfoReturnable<LiteralArgumentBuilder<CommandSourceStack>> cir
    ) {
        LiteralArgumentBuilder<CommandSourceStack> action = cir.getReturnValue();
        if (action == null || action.build().getChild("stop") != null) return;
        action.then(LiteralArgumentBuilder.<CommandSourceStack>literal("stop")
                .executes(manipulation(pack -> pack.start(type, null))));
    }
}
