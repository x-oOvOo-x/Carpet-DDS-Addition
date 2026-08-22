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
    private static volatile String cachedRaw;
    private static volatile Config cachedConfig = Config.ALL;
    private NaturalPassiveSpawning() {}

    public static boolean land() { return config().land; }
    public static boolean fish() { return config().fish; }
    public static boolean squid() { return config().squid; }
    public static boolean axolotl() { return config().axolotl; }
    public static boolean bat() { return config().bat; }
    public static boolean dolphin() { return config().dolphin; }

    private static Config config() {
        String raw = DDSRules.naturalPassiveSpawning();
        if (raw == null) raw = "all";
        if (!raw.equals(cachedRaw)) synchronized (NaturalPassiveSpawning.class) {
            if (!raw.equals(cachedRaw)) {
                cachedConfig = parse(raw);
                cachedRaw = raw;
            }
        }
        return cachedConfig;
    }

    private static Config parse(String raw) {
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if ("all".equals(normalized)) return Config.ALL;
        if ("none".equals(normalized)) return Config.NONE;
        boolean land = false, fish = false, squid = false, axolotl = false, bat = false, dolphin = false;
        for (String rawToken : normalized.split(",")) {
            String token = rawToken.trim();
            if (token.isEmpty()) return Config.ALL;
            switch (token) {
                case "land": land = true; break;
                case "fish": fish = true; break;
                case "squid": squid = true; break;
                case "axolotl": axolotl = true; break;
                case "bat": bat = true; break;
                case "dolphin": dolphin = true; break;
                default: return Config.ALL; // Invalid input fails safe to vanilla behavior.
            }
        }
        return new Config(land, fish, squid, axolotl, bat, dolphin);
    }

    private static final class Config {
        private static final Config ALL = new Config(true, true, true, true, true, true);
        private static final Config NONE = new Config(false, false, false, false, false, false);
        private final boolean land, fish, squid, axolotl, bat, dolphin;
        private Config(boolean land, boolean fish, boolean squid, boolean axolotl, boolean bat, boolean dolphin) {
            this.land = land;
            this.fish = fish;
            this.squid = squid;
            this.axolotl = axolotl;
            this.bat = bat;
            this.dolphin = dolphin;
        }
    }
}
