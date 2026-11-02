/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.playerinventory;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsReopenableMenu;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.Container;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import java.util.UUID;
//$$
//$$ /** Immutable provider-neutral identity for one already-authorized remote player menu. */
//$$ public final class DdsPlayerInventorySession {
//$$     public enum TargetType {
//$$         LIVE_FAKE("fake"), LIVE_PLAYER("live_player"), OFFLINE_PLAYER("offline_player");
//$$         private final String wireName;
//$$         TargetType(String wireName) { this.wireName = wireName; }
//$$         public String wireName() { return wireName; }
//$$     }
//$$     public enum InventoryKind {
//$$         INVENTORY("inventory"), CONTROLLER("controller"), ENDER_CHEST("ender_chest");
//$$         private final String wireName;
//$$         InventoryKind(String wireName) { this.wireName = wireName; }
//$$         public String wireName() { return wireName; }
//$$     }
//$$
//$$     private final String providerId;
//$$     private final int containerId;
//$$     private final UUID targetUuid;
//$$     private final String targetName;
//$$     private final TargetType targetType;
//$$     private final InventoryKind inventoryKind;
//$$     private final boolean carriedScratch;
//$$     private final String layoutId;
//$$     private final int layoutSchema;
//$$     private final String behaviorId;
//$$     private final ServerPlayer liveTarget;
//$$     private final Container canonicalContainer;
//$$     private final int canonicalSize;
//$$     private final ServerPlayer viewer;
//$$     private final Object viewerConnection;
//$$     private final AbstractContainerMenu menu;
//$$     private final long generation;
//$$     private final DdsReopenableMenu rootReturn;
//$$
//$$     private DdsPlayerInventorySession(String providerId, int containerId, UUID targetUuid, String targetName,
//$$                                       TargetType targetType, InventoryKind inventoryKind, boolean carriedScratch,
//$$                                       String layoutId, int layoutSchema, String behaviorId,
//$$                                       ServerPlayer liveTarget, Container canonicalContainer,
//$$                                       ServerPlayer viewer, AbstractContainerMenu menu, long generation,
//$$                                       DdsReopenableMenu rootReturn) {
//$$         if (providerId == null || providerId.trim().isEmpty()) throw new IllegalArgumentException("providerId");
//$$         if (targetUuid == null) throw new IllegalArgumentException("targetUuid");
//$$         if (targetType == null) throw new IllegalArgumentException("targetType");
//$$         if (inventoryKind == null) throw new IllegalArgumentException("inventoryKind");
//$$         if (layoutId == null || layoutId.trim().isEmpty()) throw new IllegalArgumentException("layoutId");
//$$         if (layoutSchema < 1) throw new IllegalArgumentException("layoutSchema");
//$$         if (behaviorId == null || behaviorId.trim().isEmpty()) throw new IllegalArgumentException("behaviorId");
//$$         if (generation < 0) throw new IllegalArgumentException("generation");
//$$         this.providerId = providerId;
//$$         this.containerId = containerId;
//$$         this.targetUuid = targetUuid;
//$$         this.targetName = targetName == null ? "" : targetName;
//$$         this.targetType = targetType;
//$$         this.inventoryKind = inventoryKind;
//$$         this.carriedScratch = carriedScratch;
//$$         this.layoutId = layoutId;
//$$         this.layoutSchema = layoutSchema;
//$$         this.behaviorId = behaviorId;
//$$         this.liveTarget = liveTarget;
//$$         this.canonicalContainer = canonicalContainer;
//$$         this.canonicalSize = canonicalContainer == null ? 0 : canonicalContainer.getContainerSize();
//$$         this.viewer = viewer;
//$$         this.viewerConnection = viewer == null ? null : viewer.connection;
//$$         this.menu = menu;
//$$         this.generation = generation;
//$$         this.rootReturn = rootReturn;
//$$     }
//$$
//$$     public static DdsPlayerInventorySession live(String providerId, int containerId, ServerPlayer target, boolean fake,
//$$                                                   InventoryKind kind, boolean carriedScratch, String layoutId,
//$$                                                   int layoutSchema, String behaviorId, Container canonicalContainer) {
//$$         if (target == null) return null;
//$$         return new DdsPlayerInventorySession(providerId, containerId, target.getUUID(), target.getName().getString(),
//$$                 fake ? TargetType.LIVE_FAKE : TargetType.LIVE_PLAYER, kind, carriedScratch,
//$$                 layoutId, layoutSchema, behaviorId, target, canonicalContainer, null, null, 0L, null);
//$$     }
//$$
//$$     public static DdsPlayerInventorySession offline(String providerId, int containerId, UUID uuid, String name,
//$$                                                      InventoryKind kind, String layoutId, int layoutSchema,
//$$                                                      String behaviorId, Container canonicalContainer) {
//$$         return uuid == null ? null : new DdsPlayerInventorySession(providerId, containerId, uuid, name,
//$$                 TargetType.OFFLINE_PLAYER, kind, false, layoutId, layoutSchema, behaviorId,
//$$                 null, canonicalContainer, null, null, 0L, null);
//$$     }
//$$
//$$     DdsPlayerInventorySession bind(ServerPlayer viewer, AbstractContainerMenu menu, long generation) {
//$$         if (viewer == null || menu == null || generation <= 0) throw new IllegalArgumentException("active session binding");
//$$         return new DdsPlayerInventorySession(providerId, containerId, targetUuid, targetName, targetType, inventoryKind,
//$$                 carriedScratch, layoutId, layoutSchema, behaviorId, liveTarget, canonicalContainer,
//$$                 viewer, menu, generation, rootReturn);
//$$     }
//$$
//$$     public String providerId() { return providerId; }
//$$     public int containerId() { return containerId; }
//$$     public UUID targetUuid() { return targetUuid; }
//$$     public String targetName() { return targetName; }
//$$     public TargetType targetType() { return targetType; }
//$$     public InventoryKind inventoryKind() { return inventoryKind; }
//$$     public boolean carriedScratch() { return carriedScratch; }
//$$     public String layoutId() { return layoutId; }
//$$     public int layoutSchema() { return layoutSchema; }
//$$     public String behaviorId() { return behaviorId; }
//$$     public ServerPlayer liveTarget() { return liveTarget; }
//$$     public Container canonicalContainer() { return canonicalContainer; }
//$$     public int canonicalSize() { return canonicalSize; }
//$$     public ServerPlayer viewer() { return viewer; }
//$$     public Object viewerConnection() { return viewerConnection; }
//$$     public AbstractContainerMenu menu() { return menu; }
//$$     public long generation() { return generation; }
//$$     public DdsReopenableMenu rootReturn() { return rootReturn; }
//$$     DdsPlayerInventorySession withRootReturn(DdsReopenableMenu root) {
//$$         if (root == rootReturn) return this;
//$$         return new DdsPlayerInventorySession(providerId, containerId, targetUuid, targetName, targetType, inventoryKind,
//$$                 carriedScratch, layoutId, layoutSchema, behaviorId, liveTarget, canonicalContainer,
//$$                 viewer, menu, generation, root);
//$$     }
//$$ }
//#else
public final class DdsPlayerInventorySession { private DdsPlayerInventorySession() {} }
//#endif
