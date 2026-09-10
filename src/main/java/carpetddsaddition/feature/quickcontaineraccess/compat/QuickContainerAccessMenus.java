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
import carpetddsaddition.feature.quickcontaineraccess.network.QuickContainerAccessServerNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
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
        if (!sourceRef.valid() || !sourceRef.canLease()) return true;
        closeForSwitch(player, currentMenu);
        if (!sourceRef.valid()) { QuickContainerAccessSession.syncInventoryMenu(player); return true; }
        ItemStack source = sourceRef.get();
        QuickContainerAccessItems.MenuKind actualKind = QuickContainerAccessItems.kindOf(source);
        if (source.isEmpty() || actualKind != expectedKind || !QuickContainerAccessItems.canOpenInHand(actualKind, source)
                || !QuickContainerAccessItems.sameItemAndData(source, expectedSource)) {
            QuickContainerAccessSession.syncInventoryMenu(player); return true;
        }
        return open(player, sourceRef, actualKind);
    }

    static boolean navigateBack(ServerPlayer player, AbstractContainerMenu currentMenu,
                                QuickContainerAccessSession.PathNode destination) {
        QuickContainerAccessSession.Session current = QuickContainerAccessSession.get(currentMenu);
        if (destination == null || destination.sourceRef == null || !destination.sourceRef.valid()) return false;
        if (destination.kind != QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && destination.kind != QuickContainerAccessItems.MenuKind.SHULKER) return false;
        if (current != null && destination == current.path) return true;
        closeForSwitch(player, currentMenu);
        if (!destination.sourceRef.valid()) { QuickContainerAccessSession.syncInventoryMenu(player); return false; }
        return openInternal(player, destination.sourceRef, destination.kind,
                destination.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST);
    }

    static boolean open(ServerPlayer player, QuickContainerAccessSession.SourceRef sourceRef,
                        QuickContainerAccessItems.MenuKind kind) { return openInternal(player, sourceRef, kind, false); }

    private static boolean openInternal(ServerPlayer player, QuickContainerAccessSession.SourceRef sourceRef,
                                        QuickContainerAccessItems.MenuKind kind, boolean allowSyntheticEnderSource) {
        if (!QuickContainerAccess.enabled() || player instanceof EntityPlayerMPFake || !sourceRef.valid() || !sourceRef.canLease()) return false;
        ItemStack source = sourceRef.get();
        if (!sourceRef.hasItemSource()) {
            if (!allowSyntheticEnderSource || kind != QuickContainerAccessItems.MenuKind.ENDER_CHEST) return false;
            source = ItemStack.EMPTY;
        } else if (QuickContainerAccessItems.kindOf(source) != kind || !QuickContainerAccessItems.canOpenInHand(kind, source)) return false;
        ServerPlayer owner = sourceRef.inventoryOwner;
        int openedSize = resolveOpenedSize(owner, kind);
        if (requiresStorageSize(kind) && openedSize == 0) return false;
        final ItemStack menuSource = source;
        final int sessionSize = openedSize;
        SimpleMenuProvider provider = new SimpleMenuProvider((containerId, inventory, menuPlayer) -> {
            ServerPlayer viewer = (ServerPlayer) menuPlayer;
            AbstractContainerMenu created = QuickContainerAccessMenuFactory.create(
                    containerId, inventory, viewer, owner, menuSource, kind, sessionSize, sourceRef.enderMenuFactory);
            if (created != null) QuickContainerAccessSession.begin(viewer, created, sourceRef, kind, sessionSize,
                    QuickContainerAccessMenuFactory.openedContainer(created, owner, kind));
            return created;
        }, QuickContainerAccessMenuFactory.titleFor(menuSource, kind));
        boolean opened = player.openMenu(provider).isPresent();
        if (!opened) return false;
        QuickContainerAccessCloseDebounce.opened(player.containerMenu);
        QuickContainerAccessSounds.sendOpen(player, kind);
        QuickContainerAccessServerNetwork.sendNavigationState(player,
                QuickContainerAccessCompat.canNavigateBack(player.containerMenu, player), false, false);
        return true;
    }

    /** Close through vanilla first so workstation removed() returns inputs before another QCA menu opens. */
    private static void closeForSwitch(ServerPlayer player, AbstractContainerMenu currentMenu) {
        if (currentMenu == player.inventoryMenu) return;
        player.doCloseContainer();
        QuickContainerAccessSession.syncInventoryMenu(player);
    }

    private static int resolveOpenedSize(ServerPlayer owner, QuickContainerAccessItems.MenuKind kind) {
        if (owner == null) return 0;
        if (kind == QuickContainerAccessItems.MenuKind.SHULKER) {
            int size = QuickContainerAccessStorage.shulkerSize(); return supportedStorageSize(size) ? size : 0;
        }
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST) {
            int size = owner.getEnderChestInventory().getContainerSize(); return supportedStorageSize(size) ? size : 0;
        }
        return 0;
    }
    private static boolean supportedStorageSize(int size) { return size == 27 || size == 54; }
    private static boolean requiresStorageSize(QuickContainerAccessItems.MenuKind kind) {
        return kind == QuickContainerAccessItems.MenuKind.SHULKER || kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST;
    }
    static void sendCloseSound(QuickContainerAccessSession.Session session) { QuickContainerAccessSounds.sendClose(session.player, session.kind); }

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
        if (infiniteMaterials || player.getRandom().nextFloat() >= 0.12F) { QuickContainerAccessSounds.sendAnvilUse(player); return true; }
        Item nextItem = nextAnvilState(source);
        if (nextItem == null) { session.sourceRef.set(ItemStack.EMPTY); QuickContainerAccessSounds.sendAnvilDestroy(player); return true; }
        session.sourceRef.set(transmuteCopyCompat(source, nextItem, 1)); QuickContainerAccessSounds.sendAnvilUse(player); return true;
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
