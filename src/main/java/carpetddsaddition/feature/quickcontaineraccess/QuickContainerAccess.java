/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess;

import carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat;
import carpetddsaddition.generated.DDSRules;

public final class QuickContainerAccess {
    private QuickContainerAccess() {}
    public static boolean enabled() { return DDSRules.quickContainerAccess(); }
    public static void onRuleChanged() { if (!enabled()) QuickContainerAccessCompat.closeActiveMenus(); }
}
