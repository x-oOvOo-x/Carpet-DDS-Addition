/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.rules;

import net.minecraft.server.MinecraftServer;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Dispatches configured boolean rule transitions without owning feature behavior. */
public final class DdsRuleEvents {
    private static Observer[] observers = {};
    private static boolean configured;
    private DdsRuleEvents() {}

    public static synchronized void configure(Observer... values) {
        if (configured) return;
        observers = values.clone();
        configured = true;
    }

    public static Observer observe(BooleanSupplier state, Consumer<MinecraftServer> onChanged) {
        return new Observer(state, onChanged);
    }

    public static void captureCurrentState() {
        for (Observer observer : observers) observer.capture();
    }

    public static void onRuleChanged(MinecraftServer server) {
        for (Observer observer : observers) observer.dispatch(server);
    }

    public static final class Observer {
        private final BooleanSupplier state;
        private final Consumer<MinecraftServer> onChanged;
        private boolean observed, captured;

        private Observer(BooleanSupplier state, Consumer<MinecraftServer> onChanged) {
            this.state = state;
            this.onChanged = onChanged;
        }

        private void capture() {
            observed = state.getAsBoolean();
            captured = true;
        }

        private void dispatch(MinecraftServer server) {
            boolean current = state.getAsBoolean();
            if (!captured) {
                observed = current;
                captured = true;
                return;
            }
            if (observed == current) return;
            observed = current;
            onChanged.accept(server);
        }
    }
}
