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
import carpetddsaddition.feature.dynamicrecipes.UnifiedRecipeLifecycle;
import carpetddsaddition.feature.dynamicrecipes.compat.UnifiedRecipeCoordinatorCompat;
import carpetddsaddition.feature.commandcamera.CommandCameraLifecycle;
import carpetddsaddition.feature.directblockdrops.DirectBlockDrops;
import carpetddsaddition.feature.directblockdrops.DirectBlockDropsLifecycle;
import carpetddsaddition.feature.enhancednametag.EnhancedNameTagLifecycle;
import carpetddsaddition.feature.mendingabsorbxp.MendingAbsorbXpLifecycle;
import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestock;
import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestockLifecycle;
import carpetddsaddition.feature.playershulkerrestock.PlayerShulkerRestockNetwork;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccessLifecycle;
import carpetddsaddition.feature.stonecuttercutsdeepslate.StonecutterCutsDeepslateLifecycle;
import carpetddsaddition.feature.stonecuttercutswood.StonecutterCutsWoodLifecycle;
import carpetddsaddition.feature.undoredo.UndoRedo;
import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessionLifecycle;
import carpetddsaddition.network.DdsNetworkLifecycle;
import carpetddsaddition.network.DdsServerNetwork;

/** Composition root for DDS runtime lifecycle components and rule-change observers. */
public final class DdsRuntimeBootstrap {
    private static boolean initialized;
    private DdsRuntimeBootstrap() {}

    public static synchronized void initialize() {
        if (initialized) return;
        DdsLifecycle.configure(
                DdsRuleEventsLifecycle.INSTANCE,DdsNetworkLifecycle.INSTANCE,DirectBlockDropsLifecycle.INSTANCE,
                MendingAbsorbXpLifecycle.INSTANCE,QuickContainerAccessLifecycle.INSTANCE,CommandCameraLifecycle.INSTANCE,
                EnhancedNameTagLifecycle.INSTANCE,
                StonecutterCutsDeepslateLifecycle.INSTANCE,
                StonecutterCutsWoodLifecycle.INSTANCE,
                UnifiedRecipeLifecycle.INSTANCE,
                DdsPlayerInventorySessionLifecycle.INSTANCE,
                PlayerShulkerRestockLifecycle.INSTANCE
        );
        DDSRules.STONECUTTER_CUTS_DEEPSLATE.onChanged(
                change -> UnifiedRecipeCoordinatorCompat.onRuleChanged(change.server()));
        DDSRules.STONECUTTER_CUTS_WOOD.onChanged(
                change -> UnifiedRecipeCoordinatorCompat.onRuleChanged(change.server()));
        DDSRules.SMELT_CONCRETE_POWDER_TO_GLASS.onChanged(
                change -> UnifiedRecipeCoordinatorCompat.onRuleChanged(change.server()));
        DDSRules.DDS_NETWORK_PROTOCOL.onChanged(change -> {
            DdsServerNetwork.syncProtocolRuleState(change.server());
            PlayerShulkerRestockNetwork.reset();
        });
        DDSRules.DIRECT_BLOCK_DROPS.onChanged(change -> DirectBlockDrops.onRuleChanged());
        DDSRules.QUICK_CONTAINER_ACCESS.onChanged(change -> {
            QuickContainerAccess.onRuleChanged();
            DdsServerNetwork.refreshCapabilities(change.server());
        });
        DDSRules.PLAYER_INVENTORY_EXTENSION.onChanged(
                change -> PlayerInventoryExtension.onRuleChanged(change.server()));
        DDSRules.UNDO_REDO.onChanged(change -> {
            UndoRedo.onRuleChanged();
            DdsServerNetwork.refreshCapabilities(change.server());
        });

        // Preserve the historical derived-mode callback count for ALWAYS <-> CLIENT transitions.
        DdsRuleEvents.configure(
                DdsRuleEvents.observe(
                        () -> PlayerShulkerRestock.mode() == PlayerShulkerRestock.Mode.ALWAYS,
                        PlayerShulkerRestockNetwork::onRuleChanged),
                DdsRuleEvents.observe(
                        () -> PlayerShulkerRestock.mode() == PlayerShulkerRestock.Mode.CLIENT,
                        PlayerShulkerRestockNetwork::onRuleChanged)
        );
        initialized=true;
    }
}