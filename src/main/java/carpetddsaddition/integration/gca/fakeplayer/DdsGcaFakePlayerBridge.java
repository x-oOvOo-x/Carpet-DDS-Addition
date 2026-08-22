/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerPackets;
//$$ import carpetddsaddition.network.DdsServerNetwork;
//$$ import net.fabricmc.loader.api.FabricLoader;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.item.Items;
//$$ import java.lang.reflect.Field;
//$$
//$$ public final class DdsGcaFakePlayerBridge {
//$$     private static final boolean GCA_LOADED = FabricLoader.getInstance().isModLoaded("gca");
//$$     private static final boolean SAFE_DIRECT_INVENTORY = hasSafeDirectInventoryModel();
//$$     private static final Field OPEN_INVENTORY_RULE_FIELD = resolveRuleField("openFakePlayerInventory");
//$$     private static final Field OPEN_ENDER_CHEST_RULE_FIELD = resolveRuleField("openFakePlayerEnderChest");
//$$     private DdsGcaFakePlayerBridge() {}
//$$
//$$     public static boolean shouldUseDdsMenu(ServerPlayer viewer) {
//$$         return SAFE_DIRECT_INVENTORY && isGcaInventoryRuleEnabled() && DdsServerNetwork.doesClientSupport(viewer, DdsFakePlayerPackets.ACTION_C2S);
//$$     }
//$$
//$$     /** Mirrors GCA's openFakePlayerEnderChest rule semantics. */
//$$     public static boolean shouldOpenGcaEnderChest(ServerPlayer viewer) {
//$$         String value = readStringRule(OPEN_ENDER_CHEST_RULE_FIELD);
//$$         if ("true".equals(value)) return true;
//$$         if (!"ender_chest".equals(value)) return false;
//$$         return viewer.getMainHandItem().is(Items.ENDER_CHEST) || viewer.getOffhandItem().is(Items.ENDER_CHEST);
//$$     }
//$$
//$$     private static boolean isGcaInventoryRuleEnabled() {
//$$         Field field = OPEN_INVENTORY_RULE_FIELD;
//$$         if (field == null) return false;
//$$         try { return field.getBoolean(null); }
//$$         catch (IllegalAccessException | IllegalArgumentException ignored) { return false; }
//$$     }
//$$
//$$     private static String readStringRule(Field field) {
//$$         if (field == null) return null;
//$$         try { Object value = field.get(null); return value instanceof String ? (String) value : null; }
//$$         catch (IllegalAccessException | IllegalArgumentException ignored) { return null; }
//$$     }
//$$
//$$     private static boolean hasSafeDirectInventoryModel() {
//$$         if (!GCA_LOADED) return false;
//$$         try {
//$$             ClassLoader loader = DdsGcaFakePlayerBridge.class.getClassLoader();
//$$             // Legacy GCA used a detached shadow inventory; direct DDS writes are unsafe there.
//$$             try { Class.forName("dev.dubhe.gugle.carpet.tools.FakePlayer", false, loader); return false; }
//$$             catch (ClassNotFoundException ignored) {}
//$$             Class<?> gcaPlayer = Class.forName("dev.dubhe.gugle.carpet.tools.player.IGcaPlayer", false, loader);
//$$             Class<?> inventoryContainer = Class.forName("dev.dubhe.gugle.carpet.tools.player.PlayerInventoryContainer", false, loader);
//$$             return gcaPlayer.getMethod("getInventoryContainer").getReturnType() == inventoryContainer;
//$$         } catch (ReflectiveOperationException | LinkageError ignored) { return false; }
//$$     }
//$$
//$$     private static Field resolveRuleField(String fieldName) {
//$$         if (!GCA_LOADED) return null;
//$$         try { return Class.forName("dev.dubhe.gugle.carpet.GcaSetting").getField(fieldName); }
//$$         catch (ReflectiveOperationException | LinkageError ignored) { return null; }
//$$     }
//$$ }
//#else
public final class DdsGcaFakePlayerBridge { private DdsGcaFakePlayerBridge() {} }
//#endif
