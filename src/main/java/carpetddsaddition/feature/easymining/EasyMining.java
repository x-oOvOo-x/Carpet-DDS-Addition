/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.easymining;

import carpetddsaddition.feature.easymining.compat.EasyMiningCompat;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public final class EasyMining {
    private static final float MULTIPLIER = 20.0F;
    private static final int OBSIDIAN = 1, CRYING_OBSIDIAN = 2, DEEPSLATE = 4, ORES = 8, ALL = 16;
    private static volatile Selection cached = new Selection("false", 0);

    private EasyMining() {}

    public static String mode() {
        return DDSRules.easyMining();
    }

    public static boolean enabled() {
        return flags(mode()) != 0;
    }

    public static boolean affects(BlockState state) {
        int flags = flags(mode());
        return (flags & ALL) != 0
                || (flags & OBSIDIAN) != 0 && EasyMiningCompat.isObsidian(state)
                || (flags & CRYING_OBSIDIAN) != 0 && EasyMiningCompat.isCryingObsidian(state)
                || (flags & DEEPSLATE) != 0 && EasyMiningCompat.isDeepslate(state)
                || (flags & ORES) != 0 && EasyMiningCompat.isOre(state);
    }

    // Publish the source and parsed flags together: client/server may read concurrently.
    static int flags(String value) {
        Selection current = cached;
        if (!Objects.equals(current.source, value)) {
            current = new Selection(value, parse(value));
            cached = current;
        }
        return current.flags;
    }

    static int parse(String value) {
        if (value == null || "false".equals(value.trim())) return 0;
        int flags = 0;
        for (String option : value.split(",", -1)) {
            switch (option.trim()) {
                case "obsidian": flags |= OBSIDIAN; break;
                case "crying_obsidian": flags |= CRYING_OBSIDIAN; break;
                case "deepslate": flags |= DEEPSLATE; break;
                case "ores": flags |= ORES; break;
                case "all": flags |= ALL; break;
                default: return 0;
            }
        }
        return flags;
    }

    public static float adjustProgress(BlockState state, float progress) {
        if (!(progress > 0.0F && progress < 1.0F)) return progress;
        return affects(state) ? progress * MULTIPLIER : progress;
    }

    private static final class Selection {
        final String source;
        final int flags;

        Selection(String source, int flags) {
            this.source = source;
            this.flags = flags;
        }
    }
}
