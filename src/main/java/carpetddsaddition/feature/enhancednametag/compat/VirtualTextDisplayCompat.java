/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.feature.enhancednametag.compat;

//#if MC >= 260200
//$$ import net.minecraft.world.entity.EntityTypes;
//#endif
import carpetddsaddition.feature.enhancednametag.BlockAnnotation;
import net.minecraft.server.level.ServerPlayer;
import java.util.Set;
import java.util.UUID;

//#if MC >= 11904
//$$ import com.mojang.math.Transformation;
//$$ import carpetddsaddition.mixin.feature.enhancednametag.DisplayAccessor;
//$$ import carpetddsaddition.mixin.feature.enhancednametag.TextDisplayAccessor;
//$$ import carpetddsaddition.feature.enhancednametag.EnhancedNameTag;
//$$ import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
//$$ import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
//$$ import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
//$$ import net.minecraft.network.syncher.SynchedEntityData;
//$$ import net.minecraft.world.entity.Display;
//$$ import net.minecraft.world.entity.EntityType;
//$$ import net.minecraft.world.phys.Vec3;
//$$ import org.joml.Quaternionf;
//$$ import org.joml.Vector3f;
//$$ import java.util.HashMap;
//$$ import java.util.List;
//$$ import java.util.Map;
//#endif

/** Packet-only client TextDisplay; each player owns one reusable virtual entity ID. */
public final class VirtualTextDisplayCompat {
    //#if MC >= 11904
    //$$ private static final int FIRST_VIRTUAL_ENTITY_ID = 2_000_000_000;
    //$$ private static int nextVirtualEntityId = FIRST_VIRTUAL_ENTITY_ID;
    //$$ private static final Map<UUID, Integer> PLAYER_ENTITY_IDS = new HashMap<>();
    //#endif
    private VirtualTextDisplayCompat() {}

    public static void show(ServerPlayer player, BlockAnnotation annotation) {
        //#if MC >= 11904
        //$$ int entityId = getOrCreateEntityId(player.getUUID());
        //$$ Vec3 position = annotation.getDisplayPosition();
        //$$ // Temporary object only supplies version-correct SynchedEntityData; it is never added to the Level.
        //$$ Display.TextDisplay display = new Display.TextDisplay(
        //#if MC >= 260200
        //$$         EntityTypes.TEXT_DISPLAY,
        //#else
        //$$         EntityType.TEXT_DISPLAY,
        //#endif
        //$$         EnhancedNameTagCompat.getLevel(player));
        //$$ display.setPos(position.x, position.y, position.z);
        //$$ ((DisplayAccessor) display).carpetDDSAddition$setBillboardConstraints(Display.BillboardConstraints.CENTER);
        //$$ float scale = EnhancedNameTag.TEXT_SCALE;
        //$$ ((DisplayAccessor) display).carpetDDSAddition$setTransformation(new Transformation(
        //$$         new Vector3f(0.0F, 0.0F, 0.0F), new Quaternionf(), new Vector3f(scale, scale, scale), new Quaternionf()));
        //$$ ((TextDisplayAccessor) display).carpetDDSAddition$setText(EnhancedNameText.parse(annotation.getRawText()));
        //$$ ((TextDisplayAccessor) display).carpetDDSAddition$setBackgroundColor(0x00000000);
        //$$ // SEE_THROUGH is safe because the server raycast only spawns the label for the directly hit block.
        //$$ byte flags = (byte) (Display.TextDisplay.FLAG_SHADOW | Display.TextDisplay.FLAG_SEE_THROUGH);
        //$$ ((TextDisplayAccessor) display).carpetDDSAddition$setFlags(flags);
        //$$ player.connection.send(new ClientboundAddEntityPacket(entityId, annotation.getId(), position.x, position.y, position.z,
        //$$         0.0F, 0.0F,
        //#if MC >= 260200
        //$$         EntityTypes.TEXT_DISPLAY,
        //#else
        //$$         EntityType.TEXT_DISPLAY,
        //#endif
        //$$         0, Vec3.ZERO, 0.0D));
        //$$ List<SynchedEntityData.DataValue<?>> values = display.getEntityData().getNonDefaultValues();
        //$$ if (values != null && !values.isEmpty()) player.connection.send(new ClientboundSetEntityDataPacket(entityId, values));
        //#endif
    }

    public static void hide(ServerPlayer player) {
        //#if MC >= 11904
        //$$ Integer entityId = PLAYER_ENTITY_IDS.get(player.getUUID());
        //$$ if (entityId != null) player.connection.send(new ClientboundRemoveEntitiesPacket(entityId.intValue()));
        //#endif
    }

    public static void retainPlayers(Set<UUID> onlinePlayers) {
        //#if MC >= 11904
        //$$ PLAYER_ENTITY_IDS.keySet().retainAll(onlinePlayers);
        //#endif
    }

    public static void resetRuntimeState() {
        //#if MC >= 11904
        //$$ PLAYER_ENTITY_IDS.clear();
        //$$ nextVirtualEntityId = FIRST_VIRTUAL_ENTITY_ID;
        //#endif
    }

    //#if MC >= 11904
    //$$ private static int getOrCreateEntityId(UUID playerId) {
    //$$     Integer existing = PLAYER_ENTITY_IDS.get(playerId);
    //$$     if (existing != null) return existing.intValue();
    //$$     int allocated = nextVirtualEntityId--;
    //$$     PLAYER_ENTITY_IDS.put(playerId, Integer.valueOf(allocated));
    //$$     return allocated;
    //$$ }
    //#endif
}
