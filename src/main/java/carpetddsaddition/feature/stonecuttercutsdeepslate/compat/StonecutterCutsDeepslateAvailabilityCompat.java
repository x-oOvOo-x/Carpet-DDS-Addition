/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutsdeepslate.compat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;

//#if MC < 260100
import carpetddsaddition.generated.DDSRules;
//#endif

/** Keeps retired-version checks in compat, outside feature and bootstrap code. */
public final class StonecutterCutsDeepslateAvailabilityCompat {
    private StonecutterCutsDeepslateAvailabilityCompat() {}

    public static boolean enabled() {
        boolean enabled = false;
        //#if MC < 260100
        enabled = DDSRules.stonecutterCutsDeepslate();
        //#endif
        return enabled;
    }

    public static void onRecipeManagerApplied(RecipeManager manager) {
        //#if MC < 260100
        StonecutterCutsDeepslateCompat.onRecipeManagerApplied(manager);
        //#endif
    }

    public static void refreshLegacyLifecycle(MinecraftServer server) {
        //#if MC < 260100
        StonecutterCutsDeepslateCompat.refreshLegacyLifecycle(server);
        //#endif
    }

    public static void refresh(MinecraftServer server, boolean force) {
        //#if MC < 260100
        StonecutterCutsDeepslateCompat.refresh(server, force);
        //#endif
    }

    public static void reset() {
        //#if MC < 260100
        StonecutterCutsDeepslateCompat.reset();
        //#endif
    }
}
