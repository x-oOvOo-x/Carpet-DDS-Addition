/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

//#if MC >= 11404 && MC <= 260200
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//#if MC >= 12005
//$$ import net.minecraft.nbt.NbtOps;
//$$ import net.minecraft.nbt.Tag;
//$$ import net.minecraft.resources.RegistryOps;
//#endif
//#endif

/** Full-fidelity ItemStack transport for Creative QCA cursor transactions. */
final class QuickContainerAccessStackCodec {
    private QuickContainerAccessStackCodec() {}

    //#if MC >= 11404 && MC <= 260200
    static void put(CompoundTag payload, String key, Player player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) { payload.putInt(key + "_empty", 1); return; }
        //#if MC >= 12005
        //$$ RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, player.level().registryAccess());
        //$$ Tag encoded = ItemStack.CODEC.encodeStart(ops, stack).result().orElse(null);
        //$$ if (encoded != null) payload.put(key, encoded);
        //#else
        payload.put(key, stack.save(new CompoundTag()));
        //#endif
    }

    static ItemStack get(CompoundTag payload, String key, Player player) {
        //#if MC >= 12105
        //$$ if (payload.getIntOr(key + "_empty", 0) != 0) return ItemStack.EMPTY;
        //#else
        if (payload.getInt(key + "_empty") != 0) return ItemStack.EMPTY;
        //#endif
        //#if MC >= 12005
        //$$ Tag encoded = payload.get(key);
        //$$ if (encoded == null) return ItemStack.EMPTY;
        //$$ RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, player.level().registryAccess());
        //$$ return ItemStack.CODEC.parse(ops, encoded).result().orElse(ItemStack.EMPTY);
        //#else
        return payload.contains(key) ? ItemStack.of(payload.getCompound(key)) : ItemStack.EMPTY;
        //#endif
    }
    //#endif
}
