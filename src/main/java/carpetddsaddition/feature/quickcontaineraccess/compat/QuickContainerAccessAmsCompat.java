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
    //#if MC >= 11404 && MC <= 260200
    private static boolean resolved;
    private static Method lazyIsEnabled;
    private static Object lazyRule;
    private static Field legacyField;
    //#endif
    private QuickContainerAccessAmsCompat() {}

    static boolean largeShulkerBoxEnabled() {
        //#if MC >= 11404 && MC <= 260200
        if (!resolved) resolve();
        try {
            if (lazyIsEnabled != null) return Boolean.TRUE.equals(lazyIsEnabled.invoke(null, lazyRule));
            return legacyField != null && legacyField.getBoolean(null);
        } catch (ReflectiveOperationException | LinkageError ignored) { return false; }
        //#else
        //$$ return false;
        //#endif
    }
    static void reset() {
        //#if MC >= 11404 && MC <= 260200
        resolved = false; lazyIsEnabled = null; lazyRule = null; legacyField = null;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260200
    private static void resolve() {
        resolved = true;
        if (!FabricLoader.getInstance().isModLoaded(AMS_MOD_ID)) return;
        try {
            Class<?> lazySettings = Class.forName(AMS_LAZY_SETTINGS), ruleClass = Class.forName(AMS_LAZY_RULE);
            lazyRule = ruleClass.getField("LARGE_SHULKER_BOX").get(null);
            lazyIsEnabled = lazySettings.getMethod("isEnabled", ruleClass);
            return;
        } catch (ReflectiveOperationException | LinkageError ignored) {}
        try { legacyField = Class.forName(AMS_SETTINGS).getField("largeShulkerBox"); }
        catch (ReflectiveOperationException | LinkageError ignored) { legacyField = null; }
    }
    //#endif
}
