/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
import net.fabricmc.loader.api.FabricLoader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
//#endif

final class QuickContainerAccessAmsCompat {
    private static final String AMS_MOD_ID = "carpet-ams-addition";
    private static final String AMS_SETTINGS = "carpetamsaddition.CarpetAMSAdditionSettings";
    private static final String AMS_LAZY_SETTINGS = "carpetamsaddition.CarpetAMSAdditionLazySettings";
    private static final String AMS_LAZY_RULE = "carpetamsaddition.CarpetAMSAdditionLazySettings$Rule";
    private static boolean resolved, largeShulkerBoxEnabled;
    private QuickContainerAccessAmsCompat() {}

    static boolean largeShulkerBoxEnabled() {
        //#if MC >= 11404 && MC <= 260200
        if (!resolved) resolve();
        return largeShulkerBoxEnabled;
        //#else
        //$$ return false;
        //#endif
    }
    static void reset() {
        resolved = false;
        largeShulkerBoxEnabled = false;
    }

    //#if MC >= 11404 && MC <= 260200
    private static void resolve() {
        resolved = true;
        largeShulkerBoxEnabled = false;
        if (!FabricLoader.getInstance().isModLoaded(AMS_MOD_ID)) return;
        Boolean lazyState = readLazyState();
        if (lazyState != null) {
            largeShulkerBoxEnabled = lazyState;
            return;
        }
        largeShulkerBoxEnabled = readLegacyState();
    }
    /** Prefer AMS' exact lazy runtime state; reflection keeps AMS optional. */
    private static Boolean readLazyState() {
        try {
            Class<?> lazySettings = Class.forName(AMS_LAZY_SETTINGS), ruleClass = Class.forName(AMS_LAZY_RULE);
            Object rule = ruleClass.getField("LARGE_SHULKER_BOX").get(null);
            Method isEnabled = lazySettings.getMethod("isEnabled", ruleClass);
            Object result = isEnabled.invoke(null, rule);
            return result instanceof Boolean ? (Boolean) result : null;
        } catch (ReflectiveOperationException | LinkageError ignored) { return null; }
    }
    /** Compatibility fallback for older AMS releases. */
    private static boolean readLegacyState() {
        try {
            Class<?> settings = Class.forName(AMS_SETTINGS);
            Field field = settings.getField("largeShulkerBox");
            return field.getBoolean(null);
        } catch (ReflectiveOperationException | LinkageError ignored) { return false; }
    }
    //#endif
}
