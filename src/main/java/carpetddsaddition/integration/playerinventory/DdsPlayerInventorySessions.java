/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.playerinventory;

//#if MC >= 11904 && MC <= 260300
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.core.inventory.DdsCarriedScratchSwap;
//$$ import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//$$ import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
//$$ import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
//$$ import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
//$$ import net.fabricmc.loader.api.FabricLoader;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import java.lang.reflect.Method;
//$$ import java.util.ArrayDeque;
//$$ import java.util.Deque;
//$$ import java.util.Map;
//$$ import java.util.UUID;
//$$ import java.util.WeakHashMap;
//$$
//$$ /**
//$$  * Resolves and binds provider-neutral sessions for menus already authorized by a Provider.
//$$  * Pre-open metadata and active current-menu authority are intentionally separate states.
//$$  */
//$$ public final class DdsPlayerInventorySessions {
//$$     private static final String GCA_INVENTORY_LAYOUT = "gca_player_inventory_v1";
//$$     private static final String GCA_CONTROLLER_LAYOUT = "gca_player_controller_3_v1";
//$$     private static final String GCA_ENDER_LAYOUT = "gca_player_ender_3_v1";
//$$     private static final String ORG_INVENTORY_LAYOUT = "org_player_inventory_v1";
//$$     private static final String ORG_CONTROLS_LAYOUT = "org_player_inventory_controls_v1";
//$$     private static final String ORG_ENDER_LAYOUT = "org_player_ender_3_v1";
//$$     private static final String ENDER_3_LAYOUT = "player_ender_3_v1";
//$$     private static final String ENDER_6_LAYOUT = "player_ender_6_v1";
//$$     private static final String PROVIDER_MENU_BEHAVIOR = "provider_menu_v1";
//$$     private static final String PROVIDER_CONTROLS_BEHAVIOR = "provider_controls_v1";
//$$     private static final String QCA_BEHAVIOR = "dds_qca_v1";
//$$     private static final String EXTENSION_ENDER_BEHAVIOR = "player_inventory_extension_ender_v1";
//$$     private static final ThreadLocal<Deque<ServerPlayer>> OPENING_VIEWERS = ThreadLocal.withInitial(ArrayDeque::new);
//$$     private static final Map<ServerPlayer,DdsPlayerInventorySession> PENDING = new WeakHashMap<>();
//$$     private static final Map<ServerPlayer,DdsPlayerInventorySession> ACTIVE = new WeakHashMap<>();
//$$     private static final Map<ServerPlayer,Long> GENERATIONS = new WeakHashMap<>();
//$$
//$$     private DdsPlayerInventorySessions() {}
//$$
//$$     public static boolean anyProviderAvailable() {
//$$         return FabricLoader.getInstance().isModLoaded("gca")
//$$                 || FabricLoader.getInstance().isModLoaded("carpet-org-addition")
//$$                 || QuickContainerAccess.enabled();
//$$     }
//$$
//$$     public static void beginMenuOpen(ServerPlayer viewer) {
//$$         if (!PlayerInventoryExtension.enabled() || viewer == null) return;
//$$         OPENING_VIEWERS.get().push(viewer);
//$$     }
//$$
//$$     public static void endMenuOpen(ServerPlayer viewer) {
//$$         Deque<ServerPlayer> stack = OPENING_VIEWERS.get();
//$$         if (!stack.isEmpty() && stack.peek() == viewer) stack.pop();
//$$         else if (viewer != null) stack.removeFirstOccurrence(viewer);
//$$         if (stack.isEmpty()) OPENING_VIEWERS.remove();
//$$     }
//$$
//$$     public static ServerPlayer openingViewer() {
//$$         if (!PlayerInventoryExtension.enabled()) return null;
//$$         Deque<ServerPlayer> stack = OPENING_VIEWERS.get();
//$$         return stack.isEmpty() ? null : stack.peek();
//$$     }
//$$
//$$     public static DdsPlayerInventorySession prepare(ServerPlayer viewer, AbstractContainerMenu menu) {
//$$         if (!PlayerInventoryExtension.enabled() || viewer == null || menu == null || viewer.connection == null
//$$                 || !containsViewerInventory(menu, viewer)) return null;
//$$         DdsPlayerInventorySession existing = PENDING.get(viewer);
//$$         if (sameBinding(existing, viewer, menu)) return existing;
//$$         DdsPlayerInventorySession metadata = resolve(viewer, menu);
//$$         if (metadata == null) return null;
//$$         DdsPlayerInventorySession session = metadata.bind(viewer, menu, nextGeneration(viewer));
//$$         PENDING.put(viewer, session);
//$$         return session;
//$$     }
//$$
//$$     public static DdsPlayerInventorySession resolve(ServerPlayer viewer, AbstractContainerMenu menu) {
//$$         if (!PlayerInventoryExtension.enabled() || viewer == null || menu == null) return null;
//$$         if (carpetddsaddition.feature.quickcontaineraccess.compat.QuickContainerAccessCompat.isOrgOfflineMenu(menu)) return null;
//$$         DdsPlayerInventorySession session = resolveCanonical(menu);
//$$         if (session == null) session = resolveGca(menu);
//$$         if (session == null) session = resolveOrg(menu);
//$$         if (session == null) session = resolveQca(menu);
//$$         return session;
//$$     }
//$$
//$$     public static DdsPlayerInventorySession current(ServerPlayer viewer, int containerId, UUID expectedTarget) {
//$$         if (!PlayerInventoryExtension.enabled() || viewer == null || viewer.connection == null) return null;
//$$         AbstractContainerMenu menu = viewer.containerMenu;
//$$         if (menu == null || menu.containerId != containerId) return null;
//$$
//$$         DdsPlayerInventorySession session = ACTIVE.get(viewer);
//$$         if (!sameBinding(session, viewer, menu)) {
//$$             DdsPlayerInventorySession pending = PENDING.get(viewer);
//$$             if (sameBinding(pending, viewer, menu)) {
//$$                 session = pending;
//$$                 PENDING.remove(viewer);
//$$             } else {
//$$                 DdsPlayerInventorySession metadata = resolve(viewer, menu);
//$$                 if (metadata == null || !containsViewerInventory(menu, viewer)) {
//$$                     ACTIVE.remove(viewer);
//$$                     return null;
//$$                 }
//$$                 session = metadata.bind(viewer, menu, nextGeneration(viewer));
//$$             }
//$$             ACTIVE.put(viewer, session);
//$$         }
//$$         if (expectedTarget != null && !expectedTarget.equals(session.targetUuid())) return null;
//$$         if (!validCurrent(viewer, menu, session)) {
//$$             ACTIVE.remove(viewer);
//$$             return null;
//$$         }
//$$         return session;
//$$     }
//$$
//$$     public static DdsPlayerInventorySession current(ServerPlayer viewer, int containerId, UUID expectedTarget, long expectedGeneration) {
//$$         if (expectedGeneration <= 0L) return null;
//$$         DdsPlayerInventorySession session = current(viewer, containerId, expectedTarget);
//$$         return session != null && DdsPlayerInventoryStructureRequestGuard.generationMatches(expectedGeneration, session.generation())
//$$                 ? session : null;
//$$     }
//$$
//$$     public static void clearViewer(ServerPlayer viewer) {
//$$         if (viewer == null) return;
//$$         PENDING.remove(viewer);
//$$         ACTIVE.remove(viewer);
//$$         GENERATIONS.remove(viewer);
//$$         DdsPlayerInventoryRootReturns.clear(viewer);
//$$     }
//$$
//$$     public static void clearActiveSessions() {
//$$         PENDING.clear();
//$$         ACTIVE.clear();
//$$         OPENING_VIEWERS.remove();
//$$         DdsPlayerInventoryRootReturns.clear();
//$$     }
//$$
//$$     public static void clearRuntimeState() {
//$$         clearActiveSessions();
//$$         GENERATIONS.clear();
//$$     }
//$$
//$$     private static boolean sameBinding(DdsPlayerInventorySession session, ServerPlayer viewer, AbstractContainerMenu menu) {
//$$         return session != null && viewer != null && menu != null
//$$                 && DdsPlayerInventoryStructureRequestGuard.bindingMatches(
//$$                         session.viewer(), viewer, session.viewerConnection(), viewer.connection,
//$$                         session.menu(), menu, session.containerId(), menu.containerId);
//$$     }
//$$
//$$     private static long nextGeneration(ServerPlayer viewer) {
//$$         long previous = GENERATIONS.getOrDefault(viewer, 0L);
//$$         long next = previous == Long.MAX_VALUE ? Long.MAX_VALUE : previous + 1L;
//$$         if (next <= 0L || next == previous) throw new IllegalStateException("player inventory session generation exhausted");
//$$         GENERATIONS.put(viewer, next);
//$$         return next;
//$$     }
//$$
//$$     private static boolean containsViewerInventory(AbstractContainerMenu menu, ServerPlayer viewer) {
//$$         for (Slot slot : menu.slots) {
//$$             if (!(slot.container instanceof Inventory)) continue;
//$$             Player player = ((Inventory) slot.container).player;
//$$             if (player == viewer) return true;
//$$         }
//$$         return false;
//$$     }
//$$
//$$     private static boolean validCurrent(ServerPlayer viewer, AbstractContainerMenu menu, DdsPlayerInventorySession session) {
//$$         if (!sameBinding(session, viewer, menu) || session.generation() <= 0L) return false;
//$$         Container canonical = session.canonicalContainer();
//$$         if (canonical != null && !DdsPlayerInventoryStructureRequestGuard.canonicalSizeMatches(
//$$                 canonical.getContainerSize(), session.canonicalSize())) return false;
//$$         try { if (!menu.stillValid(viewer)) return false; }
//$$         catch (RuntimeException ignored) { return false; }
//$$         MinecraftServer server =
//#if MC >= 12106
//$$                 viewer.level().getServer();
//#elseif MC >= 12001
//$$                 viewer.serverLevel().getServer();
//#else
//$$                 viewer.level.getServer();
//#endif
//$$         if (server == null) return false;
//$$         ServerPlayer online = server.getPlayerList().getPlayer(session.targetUuid());
//$$         return DdsPlayerInventoryStructureRequestGuard.targetPresenceMatches(
//$$                 session.targetType() == DdsPlayerInventorySession.TargetType.OFFLINE_PLAYER,
//$$                 online, session.liveTarget());
//$$     }
//$$
//$$     private static DdsPlayerInventorySession resolveCanonical(AbstractContainerMenu menu) {
//#if MC >= 12111
//$$         if (!(menu instanceof DdsPlayerInventoryCanonicalEnderMenu)) return null;
//$$         DdsPlayerInventoryCanonicalEnderMenu canonicalMenu = (DdsPlayerInventoryCanonicalEnderMenu) menu;
//$$         ServerPlayer target = canonicalMenu.target();
//$$         Container canonical = canonicalMenu.canonicalContainer();
//$$         int size = canonicalMenu.canonicalSize();
//$$         if (target == null || canonical != target.getEnderChestInventory() || size != 27 && size != 54) return null;
//$$         DdsPlayerInventorySession session = DdsPlayerInventorySession.live(canonicalMenu.providerId(), menu.containerId, target,
//$$                 target instanceof EntityPlayerMPFake,
//$$                 DdsPlayerInventorySession.InventoryKind.ENDER_CHEST, menu instanceof DdsCarriedScratchSwap,
//$$                 size == 54 ? ENDER_6_LAYOUT : ENDER_3_LAYOUT, 1, EXTENSION_ENDER_BEHAVIOR, canonical);
//$$         return session == null ? null : session.withRootReturn(canonicalMenu.rootReturn());
//#else
//$$         return null;
//#endif
//$$     }
//$$
//$$     private static DdsPlayerInventorySession resolveGca(AbstractContainerMenu menu) {
//$$         for (Slot slot : menu.slots) {
//$$             if (!(slot.container instanceof DdsInventoryOwnerView)) continue;
//$$             Container container = slot.container;
//$$             String name = container.getClass().getName();
//$$             boolean inventory = name.endsWith(".PlayerInventoryContainer");
//$$             boolean ender = name.endsWith(".PlayerEnderChestContainer");
//$$             if (!inventory && !ender) continue;
//$$             DdsInventoryOwnerView view = (DdsInventoryOwnerView) container;
//$$             ServerPlayer target = view.ddsInventoryOwner();
//$$             if (target == null) continue;
//$$             DdsPlayerInventorySession.InventoryKind kind;
//$$             String layout;
//$$             Container canonical = view.ddsCanonicalContainer();
//$$             if (inventory) {
//$$                 if (canonical != null) continue;
//$$                 kind = DdsPlayerInventorySession.InventoryKind.INVENTORY;
//$$                 layout = GCA_INVENTORY_LAYOUT;
//$$             } else {
//$$                 if (canonical == null || canonical != target.getEnderChestInventory()) continue;
//$$                 kind = providerSlotCount(menu, container) <= 27
//$$                         ? DdsPlayerInventorySession.InventoryKind.CONTROLLER
//$$                         : DdsPlayerInventorySession.InventoryKind.ENDER_CHEST;
//$$                 layout = kind == DdsPlayerInventorySession.InventoryKind.CONTROLLER
//$$                         ? GCA_CONTROLLER_LAYOUT : GCA_ENDER_LAYOUT;
//$$             }
//$$             return DdsPlayerInventorySession.live("gca", menu.containerId, target,
//$$                     target instanceof EntityPlayerMPFake, kind, menu instanceof DdsCarriedScratchSwap,
//$$                     layout, 1, PROVIDER_CONTROLS_BEHAVIOR, canonical);
//$$         }
//$$         return null;
//$$     }
//$$
//$$     private static DdsPlayerInventorySession resolveOrg(AbstractContainerMenu menu) {
//$$         String menuName = menu.getClass().getName();
//$$         if (!menuName.contains(".carpetorgaddition.")) return null;
//$$         if (menuName.endsWith(".PlayerEnderChestScreenHandler")) {
//$$             ServerPlayer target = DdsRemotePlayerOwnerResolver.owner(menu);
//$$             if (target == null) return null;
//$$             return DdsPlayerInventorySession.live("org", menu.containerId, target,
//$$                     target instanceof EntityPlayerMPFake, DdsPlayerInventorySession.InventoryKind.ENDER_CHEST,
//$$                     false, ORG_ENDER_LAYOUT, 1, PROVIDER_MENU_BEHAVIOR, target.getEnderChestInventory());
//$$         }
//$$         boolean controls = menuName.endsWith(".WithButtonPlayerInventoryScreenHandler");
//$$         if (!controls && !menuName.endsWith(".PlayerInventoryScreenHandler")) return null;
//$$         ServerPlayer target = DdsRemotePlayerOwnerResolver.owner(menu);
//$$         if (target == null) return null;
//$$         return DdsPlayerInventorySession.live("org", menu.containerId, target,
//$$                 target instanceof EntityPlayerMPFake, DdsPlayerInventorySession.InventoryKind.INVENTORY,
//$$                 false, controls ? ORG_CONTROLS_LAYOUT : ORG_INVENTORY_LAYOUT, 1,
//$$                 controls ? PROVIDER_CONTROLS_BEHAVIOR : PROVIDER_MENU_BEHAVIOR, null);
//$$     }
//$$
//$$     private static DdsPlayerInventorySession resolveQca(AbstractContainerMenu menu) {
//$$         if (!QuickContainerAccess.enabled() && !PlayerInventoryExtension.enabled()) return null;
//$$         for (Slot slot : menu.slots) {
//$$             if (!(slot.container instanceof DdsInventoryOwnerView)) continue;
//$$             Container container = slot.container;
//$$             String name = container.getClass().getName();
//$$             if (!name.contains("QuickContainerAccessMenuFactory$PortableEnderContainer")) continue;
//$$             DdsInventoryOwnerView view = (DdsInventoryOwnerView) container;
//$$             ServerPlayer target = view.ddsInventoryOwner();
//$$             Container canonical = view.ddsCanonicalContainer();
//$$             if (target == null || canonical == null || canonical != target.getEnderChestInventory()) continue;
//$$             int visibleSize = container.getContainerSize();
//$$             if (visibleSize != 27 && visibleSize != 54) return null;
//$$             return DdsPlayerInventorySession.live("dds_qca", menu.containerId, target,
//$$                     target instanceof EntityPlayerMPFake, DdsPlayerInventorySession.InventoryKind.ENDER_CHEST,
//$$                     menu instanceof DdsCarriedScratchSwap, visibleSize == 54 ? ENDER_6_LAYOUT : ENDER_3_LAYOUT,
//$$                     1, QCA_BEHAVIOR, canonical);
//$$         }
//$$         return null;
//$$     }
//$$
//$$     /** Count only slots backed by the exact provider container. */
//$$     private static int providerSlotCount(AbstractContainerMenu menu, Container container) {
//$$         int count = 0;
//$$         for (Slot slot : menu.slots) if (slot.container == container) count++;
//$$         return count;
//$$     }
//$$
//$$     private static Object invoke(Object target, String name) {
//$$         if (target == null) return null;
//$$         try {
//$$             Method method = target.getClass().getMethod(name);
//$$             method.setAccessible(true);
//$$             return method.invoke(target);
//$$         } catch (ReflectiveOperationException | LinkageError | SecurityException ignored) { return null; }
//$$     }
//$$
//$$ }
//#else
public final class DdsPlayerInventorySessions {
    private DdsPlayerInventorySessions() {}
    public static boolean anyProviderAvailable() { return false; }
    public static void clearActiveSessions() {}
    public static void clearRuntimeState() {}
}
//#endif
