/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.bootstrap;

import carpetddsaddition.core.lifecycle.DdsLifecycle;
import carpetddsaddition.core.rules.DdsRuleEvents;
import carpetddsaddition.core.rules.DdsRuleEventsLifecycle;
import carpetddsaddition.feature.commandcamera.CommandCameraLifecycle;
import carpetddsaddition.feature.directblockdrops.DirectBlockDrops;
import carpetddsaddition.feature.directblockdrops.DirectBlockDropsLifecycle;
import carpetddsaddition.feature.enhancednametag.EnhancedNameTagLifecycle;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccessLifecycle;
import carpetddsaddition.feature.stonecuttercutsdeepslate.StonecutterCutsDeepslate;
import carpetddsaddition.feature.stonecuttercutsdeepslate.StonecutterCutsDeepslateLifecycle;
import carpetddsaddition.feature.undoredo.UndoRedo;
import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.network.DdsNetworkLifecycle;
import carpetddsaddition.network.DdsServerNetwork;

/** Composition root for DDS runtime lifecycle components and rule-change observers. */
public final class DdsRuntimeBootstrap {
    private static boolean initialized;
    private DdsRuntimeBootstrap() {}

    public static synchronized void initialize() {
        if (initialized) return;
        DdsLifecycle.configure(
                DdsRuleEventsLifecycle.INSTANCE, DdsNetworkLifecycle.INSTANCE, DirectBlockDropsLifecycle.INSTANCE,
                QuickContainerAccessLifecycle.INSTANCE, CommandCameraLifecycle.INSTANCE, EnhancedNameTagLifecycle.INSTANCE,
                StonecutterCutsDeepslateLifecycle.INSTANCE
        );
        DdsRuleEvents.configure(
                DdsRuleEvents.observe(DDSRules::stonecutterCutsDeepslate, server -> StonecutterCutsDeepslate.refresh(server, false)),
                DdsRuleEvents.observe(DDSRules::ddsNetworkProtocol, DdsServerNetwork::syncProtocolRuleState),
                DdsRuleEvents.observe(DDSRules::directBlockDrops, server -> DirectBlockDrops.onRuleChanged()),
                DdsRuleEvents.observe(DDSRules::quickContainerAccess, server -> QuickContainerAccess.onRuleChanged()),
                DdsRuleEvents.observe(DDSRules::undoRedo, server -> UndoRedo.onRuleChanged())
        );
        initialized = true;
    }
}
