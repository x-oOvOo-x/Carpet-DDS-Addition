/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class DdsMixinConfigPlugin implements IMixinConfigPlugin {
    private static final String DISABLED_TARGET_PREFIX = "carpetddsaddition.disabled.";
    private static final String[][] OPTIONAL_INTEGRATIONS = {
            {"carpetddsaddition.mixin.integration.gca.", "gca"},
            {"carpetddsaddition.mixin.integration.carpet.gca.", "gca"},
            {"carpetddsaddition.mixin.integration.inventoryprofilesnext.", "inventoryprofilesnext"},
            {"carpetddsaddition.mixin.integration.minihud.", "minihud"}
    };

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (targetClassName.startsWith(DISABLED_TARGET_PREFIX)) return false;
        for (String[] integration : OPTIONAL_INTEGRATIONS) {
            if (mixinClassName.startsWith(integration[0])) return FabricLoader.getInstance().isModLoaded(integration[1]);
        }
        return true;
    }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
