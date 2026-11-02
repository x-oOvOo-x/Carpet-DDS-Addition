/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

import net.minecraft.server.MinecraftServer;

/** A typed, committed DDS rule transition. */
public final class DdsRuleChange<T> {
    public enum Origin {
        COMMAND,
        CONFIG_LOAD,
        CONFIG_RELOAD,
        MIGRATION,
        INTERNAL,
        LEGACY_BACKEND,
        UNKNOWN
    }

    private final DdsRule<T> rule;
    private final T oldValue;
    private final T newValue;
    private final Origin origin;
    private final MinecraftServer server;

    DdsRuleChange(DdsRule<T> rule, T oldValue, T newValue, Origin origin, MinecraftServer server) {
        this.rule = rule;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.origin = origin;
        this.server = server;
    }

    public DdsRule<T> rule() { return rule; }
    public T oldValue() { return oldValue; }
    public T newValue() { return newValue; }
    public Origin origin() { return origin; }
    public MinecraftServer server() { return server; }
}
