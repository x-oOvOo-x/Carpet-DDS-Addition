/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import carpetddsaddition.core.rules.DdsRuleChange;
import carpetddsaddition.generated.DDSRules;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

//#if MC >= 11902
//$$ import carpet.api.settings.SettingsManager;
//#else
import carpet.settings.SettingsManager;
//#endif

//#if MC >= 11601
//$$ import net.minecraft.world.level.storage.LevelResource;
//#endif

/** CDA-owned Carpet settings domain. */
public final class DdsSettingsManager extends SettingsManager {
    public static final String IDENTIFIER = "carpet-dds-addition";
    public static final String FANCY_NAME = "Carpet DDS Addition";

    private final ThreadLocal<DdsRuleChange.Origin> origin =
            new ThreadLocal<DdsRuleChange.Origin>() {
                @Override protected DdsRuleChange.Origin initialValue() {
                    return DdsRuleChange.Origin.COMMAND;
                }
            };
    private MinecraftServer server;

    DdsSettingsManager(String version) {
        super(version, IDENTIFIER, FANCY_NAME);
    }

    DdsRuleChange.Origin currentOrigin() { return origin.get(); }
    MinecraftServer currentServer() { return server; }

    @Override
    public void attachServer(MinecraftServer server) {
        this.server = server;
        migrateLegacyConfig(server);
        DdsRuleChange.Origin previous = origin.get();
        origin.set(DdsRuleChange.Origin.CONFIG_LOAD);
        try {
            super.attachServer(server);
        } finally {
            origin.set(previous);
        }
        // Carpet applies source-null config values silently. Re-baseline the
        // typed handles so the next user mutation observes the loaded value.
        DDSRules.captureCurrentState();
        DdsRuleCommandCompat.register(server);
    }

    @Override
    public void detachServer() {
        try {
            super.detachServer();
        } finally {
            // The Carpet base manager clears its own server reference. Do not
            // retain the detached world through CDA's rule-change context.
            server = null;
            origin.remove();
            DDSRules.captureCurrentState();
        }
    }

    private void migrateLegacyConfig(MinecraftServer server) {
        Path legacy;
        Path canonical;
        //#if MC >= 11601
        //$$ Path root = server.getWorldPath(LevelResource.ROOT);
        //$$ legacy = root.resolve("carpet.conf");
        //$$ canonical = root.resolve(IDENTIFIER + ".conf");
        //#else
        Path root = server.getStorageSource().getBaseDir().resolve(server.getLevelIdName());
        legacy = root.resolve("carpet.conf");
        canonical = root.resolve(IDENTIFIER + ".conf");
        //#endif
        DdsRuleConfigMigration.migrate(legacy, canonical, DDSRules.registry());
    }
}