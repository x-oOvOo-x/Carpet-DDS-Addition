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
//$$     private static MenuType type;
//$$     private DdsFakePlayerMenus() {}
//$$
//$$     public static synchronized void register() {
//$$         if (type != null) return;
//$$         try {
//$$             Constructor<?> constructor = findMenuTypeConstructor();
//$$             Class<?> supplierClass = constructor.getParameterTypes()[0];
//$$             Object supplier = Proxy.newProxyInstance(MenuType.class.getClassLoader(), new Class<?>[]{supplierClass}, new MenuSupplierInvocationHandler());
//$$             constructor.setAccessible(true);
//#if MC >= 11904
//$$             MenuType created = (MenuType) constructor.newInstance(supplier, FeatureFlags.VANILLA_SET);
//#else
//$$             MenuType created = (MenuType) constructor.newInstance(supplier);
//#endif
//#if MC >= 12111
//$$             Identifier id = Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "fake_player_inventory");
//#elseif MC >= 12101
//$$             ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, "fake_player_inventory");
//#else
//$$             ResourceLocation id = new ResourceLocation(CarpetDDSAddition.MOD_ID, "fake_player_inventory");
//#endif
//#if MC >= 11903
//$$             type = (MenuType) Registry.register(BuiltInRegistries.MENU, id, created);
//#else
//$$             type = (MenuType) Registry.register(Registry.MENU, id, created);
//#endif
//$$         } catch (ReflectiveOperationException exception) { throw new IllegalStateException("Failed to register DDS fake-player menu type", exception); }
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
//$$         if (type == null) throw new IllegalStateException("DDS fake-player menu type is not registered");
//$$         return type;
//$$     }
//$$
//$$     private static final class MenuSupplierInvocationHandler implements InvocationHandler {
//$$         @Override public Object invoke(Object proxy, Method method, Object[] args) {
//$$             if (args != null && args.length == 2 && args[0] instanceof Integer && args[1] instanceof Inventory)
//$$                 return DdsFakePlayerMenu.client(((Integer) args[0]).intValue(), (Inventory) args[1]);
//$$             switch (method.getName()) {
//$$                 case "toString": return "DDSFakePlayerMenuSupplier";
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
