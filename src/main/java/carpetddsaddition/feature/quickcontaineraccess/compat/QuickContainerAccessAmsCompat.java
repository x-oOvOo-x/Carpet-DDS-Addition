/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260300
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
//#endif

final class QuickContainerAccessAmsCompat {
    private static final String AMS_MOD_ID = "carpet-ams-addition";
    // AMS v26.3 ships two package layouts: v26.3-legacy (older MC) and v26.3 (newer MC).
    private static final String[][] AMS_APIS = {
            {"carpetamsaddition.CarpetAMSAdditionLazySettings",
                    "carpetamsaddition.CarpetAMSAdditionLazySettings$Rule",
                    "carpetamsaddition.CarpetAMSAdditionSettings"},
            {"club.mcams.carpet.AmsServerLazySettings",
                    "club.mcams.carpet.AmsServerLazySettings$Rule",
                    "club.mcams.carpet.AmsServerSettings"}
    };
    //#if MC >= 11404 && MC <= 260300
    private static final Logger LOG = LogManager.getLogger("dds.qca.ams");
    private static boolean resolved;
    private static boolean amsLoaded;
    private static boolean mismatchLogged;
    private static boolean readFailureLogged;
    private static Method lazyIsEnabled;
    private static Object lazyRule;
    private static Field legacyField;
    //#endif

    private QuickContainerAccessAmsCompat() {}

    /** Returns 0 if AMS is loaded but neither compatible capacity source is readable. */
    static int shulkerSize() {
        //#if MC >= 11404 && MC <= 260300
        if (!resolved) resolve();
        Boolean cached = null;
        Boolean setting = null;
        if (lazyIsEnabled != null) {
            try {
                cached = (Boolean) lazyIsEnabled.invoke(null, lazyRule);
            } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
                logReadFailure(e);
            }
        }
        if (legacyField != null) {
            try {
                setting = legacyField.getBoolean(null);
            } catch (ReflectiveOperationException | LinkageError e) {
                logReadFailure(e);
            }
        }
        if (cached != null && setting != null && !cached.equals(setting) && !mismatchLogged) {
            mismatchLogged = true;
            LOG.warn("AMS largeShulkerBox cached state ({}) differs from the setting ({}); "
                    + "restart required; QCA follows the cached state used by AMS menus",
                    cached, setting);
        }
        if (amsLoaded && cached == null && setting == null) return 0;
        return effectiveShulkerEnabled(cached, setting) ? 54 : 27;
        //#else
        //$$ return 27;
        //#endif
    }

    /** AMS' menu mixins use their cached state; the setting is only a compatibility fallback. */
    static boolean effectiveShulkerEnabled(Boolean cached, Boolean setting) {
        return cached != null ? cached : Boolean.TRUE.equals(setting);
    }

    static void reset() {
        //#if MC >= 11404 && MC <= 260300
        resolved = false;
        amsLoaded = false;
        mismatchLogged = false;
        readFailureLogged = false;
        lazyIsEnabled = null;
        lazyRule = null;
        legacyField = null;
        //#endif
    }

    //#if MC >= 11404 && MC <= 260300
    private static void resolve() {
        resolved = true;
        amsLoaded = FabricLoader.getInstance().isModLoaded(AMS_MOD_ID);
        if (!amsLoaded) return;
        for (String[] api : AMS_APIS) {
            if (resolveApi(api)) return;
        }
        LOG.warn("AMS loaded but neither modern nor legacy largeShulkerBox rule API was found; "
                + "QCA shulker interactions are disabled to protect stored items");
    }

    private static boolean resolveApi(String[] api) {
        try {
            Class<?> lazySettings = Class.forName(api[0]);
            Class<?> ruleClass = Class.forName(api[1]);
            lazyRule = ruleClass.getField("LARGE_SHULKER_BOX").get(null);
            lazyIsEnabled = lazySettings.getMethod("isEnabled", ruleClass);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            lazyRule = null;
            lazyIsEnabled = null;
        }
        try {
            legacyField = Class.forName(api[2]).getField("largeShulkerBox");
        } catch (ReflectiveOperationException | LinkageError ignored) {
            legacyField = null;
        }
        return lazyIsEnabled != null || legacyField != null;
    }

    private static void logReadFailure(Throwable error) {
        if (!readFailureLogged) {
            readFailureLogged = true;
            LOG.warn("QCA could not read AMS largeShulkerBox state", error);
        }
    }
    //#endif
}
