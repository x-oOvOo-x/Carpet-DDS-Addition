/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.explosionnoblockdamage;

import carpetddsaddition.generated.DDSRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import java.util.Locale;

public final class ExplosionNoBlockDamage {
    public enum Type {
        CREEPER(1 << 0), TNT(1 << 1), TNT_MINECART(1 << 2), END_CRYSTAL(1 << 3), GHAST(1 << 4), WITHER(1 << 5), WITHER_SKULL(1 << 6);
        private final int bit;
        Type(int bit) { this.bit = bit; }
    }

    private static final int ALL_MASK = Type.CREEPER.bit | Type.TNT.bit | Type.TNT_MINECART.bit | Type.END_CRYSTAL.bit
            | Type.GHAST.bit | Type.WITHER.bit | Type.WITHER_SKULL.bit;
    private static volatile String cachedRaw;
    private static volatile int cachedMask;
    private ExplosionNoBlockDamage() {}

    public static boolean prevents(Type type) { return type != null && (mask() & type.bit) != 0; }

    public static boolean shouldProtectBlocks(Entity entity) {
        Type type = typeOf(entity);
        return type != null && prevents(type);
    }

    private static Type typeOf(Entity entity) {
        if (entity instanceof Creeper) return Type.CREEPER;
        if (entity instanceof PrimedTnt) return Type.TNT;
        if (entity instanceof MinecartTNT) return Type.TNT_MINECART;
        if (entity instanceof EndCrystal) return Type.END_CRYSTAL;
        if (entity instanceof LargeFireball) return Type.GHAST;
        if (entity instanceof WitherBoss) return Type.WITHER;
        if (entity instanceof WitherSkull) return Type.WITHER_SKULL;
        return null;
    }

    private static int mask() {
        String raw = DDSRules.explosionNoBlockDamage();
        if (raw == null) raw = "none";
        String currentRaw = cachedRaw;
        // Reference comparison keeps the unchanged path cheap; equals() covers equivalent String instances.
        if (raw != currentRaw && !raw.equals(currentRaw)) synchronized (ExplosionNoBlockDamage.class) {
            currentRaw = cachedRaw;
            if (raw != currentRaw && !raw.equals(currentRaw)) {
                cachedMask = parse(raw);
                cachedRaw = raw;
            }
        }
        return cachedMask;
    }

    private static int parse(String raw) {
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if ("none".equals(normalized) || normalized.isEmpty()) return 0;
        if ("all".equals(normalized)) return ALL_MASK;
        int mask = 0;
        // Preserve trailing empties so malformed values such as "tnt," fail safe.
        for (String rawToken : normalized.split(",", -1)) {
            String token = rawToken.trim();
            if (token.isEmpty()) return 0;
            switch (token) {
                case "creeper": mask |= Type.CREEPER.bit; break;
                case "tnt": mask |= Type.TNT.bit; break;
                case "tnt_minecart": mask |= Type.TNT_MINECART.bit; break;
                case "end_crystal": mask |= Type.END_CRYSTAL.bit; break;
                case "ghast": mask |= Type.GHAST.bit; break;
                case "wither": mask |= Type.WITHER.bit; break;
                case "wither_skull": mask |= Type.WITHER_SKULL.bit; break;
                default: return 0; // Invalid configuration fails safe to vanilla behavior.
            }
        }
        return mask;
    }
}
