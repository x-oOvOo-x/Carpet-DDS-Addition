/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class DdsAvailabilityHelloContractTest {
    private static Path root(){for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;throw new IllegalStateException("repo root not found");}
    private static String read(String path)throws IOException{return Files.readString(root().resolve(path));}

    @Test void helloAdvertisesAuthoritativeAvailabilitySchema() throws IOException {
        String server=read("src/main/java/carpetddsaddition/network/DdsServerNetwork.java");
        assertTrue(server.contains("reply.putInt(\"c2s_availability_schema\", 1)"));
        assertTrue(server.contains("DdsPacketRegistry.availableC2SPackets(listener.player)"));
        assertTrue(server.contains("packet == null || !packet.available(listener.player)"));
        assertTrue(server.contains("public static void refreshCapabilities(MinecraftServer server)"));
    }

    @Test void qcaUndoRuleObserversRefreshCapabilities() throws IOException {
        String bootstrap=read("src/main/java/carpetddsaddition/core/bootstrap/DdsRuntimeBootstrap.java");
        assertTrue(bootstrap.contains("DDSRules.QUICK_CONTAINER_ACCESS.onChanged(change -> {"));
        assertTrue(bootstrap.contains("QuickContainerAccess.onRuleChanged();"));
        assertTrue(bootstrap.contains("DdsServerNetwork.refreshCapabilities(change.server());"));
        assertTrue(bootstrap.contains("DDSRules.UNDO_REDO.onChanged(change -> {"));
        assertTrue(bootstrap.contains("UndoRedo.onRuleChanged();"));
    }

    @Test void qcaUndoRegistrationOwnsOnlyFeatureLevelRulePredicates() throws IOException {
        String qca=read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/network/QuickContainerAccessServerNetwork.java");
        String undo=read("src/main/java/carpetddsaddition/feature/undoredo/network/UndoRedoServerNetwork.java");
        assertTrue(qca.contains("player -> QuickContainerAccess.enabled()"));
        assertTrue(undo.contains("player -> UndoRedo.enabled()"));
        assertFalse(qca.contains("player.isCreative() && QuickContainerAccess.enabled()"));
    }
}