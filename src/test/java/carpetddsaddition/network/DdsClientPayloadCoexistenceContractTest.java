package carpetddsaddition.network;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

final class DdsClientPayloadCoexistenceContractTest {
    private static Path root() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.isRegularFile(p.resolve("settings.json"))) return p;
        throw new IllegalStateException("repository root missing");
    }
    private static String read(String path) throws IOException {
        return Files.readString(root().resolve(path));
    }

    @Test void serverHelloAndPayloadPathPreservedWithoutCdaLogicalClient() throws IOException {
        Path root = root().resolve("src/main/java/carpetddsaddition");
        assertFalse(Files.exists(root.resolve("network/DdsClientNetwork.java")));
        assertFalse(Files.exists(root.resolve("mixin/network/ClientCustomPayloadMixin.java")));
        String server = read("src/main/java/carpetddsaddition/network/DdsServerNetwork.java");
        assertTrue(server.contains("DdsProtocol.HELLO_C2S.equals(packetId)"));
        assertTrue(server.contains("sendPayload(listener, DdsProtocol.HELLO_S2C, reply)"));
        String mixins = read("src/main/resources/carpet-dds-addition.mixins.json");
        assertTrue(mixins.contains("network.ServerboundCustomPayloadPacketMixin"));
        assertTrue(mixins.contains("network.ClientboundCustomPayloadPacketMixin"));
        assertTrue(mixins.contains("network.ServerCustomPayloadMixin"));
        assertTrue(com.google.gson.JsonParser.parseString(mixins).getAsJsonObject().getAsJsonArray("client").size() == 0);
    }
}
