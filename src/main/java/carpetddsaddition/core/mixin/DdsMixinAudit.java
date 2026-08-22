/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.MixinEnvironment;

/** Development-only Mixin audit entry point. */
public final class DdsMixinAudit {
    private static final String AUDIT_PROPERTY = "carpetddsaddition.mixin_audit";
    private DdsMixinAudit() {}

    public static void runIfRequested() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || !Boolean.getBoolean(AUDIT_PROPERTY)) return;
        System.out.println("[Carpet DDS Addition] Running Mixin environment audit...");
        try {
            MixinEnvironment.getCurrentEnvironment().audit();
            System.out.println("[Carpet DDS Addition] Mixin environment audit passed.");
            System.exit(0);
        } catch (Throwable throwable) {
            System.err.println("[Carpet DDS Addition] Mixin environment audit failed.");
            throwable.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
