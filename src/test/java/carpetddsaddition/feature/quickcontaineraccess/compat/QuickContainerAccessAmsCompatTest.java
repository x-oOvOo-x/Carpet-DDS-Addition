package carpetddsaddition.feature.quickcontaineraccess.compat;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

final class QuickContainerAccessAmsCompatTest {
    @Test
    void cachedAmsStateControlsItsMenuEvenWhenTheRuleRequiresRestart() {
        assertTrue(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(true, true));
        assertTrue(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(true, false));
        assertFalse(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(false, true));
        assertFalse(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(false, false));
    }

    @Test
    void olderAmsSettingsAreUsedWhenTheCachedApiIsUnavailable() {
        assertTrue(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(null, true));
        assertFalse(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(null, false));
        assertFalse(QuickContainerAccessAmsCompat.effectiveShulkerEnabled(null, null));
    }

    @Test
    void bothAmsV263PackageLayoutsAreProbed() throws IOException {
        String source = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessAmsCompat.java");
        assertTrue(source.contains("carpetamsaddition.CarpetAMSAdditionLazySettings"));
        assertTrue(source.contains("carpetamsaddition.CarpetAMSAdditionLazySettings$Rule"));
        assertTrue(source.contains("carpetamsaddition.CarpetAMSAdditionSettings"));
        assertTrue(source.contains("club.mcams.carpet.AmsServerLazySettings"));
        assertTrue(source.contains("club.mcams.carpet.AmsServerLazySettings$Rule"));
        assertTrue(source.contains("club.mcams.carpet.AmsServerSettings"));
        assertTrue(source.contains("if (resolveApi(api)) return"));
        assertTrue(source.contains("getField(\"LARGE_SHULKER_BOX\")"));
        assertTrue(source.contains("getMethod(\"isEnabled\", ruleClass)"));
        assertTrue(source.contains("getField(\"largeShulkerBox\")"));
        assertTrue(source.contains("return cached != null ? cached : Boolean.TRUE.equals(setting)"));
    }

    @Test
    void allActiveMinecraftVersionsAreCoveredByTheAmsCompatibilityGate() throws IOException {
        String source = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessAmsCompat.java");
        assertTrue(source.contains("//#if MC >= 11404 && MC <= 260300"));
        assertFalse(source.contains("MC <= 260200"));
        assertTrue(source.contains("//$$ return 27;"));

        String versions = read("settings.json");
        versions = versions.substring(versions.indexOf("\"activeVersions\""));
        Matcher matcher = Pattern.compile("\"(\\d+)\\.(\\d+)(?:\\.(\\d+))?\"").matcher(versions);
        int count = 0;
        while (matcher.find()) {
            int encoded = Integer.parseInt(matcher.group(1)) * 10_000
                    + Integer.parseInt(matcher.group(2)) * 100
                    + (matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3)));
            assertTrue(encoded >= 11404 && encoded <= 260300, matcher.group() + " is outside the AMS compatibility gate");
            count++;
        }
        assertTrue(count > 0, "no active Minecraft versions found");
    }

    @Test
    void unknownAmsCapacityIsRejectedAnd54SlotItemsArePreserved() throws IOException {
        String compat = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessAmsCompat.java");
        String storage = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java");
        String factory = read("src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessMenuFactory.java");

        assertTrue(compat.contains("if (amsLoaded && cached == null && setting == null) return 0"));
        assertTrue(compat.contains("effectiveShulkerEnabled(cached, setting) ? 54 : 27"));
        assertTrue(storage.contains("return QuickContainerAccessAmsCompat.shulkerSize()"));
        assertTrue(storage.contains("size != 27 && size != 54 || hasStoredItemsBeyond(host, size)"));
        assertTrue(factory.contains("if (openedSize == 27) return new ShulkerBoxMenu"));
        assertTrue(factory.contains("if (openedSize == 54) return new PortableLargeShulkerMenu"));
        assertTrue(factory.contains("MenuType.GENERIC_9x6"));
    }

    private static String read(String path) throws IOException {
        return Files.readString(repositoryRoot().resolve(path));
    }

    private static Path repositoryRoot() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent()) {
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) {
                return p;
            }
        }
        throw new IllegalStateException("Unable to locate repository root");
    }
}
