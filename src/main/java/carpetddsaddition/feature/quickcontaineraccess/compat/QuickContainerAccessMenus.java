/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;

//#if MC >= 11404 && MC <= 260300
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
    private static final boolean TRACE=Boolean.getBoolean("dds.playerinventory.trace");
    private static final org.apache.logging.log4j.Logger TRACE_LOG=
            org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.qca");
    private QuickContainerAccessMenus() {}

    //#if MC >= 11404 && MC <= 260300
    static boolean switchOpen(ServerPlayer player, AbstractContainerMenu currentMenu,
                              QuickContainerAccessSession.SourceRef sourceRef, ItemStack expectedSource,
                              QuickContainerAccessItems.MenuKind expectedKind) {
        if (!sourceRef.valid() || !sourceRef.canLease()) return true;
        if (expectedKind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && (!sourceRef.enderAuthorityValid()
                    || sourceRef.inventoryOwner != player && sourceRef.authorizedEnderSize <= 0)) return false;
        if (QuickContainerAccessCompat.isOrgOfflineMenu(currentMenu)) return false;
        if (TRACE) TRACE_LOG.info("qca_switch_source source_id="+currentMenu.containerId
                +" kind="+expectedKind+" owner="+sourceRef.inventoryOwner.getUUID()
                +" authorized_size="+sourceRef.authorizedEnderSize);
        closeForSwitch(player, currentMenu);
        if (!sourceRef.valid()) {
            if (TRACE) TRACE_LOG.info("qca_switch_source_invalid_after_close id="+currentMenu.containerId);
            QuickContainerAccessSession.syncInventoryMenu(player); return true;
        }
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
        if (destination.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && (!destination.sourceRef.enderAuthorityValid()
                    || destination.sourceRef.inventoryOwner != player
                    && (destination.sourceRef.authorizedEnderSize <= 0
                        || destination.openedSize > destination.sourceRef.authorizedEnderSize))) return false;
        if (current != null && destination == current.path) return true;
        closeForSwitch(player, currentMenu);
        if (!destination.sourceRef.valid()) { QuickContainerAccessSession.syncInventoryMenu(player); return false; }
        return openInternal(player, destination.sourceRef, destination.kind,
                destination.kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST, destination.openedSize);
    }

    static boolean openRemoteEnder(ServerPlayer viewer, AbstractContainerMenu currentMenu, ServerPlayer owner) {
        if (viewer == null || owner == null || owner == viewer || !owner.isAlive() || currentMenu == null
                || QuickContainerAccessCompat.isOrgOfflineMenu(currentMenu) || !currentMenu.stillValid(viewer)) return false;
        int openedSize = QuickContainerAccessSession.providerAuthorizedEnderSize(viewer, currentMenu, owner);
        if (openedSize <= 0) return false;
        QuickContainerAccessSession.SourceRef guard =
                QuickContainerAccessSession.SourceRef.remoteEnderGuard(viewer, owner, currentMenu, openedSize);
        return openInternal(viewer, guard, QuickContainerAccessItems.MenuKind.ENDER_CHEST, true, openedSize);
    }

    static boolean open(ServerPlayer player, QuickContainerAccessSession.SourceRef sourceRef,
                        QuickContainerAccessItems.MenuKind kind) {
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST && sourceRef.authorizedEnderSize < 0) return false;
        int authorized = kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                ? sourceRef.authorizedEnderSize : 0;
        return openInternal(player, sourceRef, kind, false, authorized);
    }

    private static boolean openInternal(ServerPlayer player, QuickContainerAccessSession.SourceRef sourceRef,
                                        QuickContainerAccessItems.MenuKind kind, boolean allowSyntheticEnderSource,
                                        int authorizedEnderSize) {
        boolean remoteSource=sourceRef.remoteEnderSource();
        boolean remoteEnder=remoteSource && PlayerInventoryExtension.enabled();
        if ((remoteSource && !remoteEnder) || !(QuickContainerAccess.enabled() || remoteEnder)
                || player instanceof EntityPlayerMPFake || !sourceRef.valid() || !sourceRef.canLease()) return false;
        ItemStack source = sourceRef.get();
        if (!sourceRef.hasItemSource()) {
            if (!allowSyntheticEnderSource || kind != QuickContainerAccessItems.MenuKind.ENDER_CHEST) return false;
            source = ItemStack.EMPTY;
        } else if (QuickContainerAccessItems.kindOf(source) != kind || !QuickContainerAccessItems.canOpenInHand(kind, source)) return false;
        ServerPlayer owner = sourceRef.inventoryOwner;
        if (kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST
                && (!sourceRef.enderAuthorityValid()
                    || owner != player
                    && (authorizedEnderSize <= 0 || authorizedEnderSize > sourceRef.authorizedEnderSize))) return false;
        int openedSize = kind == QuickContainerAccessItems.MenuKind.ENDER_CHEST && authorizedEnderSize > 0
                ? authorizedEnderSize(owner, authorizedEnderSize) : resolveOpenedSize(owner, kind);
        if (TRACE) TRACE_LOG.info("qca_open_capacity kind="+kind
                +" owner="+owner.getUUID()+" authorized_size="+authorizedEnderSize
                +" selected_size="+openedSize+" source_item="+sourceRef.hasItemSource());
        if (requiresStorageSize(kind) && openedSize == 0) return false;
        final ItemStack menuSource = source;
        final int sessionSize = openedSize;
        SimpleMenuProvider provider = new SimpleMenuProvider((containerId, inventory, menuPlayer) -> {
            ServerPlayer viewer = (ServerPlayer) menuPlayer;
            AbstractContainerMenu created = QuickContainerAccessMenuFactory.create(
                    containerId, inventory, viewer, owner, menuSource, kind, sessionSize, sourceRef.enderMenuFactory);
            if (created != null) {
                QuickContainerAccessSession.begin(viewer, created, sourceRef, kind, sessionSize,
                        QuickContainerAccessMenuFactory.openedContainer(created, owner, kind));
                DdsFakePlayerServerNetwork.sendContextFromMenu(viewer, created);
            }
            return created;
        }, QuickContainerAccessMenuFactory.titleFor(menuSource, kind));
        boolean opened = player.openMenu(provider).isPresent();
        if (TRACE) {
            QuickContainerAccessSession.Session child=opened
                    ? QuickContainerAccessSession.get(player.containerMenu) : null;
            boolean sourceValid;
            try { sourceValid=sourceRef.valid(); }
            catch (RuntimeException ignored) { sourceValid=false; }
            TRACE_LOG.info("qca_open_result kind="+kind+" opened="+opened
                    +" child_registered="+(child!=null)
                    +" source_valid="+sourceValid
                    +" current_id="+player.containerMenu.containerId);
        }
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
    private static int authorizedEnderSize(ServerPlayer owner, int requested) {
        if (owner == null || !supportedStorageSize(requested)) return 0;
        int canonical = owner.getEnderChestInventory().getContainerSize();
        return supportedStorageSize(canonical) && canonical >= requested ? requested : 0;
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
