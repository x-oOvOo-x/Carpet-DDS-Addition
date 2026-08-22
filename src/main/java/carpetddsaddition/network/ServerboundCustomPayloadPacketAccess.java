/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

//#if MC >= 11404 && MC <= 11605
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
//#endif

/** Legacy custom-payload packet bridge shared by packet/network mixins. */
public interface ServerboundCustomPayloadPacketAccess {
    //#if MC >= 11404 && MC <= 11605
    ResourceLocation dds$getIdentifier();
    FriendlyByteBuf dds$getData();
    //#endif
}
