/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate;

import carpetddsaddition.feature.stonecuttercutsdeepslate.compat.StonecutterCutsDeepslateCompat;
import net.minecraft.server.MinecraftServer;

public final class StonecutterCutsDeepslate {
    private StonecutterCutsDeepslate() {}
    public static void refresh(MinecraftServer server, boolean force) { StonecutterCutsDeepslateCompat.refresh(server, force); }
    public static void reset() { StonecutterCutsDeepslateCompat.reset(); }
}
