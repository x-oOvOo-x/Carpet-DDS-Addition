/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;

//#if MC >= 11404 && MC <= 260200
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//#endif

/** Owns portable QCA opening/switching and portable-anvil source degradation. */
final class QuickContainerAccessMenus {
    private QuickContainerAccessMenus() {}

    //#if MC >= 11404 && MC <= 260200
    static boolean switchOpen(ServerPlayer player, AbstractContainerMenu currentMenu,
                              QuickContainerAccessSession.SourceRef sourceRef, ItemStack expectedSource,
                              QuickContainerAccessItems.MenuKind expectedKind) {
        // inventoryMenu is permanent; real opened containers need normal close lifecycle first.
        if (currentMenu != player.inventoryMenu) player.doCloseContainer();
        if (!sourceRef.valid()) {
            QuickContainerAccessSession.syncInventoryMenu(player);
            return true;
        }
        ItemStack source = sourceRef.get();
        QuickContainerAccessItems.MenuKind actualKind = QuickContainerAccessItems.kindOf(source);
        // Closing may mutate backing state, so revalidate after doCloseContainer().
        if (source.isEmpty() || source.getCount() != 1 || actualKind == null || actualKind != expectedKind
                || !QuickContainerAccessItems.sameItemAndData(source, expectedSource)) {
            QuickContainerAccessSession.syncInventoryMenu(player);
            return true;
        }
        return open(player, sourceRef, actualKind);
    }

    static boolean open(ServerPlayer player, QuickContainerAccessSession.SourceRef sourceRef,
                        QuickContainerAccessItems.MenuKind kind) {
        if (!QuickContainerAccess.enabled() || player instanceof EntityPlayerMPFake || !sourceRef.valid()) return false;
        ItemStack source = sourceRef.get();
        int openedSize = resolveOpenedSize(player, kind);
        if (requiresStorageSize(kind) && openedSize == 0) return false;
        final int sessionSize = openedSize;
        SimpleMenuProvider provider = new SimpleMenuProvider((containerId, inventory, menuPlayer) -> {
            ServerPlayer serverPlayer = (ServerPlayer) menuPlayer;
            AbstractContainerMenu created = QuickContainerAccessMenuFactory.create(
                    containerId, inventory, serverPlayer, source, kind, sessionSize);
            if (created != null) QuickContainerAccessSession.begin(serverPlayer, created, sourceRef, kind, sessionSize);
            return created;
        }, QuickContainerAccessMenuFactory.titleFor(source, kind));
        boolean opened = player.openMenu(provider).isPresent();
        if (!opened) return false;
        QuickContainerAccessSounds.sendOpen(player, kind);
        return true;
    }

    private static int resolveOpenedSize(ServerPlayer player, QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) {
            int size = QuickContainerAccessStorage.shulkerSize();
            return supportedStorageSize(size) ? size : 0;
        }
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
            // Player inventory size is authoritative, including AMS largeEnderChest expansion.
            int size = player.getEnderChestInventory().getContainerSize();
            return supportedStorageSize(size) ? size : 0;
        }
        return 0;
    }
    private static boolean supportedStorageSize(int size) { return size == 27 || size == 54; }
    private static boolean requiresStorageSize(QuickContainerAccessItems.MenuKind kind) {
        return kind == QuickContainerAccessItems.MenuKind.SHULKER || kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST;
    }
    static void sendCloseSound(QuickContainerAccessSession.Session session) {
        QuickContainerAccessSounds.sendClose(session.player, session.kind);
    }

    static boolean handlePortableAnvilUse(AbstractContainerMenu menu) {
        QuickContainerAccessSession.Session session = QuickContainerAccessSession.get(menu);
        if (session == null || session.kind != QuickContainerAccessItems.MenuKind.ANVIL || !session.valid()) return false;
        ServerPlayer player = session.player;
        ItemStack source = session.sourceRef.get();
        //#if MC >= 12005
        //$$ boolean infiniteMaterials = player.hasInfiniteMaterials();
        //#else
        //#if MC >= 11701
        //$$ boolean infiniteMaterials = player.getAbilities().instabuild;
        //#else
        boolean infiniteMaterials = player.abilities.instabuild;
        //#endif
        //#endif
        // Match vanilla anvil degradation probability.
        if (infiniteMaterials || player.getRandom().nextFloat() >= 0.12F) {
            QuickContainerAccessSounds.sendAnvilUse(player);
            return true;
        }
        Item nextItem = nextAnvilState(source);
        if (nextItem == null) {
            session.sourceRef.set(ItemStack.EMPTY);
            QuickContainerAccessSounds.sendAnvilDestroy(player);
            return true;
        }
        session.sourceRef.set(transmuteCopyCompat(source, nextItem, 1));
        QuickContainerAccessSounds.sendAnvilUse(player);
        return true;
    }
    private static Item nextAnvilState(ItemStack source) {
        if (QuickContainerAccessItems.isItem(source, Items.ANVIL)) return Items.CHIPPED_ANVIL;
        if (QuickContainerAccessItems.isItem(source, Items.CHIPPED_ANVIL)) return Items.DAMAGED_ANVIL;
        return null;
    }
    private static ItemStack transmuteCopyCompat(ItemStack source, Item item, int count) {
        //#if MC >= 12005
        //$$ return source.transmuteCopy(item, count);
        //#else
        ItemStack result = new ItemStack(item, count);
        if (source.hasTag()) result.setTag(source.getTag().copy());
        return result;
        //#endif
    }
    //#endif
}
