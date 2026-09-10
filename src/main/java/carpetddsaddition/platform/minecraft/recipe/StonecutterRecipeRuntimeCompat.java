/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.platform.minecraft.recipe;

import net.minecraft.server.MinecraftServer;
//#if MC >= 11700
//$$ import net.minecraft.world.item.crafting.RecipeManager;
//#endif
//#if MC >= 12002
//$$ import net.minecraft.world.item.crafting.RecipeHolder;
//#else
import net.minecraft.world.item.crafting.Recipe;
//#endif
//#if MC >= 12103
//$$ import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.inventory.StonecutterMenu;
//$$ import net.minecraft.world.item.crafting.RecipeMap;
//#endif
import java.util.List;

/** Shared Minecraft-version boundary for replacing stonecutter recipes. */
public final class StonecutterRecipeRuntimeCompat {
    private StonecutterRecipeRuntimeCompat(){}

    //#if MC >= 12103
    //$$ public static void replaceLoadedRecipes(RecipeManager manager,List<RecipeHolder<?>> recipes){((RecipeManagerBridge)(Object)manager).carpetDDSAddition$setRecipes(RecipeMap.create(recipes));}
    //#endif

    //#if MC >= 12002
    //$$ public static void replace(MinecraftServer server,RecipeManager manager,List<RecipeHolder<?>> recipes){
    //#if MC >= 12103
    //$$     replaceLoadedRecipes(manager,recipes); manager.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    //#else
    //$$     manager.replaceRecipes(recipes);
    //#endif
    //$$     syncPlayers(server);
    //$$ }
    //#elseif MC >= 11700
    //$$ public static void replace(MinecraftServer server,RecipeManager manager,List<Recipe<?>> recipes){manager.replaceRecipes(recipes); syncPlayers(server);}
    //#endif

    private static void syncPlayers(MinecraftServer server){
        if(server==null||server.getPlayerList()==null||server.getPlayerList().getPlayers().isEmpty())return;
        //#if MC >= 12103
        //$$ RecipeManager manager=server.getRecipeManager(); ClientboundUpdateRecipesPacket packet=new ClientboundUpdateRecipesPacket(manager.getSynchronizedItemProperties(),manager.getSynchronizedStonecutterRecipes());
        //$$ for(ServerPlayer player:server.getPlayerList().getPlayers()){player.connection.send(packet); if(player.containerMenu instanceof StonecutterMenu)player.closeContainer();}
        //#else
        server.getPlayerList().reloadResources();
        //#endif
    }
}
