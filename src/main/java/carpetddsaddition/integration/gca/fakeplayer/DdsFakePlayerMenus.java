/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.CarpetDDSAddition;
//$$ import net.minecraft.core.Registry;
//#if MC >= 11903
//$$ import net.minecraft.core.registries.BuiltInRegistries;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//$$ import net.minecraft.world.entity.player.Inventory;
//#if MC >= 11904
//$$ import net.minecraft.world.flag.FeatureFlagSet;
//$$ import net.minecraft.world.flag.FeatureFlags;
//#endif
//$$ import net.minecraft.world.inventory.MenuType;
//$$ import java.lang.reflect.Constructor;
//$$ import java.lang.reflect.InvocationHandler;
//$$ import java.lang.reflect.Method;
//$$ import java.lang.reflect.Proxy;
//$$
//$$ public final class DdsFakePlayerMenus {
//$$     private static MenuType inventoryType, ender3Type, ender6Type;
//$$     private DdsFakePlayerMenus() {}
//$$
//$$     public static synchronized void register() {
//$$         if (inventoryType != null) return;
//$$         inventoryType = register("fake_player_inventory", 0);
//$$         ender3Type = register("fake_player_ender_chest_3", 3);
//$$         ender6Type = register("fake_player_ender_chest_6", 6);
//$$     }
//$$
//$$     private static MenuType register(String path, int rows) {
//$$         try {
//$$             Constructor<?> constructor = findMenuTypeConstructor();
//$$             Class<?> supplierClass = constructor.getParameterTypes()[0];
//$$             Object supplier = Proxy.newProxyInstance(MenuType.class.getClassLoader(), new Class<?>[]{supplierClass}, new MenuSupplierInvocationHandler(rows));
//$$             constructor.setAccessible(true);
//#if MC >= 11904
//$$             MenuType created = (MenuType) constructor.newInstance(supplier, FeatureFlags.VANILLA_SET);
//#else
//$$             MenuType created = (MenuType) constructor.newInstance(supplier);
//#endif
//#if MC >= 12111
//$$             Identifier id = Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path);
//#elseif MC >= 12101
//$$             ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path);
//#else
//$$             ResourceLocation id = new ResourceLocation(CarpetDDSAddition.MOD_ID, path);
//#endif
//#if MC >= 11903
//$$             return (MenuType) Registry.register(BuiltInRegistries.MENU, id, created);
//#else
//$$             return (MenuType) Registry.register(Registry.MENU, id, created);
//#endif
//$$         } catch (ReflectiveOperationException exception) { throw new IllegalStateException("Failed to register DDS fake-player menu type " + path, exception); }
//$$     }
//$$
//$$     private static Constructor<?> findMenuTypeConstructor() {
//$$         for (Constructor<?> constructor : MenuType.class.getDeclaredConstructors()) {
//$$             Class<?>[] parameters = constructor.getParameterTypes();
//#if MC >= 11904
//$$             if (parameters.length == 2 && parameters[0].isInterface() && parameters[1] == FeatureFlagSet.class) return constructor;
//#else
//$$             if (parameters.length == 1 && parameters[0].isInterface()) return constructor;
//#endif
//$$         }
//$$         throw new IllegalStateException("Unable to locate compatible MenuType constructor");
//$$     }
//$$
//$$     public static MenuType type() {
//$$         if (inventoryType == null) throw new IllegalStateException("DDS fake-player menu types are not registered");
//$$         return inventoryType;
//$$     }
//$$     public static MenuType enderType(int rows) {
//$$         MenuType type = rows == 3 ? ender3Type : rows == 6 ? ender6Type : null;
//$$         if (type == null) throw new IllegalStateException("DDS fake-player Ender Chest menu type is not registered for " + rows + " rows");
//$$         return type;
//$$     }
//$$     public static int enderRows(MenuType type) { return type == ender3Type ? 3 : type == ender6Type ? 6 : 0; }
//$$
//$$     private static final class MenuSupplierInvocationHandler implements InvocationHandler {
//$$         private final int rows;
//$$         private MenuSupplierInvocationHandler(int rows) { this.rows = rows; }
//$$         @Override public Object invoke(Object proxy, Method method, Object[] args) {
//$$             if (args != null && args.length == 2 && args[0] instanceof Integer && args[1] instanceof Inventory)
//$$                 return rows == 0 ? DdsFakePlayerMenu.client(((Integer) args[0]).intValue(), (Inventory) args[1])
//$$                         : DdsFakePlayerEnderChestMenu.client(((Integer) args[0]).intValue(), (Inventory) args[1], rows);
//$$             switch (method.getName()) {
//$$                 case "toString": return "DDSFakePlayerMenuSupplier[rows=" + rows + "]";
//$$                 case "hashCode": return System.identityHashCode(proxy);
//$$                 case "equals": return args != null && args.length == 1 && proxy == args[0];
//$$                 default: throw new UnsupportedOperationException("Unsupported MenuType supplier method: " + method);
//$$             }
//$$         }
//$$     }
//$$ }
//#else
public final class DdsFakePlayerMenus { private DdsFakePlayerMenus() {} public static void register() {} }
//#endif
