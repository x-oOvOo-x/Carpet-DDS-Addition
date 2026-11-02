/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsRuleSuggestionContractTest {
    private static Path root() {
        for (Path p = Path.of("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main")))
                return p;
        throw new IllegalStateException("repo root not found");
    }

    private static JsonObject rule(String name) throws IOException {
        JsonObject root = JsonParser.parseString(
                Files.readString(root().resolve("src/main/rules/rules.json"))
        ).getAsJsonObject();
        JsonArray rules = root.getAsJsonArray("rules");
        for (int i = 0; i < rules.size(); i++) {
            JsonObject rule = rules.get(i).getAsJsonObject();
            if (name.equals(rule.get("name").getAsString())) return rule;
        }
        throw new AssertionError("missing rule " + name);
    }

    private static void assertSuggestions(String name, String... expected) throws IOException {
        JsonObject rule = rule(name);
        JsonArray values = rule.getAsJsonArray("suggestions");
        List<String> actual = new ArrayList<String>();
        for (int i = 0; i < values.size(); i++) actual.add(values.get(i).getAsString());

        assertEquals(Arrays.asList(expected), actual);
        assertFalse(rule.get("strict").getAsBoolean(),
                "suggestions must not change existing free-form/comma-combination semantics");
    }

    @Test
    void modeRulesExposeTabSuggestionsWithoutBecomingStrict() throws IOException {
        assertSuggestions("autoRestock", "off", "always", "client");
        assertSuggestions("naturalPassiveSpawning",
                "none", "all", "land", "fish", "squid", "axolotl", "bat", "dolphin");
        assertSuggestions("explosionNoBlockDamage",
                "none", "all", "creeper", "tnt", "tnt_minecart", "end_crystal",
                "ghast", "wither", "wither_skull");
        assertSuggestions("easyMining",
                "false", "all", "obsidian", "crying_obsidian", "deepslate", "ores");
    }

    @Test
    void compatibilityCommandsReadSuggestionsFromTypedRuleMetadata() throws IOException {
        String commands = Files.readString(root().resolve(
                "src/main/java/carpetddsaddition/platform/carpet/DdsRuleCommandCompat.java"));

        assertEquals(2, count(commands, ".suggests((c, b) -> suggest(rule, b))"));
        assertTrue(commands.contains("cda.then(ruleCommand(dispatcher, rule))"));
        assertTrue(commands.contains("carpet.then(ruleCommand(dispatcher, rule))"));
    }

    private static int count(String text, String value) {
        int result = 0;
        for (int at = 0; (at = text.indexOf(value, at)) >= 0; at += value.length()) result++;
        return result;
    }
}
