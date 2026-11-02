/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.feature.playerinventoryextension;

import carpetddsaddition.generated.DDSRules;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
import carpetddsaddition.network.DdsServerNetwork;
import net.minecraft.server.MinecraftServer;

/** Server-side gate for optional provider-neutral PlayerInventory session metadata. */
public final class PlayerInventoryExtension {
    private PlayerInventoryExtension() {}

    public static boolean enabled() {
        return DDSRules.playerInventoryExtension();
    }

    public static void onRuleChanged(MinecraftServer server) {
        DdsFakePlayerServerNetwork.onExtensionRuleChanged(server);
        QuickContainerAccess.onRuleChanged();
        DdsServerNetwork.refreshCapabilities(server);
    }
}
