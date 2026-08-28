/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.bootstrap;

import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessServerNetwork;
import carpetddsaddition.feature.undoredo.network.UndoRedoServerNetwork;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
import carpetddsaddition.network.DdsPacketRegistry;

/** Composition root for feature and integration packet registration. */
public final class DdsNetworkBootstrap {
    private static boolean initialized;
    private DdsNetworkBootstrap() {}

    public static synchronized void initialize() {
        if (initialized) return;
        QuickContainerAccessServerNetwork.registerPackets();
        UndoRedoServerNetwork.registerPackets();
        DdsFakePlayerServerNetwork.registerPackets();
        DdsPacketRegistry.freeze();
        initialized = true;
    }
}
