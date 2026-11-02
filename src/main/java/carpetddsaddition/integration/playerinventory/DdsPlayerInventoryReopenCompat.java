/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.playerinventory;

import carpetddsaddition.core.inventory.DdsReopenableMenu;

//#if MC == 12111 || MC >= 260102 && MC <= 260300
//$$ import carpet.CarpetServer;
//$$ import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//$$ import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
//$$ import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
//$$ import carpetddsaddition.feature.quickcontaineraccess.QuickContainerAccess;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsGcaFakePlayerBridge;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.SimpleMenuProvider;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import java.lang.reflect.Constructor;
//$$
//$$ /** Reopens only the exact Provider menu that authorized the nested storage path. */
//$$ public final class DdsPlayerInventoryReopenCompat {
//$$     private static final boolean TRACE = Boolean.getBoolean("dds.playerinventory.trace");
//$$     private static final org.apache.logging.log4j.Logger TRACE_LOG =
//$$             org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.qca");
//$$     private DdsPlayerInventoryReopenCompat() {}
//$$
//$$     public static DdsReopenableMenu forInventory(ServerPlayer viewer, AbstractContainerMenu menu,
//$$                                                   ServerPlayer owner, Container source) {
//$$         if (viewer == null || menu == null || owner == null || owner == viewer || source == null
//$$                 || !QuickContainerAccess.enabled() || viewer.containerMenu != menu || !present(owner)) {
//$$             if (TRACE) TRACE_LOG.info("qca_parent_reopen reject=precondition menu=" + (menu != null)
//$$                     + " owner=" + (owner != null) + " owner_is_viewer=" + (owner == viewer)
//$$                     + " current_menu=" + (viewer != null && viewer.containerMenu == menu)
//$$                     + " qca_enabled=" + QuickContainerAccess.enabled() + " owner_present=" + present(owner));
//$$             return null;
//$$         }
//$$         // QCA's Provider-root reopening is independent of Extension. When Extension is
//$$         // active, retain its strict session validation rather than weakening that path.
//$$         DdsPlayerInventorySession session = PlayerInventoryExtension.enabled()
//$$                 ? DdsPlayerInventorySessions.current(viewer, menu.containerId, owner.getUUID()) : null;
//$$         if (PlayerInventoryExtension.enabled() && (session == null || session.viewer() != viewer
//$$                 || session.viewerConnection() != viewer.connection || session.liveTarget() != owner
//$$                 || session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_FAKE
//$$                 && session.targetType() != DdsPlayerInventorySession.TargetType.LIVE_PLAYER)) {
//$$             if (TRACE) TRACE_LOG.info("qca_parent_reopen reject=session id=" + menu.containerId
//$$                     + " session_present=" + (session != null));
//$$             return null;
//$$         }
//$$
//$$         String menuName = menu.getClass().getName(), sourceName = source.getClass().getName();
//$$         boolean gcaOwned = source instanceof DdsInventoryOwnerView
//$$                 && ((DdsInventoryOwnerView) source).ddsInventoryOwner() == owner;
//$$         boolean gcaInventory = (session == null || "gca".equals(session.providerId())) && gcaOwned
//$$                 && sourceName.endsWith(".PlayerInventoryContainer");
//$$         boolean gcaEnder = (session == null || "gca".equals(session.providerId())) && gcaOwned
//$$                 && sourceName.endsWith(".PlayerEnderChestContainer");
//$$         boolean orgControls = menuName.endsWith(".WithButtonPlayerInventoryScreenHandler");
//$$         boolean org = (session == null || "org".equals(session.providerId()))
//$$                 && (menuName.endsWith(".PlayerInventoryScreenHandler") || orgControls)
//$$                 && DdsRemotePlayerOwnerResolver.owner(menu) == owner;
//$$         if (!gcaInventory && !gcaEnder && !org) {
//$$             if (TRACE) TRACE_LOG.info("qca_parent_reopen reject=provider id=" + menu.containerId
//$$                     + " menu_type=" + menuName + " source_type=" + sourceName
//$$                     + " org_owner_match=" + (DdsRemotePlayerOwnerResolver.owner(menu) == owner)
//$$                     + " session_provider=" + (session == null ? "none" : session.providerId()));
//$$             return null;
//$$         }
//$$
//$$         Object reopenArgument = gcaInventory ? source : owner;
//$$         Constructor<?> constructor = gcaEnder
//$$                 ? reopenEnderConstructor(menu.getClass(), menu, source)
//$$                 : org && orgControls
//$$                 ? orgControlsConstructor(menu.getClass())
//$$                 : reopenConstructor(menu.getClass(), reopenArgument);
//$$         if (constructor == null) {
//$$             if (TRACE) TRACE_LOG.info("qca_parent_reopen reject=constructor id=" + menu.containerId
//$$                     + " menu_type=" + menuName + " org_controls=" + orgControls);
//$$             return null;
//$$         }
//$$         if (TRACE) TRACE_LOG.info("qca_parent_reopen captured id=" + menu.containerId
//$$                 + " provider=" + (org ? "org" : "gca") + " org_controls=" + orgControls);
//$$         int enderRows = gcaEnder ? providerRows(menu, source) : 0;
//$$         if (gcaEnder && enderRows == 0) return null;
//$$         Object connection = viewer.connection;
//$$         Component title = gcaInventory ? Component.translatable("gca.player.inventory", owner.getDisplayName())
//$$                 : gcaEnder ? Component.translatable("gca.player.ender_chest", owner.getDisplayName()) : owner.getName();
//$$
//$$         return new DdsReopenableMenu() {
//$$             @Override public boolean ddsCanReopen(ServerPlayer candidate) {
//$$                 if (candidate != viewer || candidate.connection != connection || !QuickContainerAccess.enabled()
//$$                         || !candidate.isAlive() || !present(owner)) {
//$$                     if (TRACE) TRACE_LOG.info("qca_parent_can_reopen reject=guard id=" + menu.containerId
//$$                             + " same_viewer=" + (candidate == viewer)
//$$                             + " same_connection=" + (candidate != null && candidate.connection == connection)
//$$                             + " qca_enabled=" + QuickContainerAccess.enabled());
//$$                     return false;
//$$                 }
//$$                 try {
//$$                     if (!menu.stillValid(candidate)) {
//$$                         if (TRACE) TRACE_LOG.info("qca_parent_can_reopen reject=stale_parent id=" + menu.containerId);
//$$                         return false;
//$$                     }
//$$                     if (gcaInventory) return DdsGcaFakePlayerBridge.isGcaInventoryRuleEnabled();
//$$                     if (gcaEnder) return DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled();
//$$                     return true;
//$$                 } catch (RuntimeException ignored) { return false; }
//$$             }
//$$
//$$             @Override public boolean ddsReopen(ServerPlayer candidate) {
//$$                 if (!ddsCanReopen(candidate)) return false;
//$$                 return candidate.openMenu(new SimpleMenuProvider((id, inventory, player) -> {
//$$                     try {
//$$                         AbstractContainerMenu reopened = gcaEnder
//$$                                 ? (AbstractContainerMenu) constructor.newInstance(menu.getType(), id, inventory, source, enderRows)
//$$                                 : org && orgControls
//$$                                 ? (AbstractContainerMenu) constructor.newInstance(id, owner, candidate)
//$$                                 : (AbstractContainerMenu) constructor.newInstance(id, inventory, reopenArgument);
//$$                         DdsFakePlayerServerNetwork.sendContextFromMenu(candidate, reopened);
//$$                         return reopened;
//$$                     } catch (ReflectiveOperationException | ClassCastException | UnsupportedOperationException ignored) {
//$$                         if (TRACE) TRACE_LOG.info("qca_parent_reopen reject=reflective_open id=" + menu.containerId
//$$                                 + " exception=" + ignored.getClass().getSimpleName());
//$$                         return null;
//$$                     }
//$$                 }, title)).isPresent();
//$$             }
//$$         };
//$$     }
//$$
//$$     /** Org button menus take (id, target, viewer), not (id, viewerInventory, target). */
//$$     static Constructor<?> orgControlsConstructor(Class<?> menuType) {
//$$         for (Constructor<?> constructor : menuType.getConstructors()) {
//$$             Class<?>[] parameters = constructor.getParameterTypes();
//$$             if (parameters.length == 3 && parameters[0] == int.class
//$$                     && parameters[1] == ServerPlayer.class && parameters[2] == ServerPlayer.class)
//$$                 return constructor;
//$$         }
//$$         return null;
//$$     }
//$$
//$$     private static Constructor<?> reopenConstructor(Class<?> menuType, Object argument) {
//$$         for (Constructor<?> constructor : menuType.getConstructors()) {
//$$             Class<?>[] parameters = constructor.getParameterTypes();
//$$             if (parameters.length == 3 && parameters[0] == int.class
//$$                     && parameters[1].isAssignableFrom(Inventory.class)
//$$                     && parameters[2].isInstance(argument)) return constructor;
//$$         }
//$$         return null;
//$$     }
//$$
//$$     private static Constructor<?> reopenEnderConstructor(Class<?> menuType, AbstractContainerMenu menu, Container source) {
//$$         Object type;
//$$         try { type = menu.getType(); }
//$$         catch (UnsupportedOperationException ignored) { return null; }
//$$         for (Constructor<?> constructor : menuType.getConstructors()) {
//$$             Class<?>[] parameters = constructor.getParameterTypes();
//$$             if (parameters.length == 5 && parameters[0].isInstance(type) && parameters[1] == int.class
//$$                     && parameters[2].isAssignableFrom(Inventory.class)
//$$                     && parameters[3].isInstance(source) && parameters[4] == int.class) return constructor;
//$$         }
//$$         return null;
//$$     }
//$$
//$$     private static boolean present(ServerPlayer player) {
//$$         if (player == null || !player.isAlive()) return false;
//$$         MinecraftServer server = CarpetServer.minecraft_server;
//$$         return server != null && server.getPlayerList().getPlayer(player.getUUID()) == player;
//$$     }
//$$
//$$     private static int providerRows(AbstractContainerMenu menu, Container source) {
//$$         int slots = 0;
//$$         for (net.minecraft.world.inventory.Slot slot : menu.slots) if (slot.container == source) slots++;
//$$         if (slots % 9 != 0) return 0;
//$$         int rows = slots / 9;
//$$         return rows == 3 || rows == 6 ? rows : 0;
//$$     }
//$$ }
//#else
public final class DdsPlayerInventoryReopenCompat {
    private DdsPlayerInventoryReopenCompat() {}
    public static DdsReopenableMenu forInventory(Object viewer, Object menu, Object owner, Object source) { return null; }
}
//#endif
