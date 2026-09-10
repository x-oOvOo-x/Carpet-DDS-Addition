/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.candlefreeplacement;

import carpetddsaddition.generated.DDSRules;

/** Business-level state for the candleFreePlacement rule. */
public final class CandleFreePlacement {
    private CandleFreePlacement() {}
    public static boolean enabled(){return DDSRules.candleFreePlacement();}
}
