/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

/** Opt-in server-side diagnostics; never changes drop attribution. */
public final class DirectDropTrace {
    private static final boolean ENABLED = Boolean.getBoolean("dds.directBlockDrops.trace");

    private DirectDropTrace() {}

    public static boolean enabled() { return ENABLED; }

    public static void log(String event, String message) {
        if (ENABLED) System.out.println("[CDA/directBlockDrops] " + event + " " + message);
    }
}
