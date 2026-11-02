package carpetddsaddition.integration.playerinventory;

import carpetddsaddition.core.lifecycle.DdsLifecycleComponent;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
import net.minecraft.server.MinecraftServer;

/** Maintains negotiated player-inventory session metadata and authoritative fake-player state. */
public final class DdsPlayerInventorySessionLifecycle implements DdsLifecycleComponent {
    public static final DdsPlayerInventorySessionLifecycle INSTANCE=new DdsPlayerInventorySessionLifecycle();
    private DdsPlayerInventorySessionLifecycle(){}
    @Override public void onServerLoaded(MinecraftServer server){DdsFakePlayerServerNetwork.clearRuntimeState();}
    @Override public void onTick(MinecraftServer server){DdsFakePlayerServerNetwork.tick(server);}
    @Override public void onServerClosed(MinecraftServer server){DdsFakePlayerServerNetwork.clearRuntimeState();}
}
