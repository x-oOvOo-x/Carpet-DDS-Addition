/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.naturalpassivespawning;

import carpetddsaddition.generated.DDSRules;
import java.util.Locale;

public final class NaturalPassiveSpawning {
    public enum Group {
        LAND(1 << 0), FISH(1 << 1), SQUID(1 << 2), AXOLOTL(1 << 3), BAT(1 << 4), DOLPHIN(1 << 5);
        private final int bit;
        Group(int bit) { this.bit = bit; }
    }

    private static final int ALL_MASK = Group.LAND.bit | Group.FISH.bit | Group.SQUID.bit | Group.AXOLOTL.bit | Group.BAT.bit | Group.DOLPHIN.bit;
    private static volatile String cachedRaw;
    private static volatile int cachedMask;
    private NaturalPassiveSpawning() {}

    public static boolean prevents(Group group) { return group != null && (mask() & group.bit) != 0; }

    private static int mask() {
        String raw = DDSRules.naturalPassiveSpawning();
        if (raw == null) raw = "none";
        String current = cachedRaw;
        if (raw != current && !raw.equals(current)) synchronized (NaturalPassiveSpawning.class) {
            current = cachedRaw;
            if (raw != current && !raw.equals(current)) {
                cachedMask = parse(raw);
                cachedRaw = raw;
            }
        }
        return cachedMask;
    }

    static int parse(String raw) {
        if (raw == null) return 0;
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || "none".equals(normalized)) return 0;
        if ("all".equals(normalized)) return ALL_MASK;
        int mask = 0;
        for (String rawToken : normalized.split(",", -1)) {
            String token = rawToken.trim();
            if (token.isEmpty()) return 0;
            switch (token) {
                case "land": mask |= Group.LAND.bit; break;
                case "fish": mask |= Group.FISH.bit; break;
                case "squid": mask |= Group.SQUID.bit; break;
                case "axolotl": mask |= Group.AXOLOTL.bit; break;
                case "bat": mask |= Group.BAT.bit; break;
                case "dolphin": mask |= Group.DOLPHIN.bit; break;
                default: return 0; // Invalid input fails safe to vanilla spawning.
            }
        }
        return mask;
    }

    static boolean prevents(int mask, Group group) { return group != null && (mask & group.bit) != 0; }
    static int allMask() { return ALL_MASK; }
}
