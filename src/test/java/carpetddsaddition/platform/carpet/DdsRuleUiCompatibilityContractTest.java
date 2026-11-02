/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsRuleUiCompatibilityContractTest {
    private static Path root() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main")))
                return p;
        throw new IllegalStateException("repo root not found");
    }

    private static String read(String path) throws IOException {
        return Files.readString(root().resolve(path));
    }

    @Test
    void carpetRootKeepsDdsVersionAndVirtualBrowseEntry() throws IOException {
        String mixin = read("src/main/java/carpetddsaddition/mixin/integration/carpet/SettingsManagerMixin.java");

        assertTrue(mixin.contains("manager != CarpetServer.settingsManager"));
        assertTrue(mixin.contains("categories.add(\"DDS\")"));
        assertTrue(mixin.contains("CarpetDDSAddition.getVersion()"));
    }

    @Test
    void virtualDdsCategoryForwardsWithoutDualRegistration() throws IOException {
        String commands = read("src/main/java/carpetddsaddition/platform/carpet/DdsRuleCommandCompat.java");
        String ruleCompat = read("src/main/java/carpetddsaddition/platform/carpet/CarpetRuleCompat.java");

        assertTrue(commands.contains("literal(\"DDS\")"));
        assertTrue(commands.contains("forward(dispatcher, c.getSource(), \"list\")"));
        assertTrue(ruleCompat.contains("manager.addCarpetRule(new DdsCarpetRule<T>(manager, rule))"));
        assertTrue(ruleCompat.contains("manager.parseSettingsClass(CarpetDDSAdditionSettings.class)"));
    }
}
