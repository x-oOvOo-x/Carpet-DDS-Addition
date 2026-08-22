/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.iceplacewater;

import carpetddsaddition.generated.DDSRules;

/** Business state for icePlaceWater; Minecraft API types stay outside this module. */
public final class IcePlaceWater {
    private IcePlaceWater() {}
    public static boolean enabled() { return DDSRules.icePlaceWater(); }
}
