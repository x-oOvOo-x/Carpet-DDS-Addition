package carpetddsaddition.platform.carpet;

import carpetddsaddition.core.rules.DdsRule;
import carpetddsaddition.core.rules.DdsRuleCodecs;
import carpetddsaddition.core.rules.DdsRuleRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class DdsRuleConfigMigrationTest {
    @TempDir Path temp;

    @Test
    void migratesAllKnownCdaKeysAndKeepsCarpetLock() throws Exception {
        Path legacy = temp.resolve("carpet.conf");
        Path canonical = temp.resolve("carpet-dds-addition.conf");
        Files.write(legacy, List.of(
                "locked",
                "# keep comment",
                "carpetRule true",
                "rightClickHarvest true",
                "retiredRule true"
        ), StandardCharsets.UTF_8);

        DdsRuleRegistry registry = registry();
        DdsRuleConfigMigration.migrate(legacy, canonical, registry);

        List<String> oldLines = Files.readAllLines(legacy, StandardCharsets.UTF_8);
        assertEquals(List.of("locked", "# keep comment", "carpetRule true"), oldLines);
        assertEquals(List.of("locked", "rightClickHarvest true", "retiredRule true"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));
    }

    @Test
    void lockOnlyLegacyConfigStillProtectsCdaOnFirstMigration() throws Exception {
        Path legacy = temp.resolve("carpet.conf");
        Path canonical = temp.resolve("carpet-dds-addition.conf");
        Files.write(legacy, List.of("locked", "# main Carpet remains locked"),
                StandardCharsets.UTF_8);

        DdsRuleConfigMigration.migrate(legacy, canonical, registry());

        assertEquals(List.of("locked", "# main Carpet remains locked"),
                Files.readAllLines(legacy, StandardCharsets.UTF_8));
        assertEquals(List.of("locked"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));

        DdsRuleConfigMigration.migrate(legacy, canonical, registry());
        assertEquals(List.of("locked"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));
    }

    @Test
    void existingCanonicalConfigDoesNotInheritLegacyLockWithoutOverrides() throws Exception {
        Path legacy = temp.resolve("carpet.conf");
        Path canonical = temp.resolve("carpet-dds-addition.conf");
        Files.write(legacy, List.of("locked"), StandardCharsets.UTF_8);
        Files.write(canonical, List.of("rightClickHarvest false"), StandardCharsets.UTF_8);

        DdsRuleConfigMigration.migrate(legacy, canonical, registry());

        assertEquals(List.of("locked"), Files.readAllLines(legacy, StandardCharsets.UTF_8));
        assertEquals(List.of("rightClickHarvest false"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));
    }

    @Test
    void existingCanonicalConfigKeepsItsOwnLockState() throws Exception {
        Path legacy = temp.resolve("carpet.conf");
        Path canonical = temp.resolve("carpet-dds-addition.conf");
        Files.write(legacy, List.of("locked", "rightClickHarvest true"), StandardCharsets.UTF_8);
        Files.write(canonical, List.of("rightClickHarvest false"), StandardCharsets.UTF_8);

        DdsRuleConfigMigration.migrate(legacy, canonical, registry());

        assertEquals(List.of("locked"), Files.readAllLines(legacy, StandardCharsets.UTF_8));
        assertEquals(List.of("rightClickHarvest false"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));
    }

    @Test
    void canonicalValueWinsAndMigrationIsIdempotent() throws Exception {
        Path legacy = temp.resolve("carpet.conf");
        Path canonical = temp.resolve("carpet-dds-addition.conf");
        Files.write(legacy, List.of("rightClickHarvest true"), StandardCharsets.UTF_8);
        Files.write(canonical, List.of("rightClickHarvest false"), StandardCharsets.UTF_8);

        DdsRuleRegistry registry = registry();
        DdsRuleConfigMigration.migrate(legacy, canonical, registry);
        DdsRuleConfigMigration.migrate(legacy, canonical, registry);

        assertTrue(Files.readAllLines(legacy, StandardCharsets.UTF_8).isEmpty());
        assertEquals(List.of("rightClickHarvest false"),
                Files.readAllLines(canonical, StandardCharsets.UTF_8));
    }

    private static DdsRuleRegistry registry() {
        DdsRuleRegistry registry = new DdsRuleRegistry();
        registry.register(DdsRule.owned(
                "rightClickHarvest", Boolean.class, Boolean.FALSE,
                new String[]{"DDS"}, new String[]{"true", "false"},
                true, DdsRuleCodecs.BOOLEAN
        ));
        registry.register(DdsRule.unavailable(
                "retiredRule", Boolean.class, Boolean.FALSE,
                new String[]{"DDS"}, new String[]{"true", "false"},
                true, DdsRuleCodecs.BOOLEAN
        ));
        registry.freeze();
        return registry;
    }
}