/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import carpet.CarpetServer;
import carpetddsaddition.core.rules.DdsRule;
import carpetddsaddition.core.rules.DdsRuleEvents;
import carpetddsaddition.generated.CarpetDDSAdditionSettings;
import carpetddsaddition.generated.DDSRules;

@SuppressWarnings({"deprecation", "removal"})
public final class CarpetRuleCompat {
    private static DdsSettingsManager settingsManager;
    private static Object registeredManager;

    private CarpetRuleCompat() {}

    public static synchronized void initialize(String version) {
        if (settingsManager == null) settingsManager = new DdsSettingsManager(version);
    }

    public static DdsSettingsManager settingsManager() {
        if (settingsManager == null)
            throw new IllegalStateException("DDS SettingsManager has not been initialized");
        return settingsManager;
    }

    public static synchronized void registerRules() {
        DdsSettingsManager manager = settingsManager;
        if (manager == null || manager == registeredManager) return;

        //#if MC >= 11902
        //$$ for (DdsRule<?> rule : DDSRules.registry().available()) registerModern(manager, rule);
        //#else
        manager.parseSettingsClass(CarpetDDSAdditionSettings.class);
        //#endif

        registerRuleObserver(manager);
        DDSRules.captureCurrentState();
        DdsRuleEvents.captureCurrentState();
        registeredManager = manager;
    }

    //#if MC >= 11902
    //$$ private static <T> void registerModern(DdsSettingsManager manager, DdsRule<T> rule) {
    //$$     manager.addCarpetRule(new DdsCarpetRule<T>(manager, rule));
    //$$ }
    //#endif

    private static void registerRuleObserver(DdsSettingsManager manager) {
        //#if MC >= 11902
        //$$ manager.registerRuleObserver((source, changedRule, userInput) ->
        //$$         onManagerRuleChanged(changedRule.name()));
        //#else
        manager.addRuleObserver((source, changedRule, userInput) ->
                onManagerRuleChanged(changedRule.name));
        //#endif
    }

    private static void onManagerRuleChanged(String name) {
        DdsRule<?> rule = DDSRules.registry().get(name);
        if (rule != null) {
            rule.captureBackendChange(
                    settingsManager.currentOrigin(),
                    CarpetServer.minecraft_server
            );
        }
        DdsRuleEvents.onRuleChanged(CarpetServer.minecraft_server);
    }
}
