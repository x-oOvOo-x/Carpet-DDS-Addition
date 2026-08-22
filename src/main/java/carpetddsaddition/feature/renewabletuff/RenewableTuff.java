/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.renewabletuff;

import carpetddsaddition.generated.DDSRules;

public final class RenewableTuff {
    private RenewableTuff() {}
    public static boolean enabled() { return DDSRules.renewableTuff(); }
}
