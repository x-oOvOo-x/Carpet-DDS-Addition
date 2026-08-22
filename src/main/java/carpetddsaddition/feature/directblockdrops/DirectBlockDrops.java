/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.directblockdrops;

import carpetddsaddition.generated.DDSRules;

public final class DirectBlockDrops {
    private DirectBlockDrops() {}
    public static boolean enabled() { return DDSRules.directBlockDrops(); }
    public static void onRuleChanged() { if (!enabled()) resetRuntimeState(); }
    public static void resetRuntimeState() {
        PendingDirectDropTicks.clearAll();
        DirectDropContext.clear();
        ScheduledDirectDropContext.clear();
    }
}
