package carpetddsaddition.integration.gca.fakeplayer;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/** P8-A1: legacy menu deletion must not weaken GCA denial or strict Provider sessions. */
final class DdsFakePlayerMenuContextContractTest {
    private static final Path ROOT=findRoot(), JAVA=ROOT.resolve("src/main/java");

    @Test void oldRuleAndDedicatedServerMenusArePhysicallyRetired() throws IOException {
        assertFalse(Files.readString(ROOT.resolve("src/main/rules/rules.json"))
                .contains("\"name\": \"fakePlayerInventory\""));
        for (String path : new String[]{
                "carpetddsaddition/feature/fakeplayerinventory/FakePlayerInventory.java",
                "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerAccess.java",
                "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerMenu.java",
                "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerEnderChestMenu.java",
                "carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerEquipmentSlot.java",
                "carpetddsaddition/integration/gca/fakeplayer/DdsMappedContainer.java"})
            assertFalse(Files.exists(JAVA.resolve(path)), path);
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        assertFalse(network.contains("sendOpenMenu("));
        assertFalse(network.contains("DdsFakePlayerPackets.OPEN_MENU_S2C"));
        assertFalse(read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerPackets.java")
                .contains("fake_player_open_menu_s2c"));
        assertTrue(network.contains("DdsFakePlayerPackets.CONTEXT_S2C"));
        assertTrue(network.contains("DdsFakePlayerPackets.STATE_S2C"));
    }

    @Test void existingProviderContextAndCanonicalEnderRemainsIndependentOfRetiredMenu() throws IOException {
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        String context=network.substring(
                network.indexOf("public static void sendContextFromMenu("),
                network.indexOf("private static boolean sendContext("));
        assertTrue(context.contains("PlayerInventoryExtension.enabled()"));
        assertTrue(context.contains("DdsPlayerInventorySessions.prepare(viewer,menu)"));
        assertTrue(context.contains("doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C)"));
        assertTrue(context.contains("sendContext(viewer,session)"));
        assertFalse(context.contains("DdsFakePlayerPackets.OPEN_MENU_S2C"));
        String ender=read("carpetddsaddition/integration/playerinventory/DdsPlayerInventoryEnderNetwork.java");
        assertTrue(ender.contains("DdsFakePlayerPackets.CANONICAL_ENDER_C2S"));
        assertTrue(ender.contains("DdsFakePlayerPackets.STRUCTURE_RESULT_S2C"));
    }

    @Test void deniedSneakEnderIsTerminalWithoutEitherLegacyRuleOrExtension() throws IOException {
        String guard=read("carpetddsaddition/integration/gca/fakeplayer/DdsGcaDeniedEnderIntentGuard.java");
        for (String required : new String[]{
                "public static boolean shouldConsume(ServerPlayer viewer, EntityPlayerMPFake target)",
                "viewer.isShiftKeyDown()", "!viewer.isSpectator()", "target.isAlive()",
                "DdsGcaFakePlayerBridge.shouldSuppressSneakControllerFallback(viewer)"})
            assertTrue(guard.contains(required), required);
        assertFalse(guard.contains("FakePlayerInventory"));
        assertFalse(guard.contains("PlayerInventoryExtension"));
        String bridge=read("carpetddsaddition/integration/gca/fakeplayer/DdsGcaFakePlayerBridge.java");
        String denied=bridge.substring(
                bridge.indexOf("public static boolean shouldSuppressSneakControllerFallback("),
                bridge.indexOf("private static boolean gcaEnderChestRuleAllows("));
        for (String required : new String[]{
                "SAFE_DIRECT_INVENTORY", "OPEN_ENDER_CHEST_RULE_FIELD != null",
                "isGcaInventoryRuleEnabled()", "!gcaEnderChestRuleAllows(viewer)"})
            assertTrue(denied.contains(required), required);
        assertFalse(denied.contains("supportsEnhancedMenu("));
        String mixin=read("carpetddsaddition/mixin/integration/carpet/gca/FakePlayerInteractionMixin.java");
        assertTrue(mixin.contains("if (!DdsGcaDeniedEnderIntentGuard.shouldConsume(viewer, target)) return;"));
        assertFalse(mixin.contains("DdsFakePlayerAccess"));
        assertTrue(mixin.contains("cir.setReturnValue(InteractionResult.CONSUME)"));
    }

    @Test void oldMinecraftSourcesRemainFullyReplayModGuarded() throws IOException {
        for (String path : new String[]{
                "carpetddsaddition/integration/gca/fakeplayer/DdsGcaDeniedEnderIntentGuard.java",
                "carpetddsaddition/mixin/integration/carpet/gca/FakePlayerInteractionMixin.java"}) {
            String source=read(path);
            int start=source.indexOf("//#if MC >= 11904 && MC <= 260300");
            int end=source.indexOf("//#else",start);
            assertTrue(start>=0 && end>start,path);
            for (String line : source.substring(start,end).split("\\R")) {
                if (line.trim().isEmpty()) continue;
                assertTrue(line.startsWith("//#")||line.startsWith("//$$"),path+": "+line);
            }
        }
    }

    @Test void strictActionTransportRejectsUnboundFormerMenuRequests() throws IOException {
        String actions=read("carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");
        String handler=actions.substring(
                actions.indexOf("public static void handleRequest("),
                actions.indexOf("public static void handleProviderRequest("));
        assertTrue(handler.contains("current.stillValid(viewer)"));
        assertTrue(handler.contains("handleStrictSessionRequest(viewer, current, action, value, data)"));
        assertFalse(handler.contains("DdsFakePlayerMenu"));
        assertFalse(handler.contains("DdsFakePlayerEnderChestMenu"));
        assertTrue(actions.contains("case DROP_ALL: dropInventory(target); break;"));
        String network=read("carpetddsaddition/integration/gca/fakeplayer/network/DdsFakePlayerServerNetwork.java");
        assertTrue(network.contains("STRICT_ENDER_ACTIONS"));
        assertTrue(network.contains("INVENTORY_ACTIONS"));
        assertFalse(network.contains("private static final String ENDER_ACTIONS="));
    }

    private static String read(String path)throws IOException{return Files.readString(JAVA.resolve(path));}
    private static Path findRoot(){
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("repo root");
    }
}
