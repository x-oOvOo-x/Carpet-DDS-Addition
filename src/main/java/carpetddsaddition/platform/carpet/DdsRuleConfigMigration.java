/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import carpetddsaddition.core.rules.DdsRule;
import carpetddsaddition.core.rules.DdsRuleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** One-way, idempotent migration of CDA keys out of the legacy carpet.conf. */
final class DdsRuleConfigMigration {
    private static final Logger LOGGER = LogManager.getLogger("dds.rules.migration");

    private DdsRuleConfigMigration() {}

    static void migrate(Path legacyFile, Path canonicalFile, DdsRuleRegistry registry) {
        if (!Files.isRegularFile(legacyFile)) return;

        Set<String> names = new LinkedHashSet<String>();
        for (DdsRule<?> rule : registry.all()) names.add(rule.name());

        try {
            List<String> legacyLines = Files.readAllLines(legacyFile, StandardCharsets.UTF_8);
            List<String> keptLegacy = new ArrayList<String>();
            Map<String, String> migrated = new LinkedHashMap<String, String>();
            boolean legacyLocked = false;

            for (String line : legacyLines) {
                String trimmed = line.trim();
                if ("locked".equalsIgnoreCase(trimmed)) legacyLocked = true;

                String[] fields = fields(line);
                if (fields != null && names.contains(fields[0])) {
                    migrated.put(fields[0], fields[1]);
                } else {
                    keptLegacy.add(line);
                }
            }
            // A global Carpet lock previously protected CDA even when no CDA
            // override was saved. Preserve that protection on first migration.
            if (migrated.isEmpty()) {
                if (legacyLocked && !Files.isRegularFile(canonicalFile))
                    writeReplace(canonicalFile, java.util.Collections.singletonList("locked"));
                return;
            }

            boolean canonicalExisted = Files.isRegularFile(canonicalFile);
            List<String> canonical = canonicalExisted
                    ? Files.readAllLines(canonicalFile, StandardCharsets.UTF_8)
                    : new ArrayList<String>();
            if (!canonicalExisted && legacyLocked) canonical.add("locked");
            Set<String> canonicalKeys = new LinkedHashSet<String>();
            for (String line : canonical) {
                String[] fields = fields(line);
                if (fields != null) canonicalKeys.add(fields[0]);
            }
            for (Map.Entry<String, String> entry : migrated.entrySet()) {
                if (!canonicalKeys.contains(entry.getKey()))
                    canonical.add(entry.getKey() + " " + entry.getValue());
            }

            writeReplace(canonicalFile, canonical);
            writeReplace(legacyFile, keptLegacy);
            LOGGER.info("Migrated {} CDA rule override(s) from {} to {}",
                    migrated.size(), legacyFile.getFileName(), canonicalFile.getFileName());
        } catch (IOException e) {
            LOGGER.error("Failed to migrate CDA rule configuration; legacy file was left usable", e);
        }
    }

    private static String[] fields(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#") || "locked".equalsIgnoreCase(trimmed))
            return null;
        String[] fields = trimmed.split("\\s+", 2);
        return fields.length == 2 ? fields : null;
    }

    private static void writeReplace(Path file, List<String> lines) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Path temp = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");
        try {
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}