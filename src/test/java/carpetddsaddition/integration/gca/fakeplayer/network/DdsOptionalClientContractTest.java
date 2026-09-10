/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

final class DdsOptionalClientContractTest {
    private static final Path ROOT = findRepositoryRoot();

    @Test void metadataKeepsDdsOptionalOnClients() throws IOException {
        String metadata = read("src/main/resources/fabric.mod.json");
        assertTrue(metadata.contains("\"environment\": \"*\""));
        assertTrue(metadata.contains("\"client\": [\"carpetddsaddition.platform.fabric.DdsFabricClientInitializer\"]"));
        assertFalse(metadata.toLowerCase().contains("fabric-api"));
    }

    @Test void serverHasNoCustomMenuRegistryEntries() throws IOException {
        assertFalse(Files.exists(ROOT.resolve("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerMenus.java")));
        String mixins = read("src/main/resources/carpet-dds-addition.mixins.json");
        assertFalse(mixins.contains("DdsFakePlayerMenuRegistryMixin"));
        try (java.util.stream.Stream<Path> sources = Files.walk(ROOT.resolve("src/main/java"))) {
            assertFalse(sources.filter(path -> path.toString().endsWith(".java")).map(DdsOptionalClientContractTest::readUnchecked)
                    .anyMatch(text -> text.contains("Registry.register(BuiltInRegistries.MENU")));
        }
    }

    @Test void cozyUiAndNegotiatedOpenProtocolArePresent() throws IOException {
        assertTrue(Files.exists(ROOT.resolve("gradle/fake-player-ui-resource-pack.gradle")));
        assertTrue(Files.isDirectory(ROOT.resolve("src/main/resourcepacks/dds_fake_player_ui/assets")));
        Path notice = ROOT.resolve("src/main/resourcepacks/dds_fake_player_ui/NOTICE.txt");
        Path license = ROOT.resolve("src/main/resourcepacks/dds_fake_player_ui/LICENSE-CozyUI-Plus.txt");
        assertTrue(Files.isRegularFile(notice));
        assertTrue(Files.isRegularFile(license));
        assertTrue(Files.readString(notice).contains("零雾〇五 Fogg05"));
        assertTrue(Files.readString(notice).contains("https://github.com/Fogg05/CozyUI-Plus"));
        assertTrue(Files.readString(license).contains("GNU GENERAL PUBLIC LICENSE"));
        String packBuild = read("gradle/fake-player-ui-resource-pack.gradle");
        assertTrue(packBuild.contains("'NOTICE.txt'"));
        assertTrue(packBuild.contains("'LICENSE-CozyUI-Plus.txt'"));
        String initializer = read("src/main/java/carpetddsaddition/platform/fabric/DdsFabricClientInitializer.java");
        assertTrue(initializer.contains("DdsBuiltinResourcePackCompat.register()"));
        String network = read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        assertTrue(network.contains("doesClientSupportS2C"));
        assertTrue(network.contains("registerS2C(DdsFakePlayerPackets.OPEN_MENU_S2C)"));
    }

    private static String read(String relative) throws IOException { return Files.readString(ROOT.resolve(relative)); }
    private static String readUnchecked(Path path) {
        try { return Files.readString(path); }
        catch (IOException exception) { throw new IllegalStateException(exception); }
    }
    private static Path findRepositoryRoot() {
        for (Path current = Path.of("").toAbsolutePath(); current != null; current = current.getParent())
            if (Files.exists(current.resolve("settings.json")) && Files.isDirectory(current.resolve("src/main"))) return current;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
