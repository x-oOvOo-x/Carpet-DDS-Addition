/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.fabric;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.CarpetDDSAddition;
//$$ import net.fabricmc.api.EnvType;
//$$ import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//$$ import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
//$$ import net.fabricmc.loader.api.FabricLoader;
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//#endif

public final class DdsBuiltinResourcePackCompat {
    private DdsBuiltinResourcePackCompat() {}

    @SuppressWarnings("deprecation")
    public static void register() {
//#if MC >= 11904 && MC <= 260200
//$$         FabricLoader loader = FabricLoader.getInstance();
//$$         if (loader.getEnvironmentType() != EnvType.CLIENT) return;
//$$         ResourceManagerHelper.registerBuiltinResourcePack(
//$$                 id("dds_fake_player_ui"),
//$$                 loader.getModContainer(CarpetDDSAddition.MOD_ID).orElseThrow(RuntimeException::new),
//$$                 ResourcePackActivationType.NORMAL
//$$         );
//#endif
    }

//#if MC >= 12111 && MC <= 260200
//$$     private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#elseif MC >= 12101 && MC <= 12110
//$$     private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#elseif MC >= 11904
//$$     private static ResourceLocation id(String path) { return new ResourceLocation(CarpetDDSAddition.MOD_ID, path); }
//#endif
}
