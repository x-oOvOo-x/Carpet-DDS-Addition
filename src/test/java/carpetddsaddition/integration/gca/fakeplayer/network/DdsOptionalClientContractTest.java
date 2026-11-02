/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class DdsOptionalClientContractTest {
    private static final Path ROOT=findRepositoryRoot();
    @Test void metadataKeepsDdsOptionalOnClients() throws IOException {String m=read("src/main/resources/fabric.mod.json");assertTrue(m.contains("\"environment\": \"*\""));assertFalse(m.contains("DdsFabricClientInitializer"));assertFalse(m.toLowerCase().contains("fabric-api"));}
    @Test void serverHasNoCustomMenuRegistryEntries() throws IOException {assertFalse(Files.exists(ROOT.resolve("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerMenus.java")));String mixins=read("src/main/resources/carpet-dds-addition.mixins.json");assertFalse(mixins.contains("DdsFakePlayerMenuRegistryMixin"));try(var sources=Files.walk(ROOT.resolve("src/main/java"))){assertFalse(sources.filter(p->p.toString().endsWith(".java")).map(DdsOptionalClientContractTest::readUnchecked).anyMatch(t->t.contains("Registry.register(BuiltInRegistries.MENU")));}}
    @Test void enhancedOpenProtocolRemainsServerSide() throws IOException {assertFalse(Files.exists(ROOT.resolve("gradle/fake-player-ui-resource-pack.gradle")));assertFalse(Files.exists(ROOT.resolve("src/main/java/carpetddsaddition/integration/gca/fakeplayer/client")));String network=read("src/main/java/carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");assertTrue(network.contains("doesClientSupportS2C"));assertFalse(network.contains("DdsFakePlayerPackets.OPEN_MENU_S2C"));assertTrue(network.contains("registerS2C(DdsFakePlayerPackets.CONTEXT_S2C, false)"));assertTrue(network.contains("registerS2C(DdsFakePlayerPackets.STATE_S2C, false)"));}
    private static String read(String p)throws IOException{return Files.readString(ROOT.resolve(p));}private static String readUnchecked(Path p){try{return Files.readString(p);}catch(IOException e){throw new IllegalStateException(e);}}
    private static Path findRepositoryRoot(){for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;throw new IllegalStateException("Unable to locate repository root");}
}
