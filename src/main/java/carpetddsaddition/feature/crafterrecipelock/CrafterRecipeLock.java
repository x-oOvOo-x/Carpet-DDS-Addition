/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.crafterrecipelock;

import carpetddsaddition.generated.DDSRules;

public final class CrafterRecipeLock {
    private CrafterRecipeLock() {}
    public static boolean enabled() { return DDSRules.crafterRecipeLock(); }
}
