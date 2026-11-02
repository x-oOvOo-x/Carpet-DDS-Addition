/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.integration.playerinventory;

//#if MC >= 12111 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsCanonicalSlotMapping;
//$$ import carpetddsaddition.core.inventory.DdsCarriedScratchSwap;
//$$ import carpetddsaddition.core.inventory.DdsDirectSlotAccess;
//$$ import carpetddsaddition.core.inventory.DdsEnderMenuFactory;
//$$ import carpetddsaddition.core.inventory.DdsInventoryOwnerView;
//$$ import carpetddsaddition.core.inventory.DdsReopenableMenu;
//$$ import carpetddsaddition.core.inventory.DdsRemotePlayerOwnerResolver;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsGcaFakePlayerBridge;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.SimpleMenuProvider;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.MenuType;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import net.minecraft.world.item.ItemStack;
//#if MC >= 260102
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
//$$ import net.minecraft.world.inventory.ClickType;
//#endif
//$$
//$$ /** Extension-owned canonical Ender menu that preserves the authorizing Provider slot policy. */
//$$ public final class DdsPlayerInventoryCanonicalEnderMenu extends AbstractContainerMenu implements DdsEnderMenuFactory,DdsCarriedScratchSwap,DdsReopenableMenu {
//$$     public static final int DDS_SORT_SCRATCH_BUTTON = 9;
//$$     private final ServerPlayer viewer;
//$$     private final Object viewerConnection;
//$$     private final ServerPlayer target;
//$$     private final Container canonical;
//$$     private final int rows;
//$$     private final String providerId;
//$$     private final AbstractContainerMenu authorizationMenu;
//$$     private final DdsReopenableMenu rootReturn;
//$$     private final ProviderSlotPolicy[] policies;
//$$
//$$     /** Capture once while the Provider menu is current; the factory retains its guard and slot policy. */
//$$     public static DdsEnderMenuFactory captureFactory(ServerPlayer viewer, AbstractContainerMenu menu, ServerPlayer target) {
//$$         DdsPlayerInventorySession session = viewer == null || menu == null || target == null ? null
//$$                 : DdsPlayerInventorySessions.current(viewer, menu.containerId, target.getUUID());
//$$         if (session == null || session.liveTarget() != target
//$$                 || session.inventoryKind() != DdsPlayerInventorySession.InventoryKind.ENDER_CHEST) return null;
//$$         ProviderSlotPolicy[] captured = captureProviderSlotPolicy(menu, viewer, target,
//$$                 session.providerId(), target.getEnderChestInventory());
//$$         if (captured == null) return null;
//$$         return (id, inventory, player) -> player == viewer ? create(id, inventory, viewer, target,
//$$                 session.providerId(), menu, captured, null) : null;
//$$     }
//$$
//$$     static ProviderSlotPolicy[] captureProviderSlotPolicy(AbstractContainerMenu menu, ServerPlayer viewer,
//$$                                                           ServerPlayer target, String providerId,
//$$                                                           Container canonical) {
//$$         if (menu == null || viewer == null || target == null || providerId == null || canonical == null
//$$                 || canonical != target.getEnderChestInventory()) return null;
//$$         int size = canonical.getContainerSize();
//$$         if (size != 27 && size != 54) return null;
//$$         ProviderSlotPolicy[] out = new ProviderSlotPolicy[size];
//$$         for (Slot slot : menu.slots) {
//$$             int canonicalSlot = canonicalSlot(slot, canonical);
//$$             if (canonicalSlot < 0) continue;
//$$             if (canonicalSlot >= size || out[canonicalSlot] != null) return null;
//$$             out[canonicalSlot] = new ProviderSlotPolicy(slot);
//$$         }
//$$         boolean complete = true;
//$$         for (ProviderSlotPolicy policy : out) if (policy == null) { complete = false; break; }
//$$         if (complete) return out;
//$$         if (!providerAuthorizesWholeEnder(menu, viewer, target, providerId, canonical)) return null;
//$$         for (int i = 0; i < out.length; i++)
//$$             if (out[i] == null) out[i] = new ProviderSlotPolicy(new Slot(canonical, i, 0, 0));
//$$         return out;
//$$     }
//$$
//$$     private static boolean providerAuthorizesWholeEnder(AbstractContainerMenu menu, ServerPlayer viewer,
//$$                                                         ServerPlayer target, String providerId,
//$$                                                         Container canonical) {
//$$         if ("org".equals(providerId))
//$$             return menu.getClass().getName().endsWith(".PlayerEnderChestScreenHandler")
//$$                     && DdsRemotePlayerOwnerResolver.owner(menu) == target;
//$$         if (!"gca".equals(providerId) || !DdsGcaFakePlayerBridge.isGcaEnderChestRuleEnabled()) return false;
//$$         for (Slot slot : menu.slots) {
//$$             Container container = slot.container;
//$$             if (!(container instanceof DdsInventoryOwnerView)) continue;
//$$             DdsInventoryOwnerView view = (DdsInventoryOwnerView) container;
//$$             if (view.ddsInventoryOwner() == target && view.ddsCanonicalContainer() == canonical
//$$                     && container.getClass().getName().endsWith(".PlayerEnderChestContainer")) return true;
//$$         }
//$$         return false;
//$$     }
//$$
//$$     private static int canonicalSlot(Slot slot, Container canonical) {
//$$         if (slot == null) return -1;
//$$         int providerSlot = slot.getContainerSlot();
//$$         if (slot.container == canonical) return providerSlot;
//$$         if (!(slot.container instanceof DdsInventoryOwnerView)
//$$                 || ((DdsInventoryOwnerView) slot.container).ddsCanonicalContainer() != canonical
//$$                 || !(slot.container instanceof DdsCanonicalSlotMapping)) return -1;
//$$         if (slot.container instanceof DdsDirectSlotAccess
//$$                 && !((DdsDirectSlotAccess) slot.container).ddsAllowsDirectSlotAccess(providerSlot)) return -1;
//$$         return ((DdsCanonicalSlotMapping) slot.container).ddsCanonicalSlot(providerSlot);
//$$     }
//$$
//$$     static DdsPlayerInventoryCanonicalEnderMenu create(int id, Inventory viewerInventory, ServerPlayer viewer,
//$$                                                          ServerPlayer target, String providerId,
//$$                                                          AbstractContainerMenu authorizationMenu,
//$$                                                          ProviderSlotPolicy[] policies, DdsReopenableMenu rootReturn) {
//$$         if (viewer == null || viewerInventory == null || target == null || providerId == null || providerId.trim().isEmpty()
//$$                 || authorizationMenu == null || policies == null) return null;
//$$         Container canonical = target.getEnderChestInventory();
//$$         int size = canonical.getContainerSize(), rows = size / 9;
//$$         if ((size != 27 && size != 54) || rows * 9 != size || policies.length != size) return null;
//$$         try { if (!authorizationMenu.stillValid(viewer)) return null; }
//$$         catch (RuntimeException ignored) { return null; }
//$$         return new DdsPlayerInventoryCanonicalEnderMenu(id, viewerInventory, viewer, target, canonical, rows,
//$$                 providerId, authorizationMenu, policies, rootReturn);
//$$     }
//$$
//$$     private DdsPlayerInventoryCanonicalEnderMenu(int id, Inventory viewerInventory, ServerPlayer viewer,
//$$                                                   ServerPlayer target, Container canonical, int rows,
//$$                                                   String providerId, AbstractContainerMenu authorizationMenu,
//$$                                                   ProviderSlotPolicy[] policies, DdsReopenableMenu rootReturn) {
//$$         super(rows == 3 ? MenuType.GENERIC_9x3 : MenuType.GENERIC_9x6, id);
//$$         this.viewer = viewer;
//$$         this.viewerConnection = viewer.connection;
//$$         this.target = target;
//$$         this.canonical = canonical;
//$$         this.rows = rows;
//$$         this.providerId = providerId;
//$$         this.authorizationMenu = authorizationMenu;
//$$         this.policies = policies.clone();
//$$         this.rootReturn = rootReturn;
//$$         ProviderContainer provider = new ProviderContainer(target, canonical, this.policies);
//$$         for (int row = 0; row < rows; row++)
//$$             for (int column = 0; column < 9; column++) {
//$$                 int slot = column + row * 9;
//$$                 addSlot(new ProviderSlot(provider, slot, 8 + column * 18, 18 + row * 18, this.policies[slot]));
//$$             }
//$$         int offset = (rows - 4) * 18;
//$$         for (int row = 0; row < 3; row++)
//$$             for (int column = 0; column < 9; column++)
//$$                 addSlot(new Slot(viewerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + offset));
//$$         for (int column = 0; column < 9; column++)
//$$             addSlot(new Slot(viewerInventory, column, 8 + column * 18, 161 + offset));
//$$         canonical.startOpen(viewer);
//$$     }
//$$
//$$     public ServerPlayer target() { return target; }
//$$     public Container canonicalContainer() { return canonical; }
//$$     public int canonicalSize() { return rows * 9; }
//$$     public String providerId() { return providerId; }
//$$     public DdsReopenableMenu rootReturn() { return rootReturn; }
//$$     static Component providerTitle(String providerId, ServerPlayer target) {
//$$         if (target == null) return Component.empty();
//$$         return "gca".equals(providerId)
//$$                 ? Component.translatable("gca.player.ender_chest", target.getDisplayName())
//$$                 : target.getName();
//$$     }
//$$
//$$     @Override public boolean stillValid(Player player) {
//$$         if (player != viewer || viewer.connection != viewerConnection || !PlayerInventoryExtension.enabled()
//$$                 || !target.isAlive() || target.getEnderChestInventory() != canonical
//$$                 || canonical.getContainerSize() != rows * 9) return false;
//$$         MinecraftServer server = viewer.level().getServer();
//$$         if (server == null || server.getPlayerList().getPlayer(target.getUUID()) != target) return false;
//$$         if (("gca".equals(providerId) || "org".equals(providerId))
//$$                 && !providerAuthorizesWholeEnder(authorizationMenu, viewer, target, providerId, canonical)) return false;
//$$         try { return authorizationMenu.stillValid(viewer); }
//$$         catch (RuntimeException ignored) { return false; }
//$$     }
//$$
//$$     @Override public void removed(Player player) {
//$$         super.removed(player);
//$$         canonical.stopOpen(player);
//$$     }
//$$
//$$     @Override public ItemStack quickMoveStack(Player player, int index) {
//$$         if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
//$$         Slot slot = slots.get(index);
//$$         if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
//$$         ItemStack stack = slot.getItem(), original = stack.copy();
//$$         int storage = rows * 9;
//$$         if (index < storage) {
//$$             if (!moveItemStackTo(stack, storage, slots.size(), true)) return ItemStack.EMPTY;
//$$         } else if (!moveItemStackTo(stack, 0, storage, false)) return ItemStack.EMPTY;
//$$         if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
//$$         if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
//$$         slot.onTake(player, stack);
//$$         return original;
//$$     }
//$$
//#if MC >= 260102
//$$     @Override public void clicked(int slotId, int button, ContainerInput type, Player player) {
//$$         if (type == ContainerInput.SWAP && button == DDS_SORT_SCRATCH_BUTTON) { swapWithCarried(slotId, player); return; }
//$$         super.clicked(slotId, button, type, player);
//$$     }
//#else
//$$     @Override public void clicked(int slotId, int button, ClickType type, Player player) {
//$$         if (type == ClickType.SWAP && button == DDS_SORT_SCRATCH_BUTTON) { swapWithCarried(slotId, player); return; }
//$$         super.clicked(slotId, button, type, player);
//$$     }
//#endif
//$$
//$$     private void swapWithCarried(int slotId, Player player) {
//$$         if (slotId < 0 || slotId >= rows * 9) return;
//$$         Slot slot = getSlot(slotId);
//$$         ItemStack slotStack = slot.getItem(), carried = getCarried();
//$$         if (!slot.mayPickup(player) || !carried.isEmpty() && !slot.mayPlace(carried)) return;
//$$         slot.set(carried); setCarried(slotStack);
//$$     }
//$$
//$$     @Override public AbstractContainerMenu ddsCreateEnderMenu(int id, Inventory viewerInventory, ServerPlayer menuViewer) {
//$$         return menuViewer == viewer && stillValid(viewer)
//$$                 ? create(id, viewerInventory, viewer, target, providerId, authorizationMenu, policies, rootReturn) : null;
//$$     }
//$$
//$$     @Override public boolean ddsCanReopen(ServerPlayer candidate) {
//$$         return candidate == viewer && candidate.connection == viewerConnection && stillValid(candidate);
//$$     }
//$$
//$$     @Override public boolean ddsReopen(ServerPlayer candidate) {
//$$         if (!ddsCanReopen(candidate)) return false;
//$$         return candidate.openMenu(new SimpleMenuProvider((id, inventory, player) -> {
//$$             DdsPlayerInventoryCanonicalEnderMenu reopened = create(
//$$                     id, inventory, viewer, target, providerId, authorizationMenu, policies, rootReturn);
//$$             if (reopened != null) DdsFakePlayerServerNetwork.sendContextFromMenu(candidate, reopened);
//$$             return reopened;
//$$         }, providerTitle(providerId, target))).isPresent();
//$$     }
//$$
//$$     static final class ProviderSlotPolicy {
//$$         private final Slot source;
//$$         private ProviderSlotPolicy(Slot source) { this.source = source; }
//$$         boolean mayPickup(Player player) { try { return source.mayPickup(player); } catch (RuntimeException ignored) { return false; } }
//$$         boolean mayPlace(ItemStack stack) { try { return source.mayPlace(stack); } catch (RuntimeException ignored) { return false; } }
//$$     }
//$$
//$$     private static final class ProviderSlot extends Slot {
//$$         private final ProviderSlotPolicy policy;
//$$         private ProviderSlot(Container container, int slot, int x, int y, ProviderSlotPolicy policy) {
//$$             super(container, slot, x, y); this.policy = policy;
//$$         }
//$$         @Override public boolean mayPickup(Player player) { return policy.mayPickup(player) && super.mayPickup(player); }
//$$         @Override public boolean mayPlace(ItemStack stack) { return policy.mayPlace(stack) && super.mayPlace(stack); }
//$$     }
//$$
//$$     private static final class ProviderContainer implements Container, DdsInventoryOwnerView, DdsDirectSlotAccess, DdsCanonicalSlotMapping {
//$$         private final ServerPlayer owner;
//$$         private final Container delegate;
//$$         private final ProviderSlotPolicy[] policies;
//$$         private ProviderContainer(ServerPlayer owner, Container delegate, ProviderSlotPolicy[] policies) {
//$$             this.owner = owner; this.delegate = delegate; this.policies = policies;
//$$         }
//$$         @Override public int getContainerSize() { return delegate.getContainerSize(); }
//$$         @Override public boolean isEmpty() { return delegate.isEmpty(); }
//$$         @Override public ItemStack getItem(int slot) { return delegate.getItem(slot); }
//$$         @Override public ItemStack removeItem(int slot, int count) { return delegate.removeItem(slot, count); }
//$$         @Override public ItemStack removeItemNoUpdate(int slot) { return delegate.removeItemNoUpdate(slot); }
//$$         @Override public void setItem(int slot, ItemStack stack) { delegate.setItem(slot, stack); }
//$$         @Override public int getMaxStackSize() { return delegate.getMaxStackSize(); }
//#if MC >= 12006
//$$         @Override public int getMaxStackSize(ItemStack stack) { return delegate.getMaxStackSize(stack); }
//#endif
//$$         @Override public void setChanged() { delegate.setChanged(); }
//$$         @Override public boolean stillValid(Player player) { return delegate.stillValid(player); }
//$$         @Override public boolean canPlaceItem(int slot, ItemStack stack) {
//$$             return slot >= 0 && slot < policies.length && policies[slot].mayPlace(stack) && delegate.canPlaceItem(slot, stack);
//$$         }
//$$         @Override public void clearContent() { delegate.clearContent(); }
//$$         @Override public ServerPlayer ddsInventoryOwner() { return owner; }
//$$         @Override public Container ddsCanonicalContainer() { return delegate; }
//$$         @Override public boolean ddsAllowsDirectSlotAccess(int slot) { return slot >= 0 && slot < policies.length; }
//$$         @Override public int ddsCanonicalSlot(int providerSlot) { return providerSlot >= 0 && providerSlot < policies.length ? providerSlot : -1; }
//$$     }
//$$ }
//#else
public final class DdsPlayerInventoryCanonicalEnderMenu { private DdsPlayerInventoryCanonicalEnderMenu() {} }
//#endif
