/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.nocampfiredamage;

import carpetddsaddition.generated.DDSRules;

/** Business-level state for the noCampfireDamage rule. */
public final class NoCampfireDamage {
    private NoCampfireDamage() {}
    public static boolean enabled() { return DDSRules.noCampfireDamage(); }
}
