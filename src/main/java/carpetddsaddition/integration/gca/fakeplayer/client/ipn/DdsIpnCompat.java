/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.client.ipn;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.client.DdsFakePlayerScreen;
//$$ import net.minecraft.client.Minecraft;
//$$ import java.lang.reflect.InvocationTargetException;
//$$ import java.lang.reflect.Method;
//$$ import java.lang.reflect.Proxy;
//$$ import java.util.ArrayList;
//$$ import java.util.Collections;
//$$ import java.util.List;
//$$
//$$ public final class DdsIpnCompat {
//$$     private static final String AREA_TYPES = "org.anti_ad.mc.ipnext.inventory.AreaTypes", AREA_TYPE = "org.anti_ad.mc.ipnext.inventory.AreaType", ITEM_AREA = "org.anti_ad.mc.ipnext.inventory.ItemArea";
//$$     private static Object sortSource, sortProxy, storageSource, storageProxy;
//$$     private static volatile boolean broken;
//$$     private DdsIpnCompat() {}
//$$
//$$     public static boolean level1Ready() {
//$$         if (broken) return false;
//$$         try {
//$$             Class<?> types = Class.forName(AREA_TYPES), area = Class.forName(AREA_TYPE), itemArea = Class.forName(ITEM_AREA);
//$$             Object instance = types.getField("INSTANCE").get(null);
//$$             Method storage = types.getDeclaredMethod("getItemStorage"), sortable = types.getDeclaredMethod("getSortableItemStorage");
//$$             if (storage.getReturnType() != area || sortable.getReturnType() != area
//$$                     || storage.invoke(instance) != storageProxy || sortable.invoke(instance) != sortProxy) return false;
//$$             boolean get = false; Method factory = null;
//$$             for (Method method : area.getMethods()) if (method.getName().equals("getItemArea") && method.getParameterCount() == 2 && method.getReturnType() == itemArea) get = true;
//$$             Object companion = itemArea.getField("Companion").get(null);
//$$             for (Method method : companion.getClass().getMethods()) if (method.getName().equals("invoke") && method.getParameterCount() == 3 && method.getReturnType() == itemArea) { factory = method; break; }
//$$             if (!get || factory == null) return false;
//$$             Object empty = factory.invoke(companion, Collections.emptyList(), Collections.emptyList(), false);
//$$             Method minus = itemArea.getMethod("minus", itemArea);
//$$             return minus.getReturnType() == itemArea && itemArea.isInstance(minus.invoke(empty, empty));
//$$         } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) { return false; }
//$$     }
//$$
//$$     public static synchronized Object wrapSortableArea(Object area) { return wrapArea(area, DdsFakePlayerMenu.FAKE_HOTBAR_START, true); }
//$$     public static synchronized Object wrapStorageArea(Object area) { return wrapArea(area, DdsFakePlayerMenu.FAKE_STORAGE_END, false); }
//$$
//$$     private static Object wrapArea(Object area, int end, boolean sortable) {
//$$         if (area == null) return null;
//$$         Object source = sortable ? sortSource : storageSource, cached = sortable ? sortProxy : storageProxy;
//$$         if (area == cached || area == source && cached != null) return cached;
//$$         try {
//$$             Class<?> type = Class.forName(AREA_TYPE);
//$$             if (!type.isInstance(area)) return area;
//$$             Object wrapped = areaProxy(type, area, end);
//$$             if (sortable) { sortSource = area; sortProxy = wrapped; } else { storageSource = area; storageProxy = wrapped; }
//$$             return wrapped;
//$$         } catch (ReflectiveOperationException | LinkageError | RuntimeException e) { broken = true; return area; }
//$$     }
//$$
//$$     private static Object areaProxy(Class<?> type, Object area, int end) {
//$$         return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
//$$             if (isGetItemArea(method, args) && isFakeScreen()) return fixedArea((List<?>) args[1], end);
//$$             if (method.getName().equals("minus") && args != null && args.length == 1 && type.isInstance(args[0]) && isFakeScreen()) {
//$$                 Object original = invoke(method, area, args), other = args[0];
//$$                 return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (derived, child, childArgs) -> {
//$$                     if (isGetItemArea(child, childArgs) && isFakeScreen()) {
//$$                         Object left = fixedArea((List<?>) childArgs[1], end);
//$$                         return left.getClass().getMethod("minus", Class.forName(ITEM_AREA)).invoke(left, invoke(child, other, childArgs));
//$$                     }
//$$                     return invoke(child, original, childArgs);
//$$                 });
//$$             }
//$$             return invoke(method, area, args);
//$$         });
//$$     }
//$$
//$$     public static boolean isFakeScreen() {
//#if MC >= 260200
//$$         return Minecraft.getInstance().gui.screen() instanceof DdsFakePlayerScreen;
//#else
//$$         return Minecraft.getInstance().screen instanceof DdsFakePlayerScreen;
//#endif
//$$     }
//$$     private static boolean isGetItemArea(Method method, Object[] args) { return method.getName().equals("getItemArea") && method.getParameterCount() == 2 && args != null && args.length == 2 && args[1] instanceof List<?>; }
//$$     private static Object invoke(Method method, Object target, Object[] args) throws Throwable {
//$$         try { return method.invoke(target, args); } catch (InvocationTargetException e) { throw e.getCause(); }
//$$     }
//$$     private static Object fixedArea(List<?> vanillaSlots, int end) {
//$$         try {
//$$             Class<?> itemArea = Class.forName(ITEM_AREA); Object companion = itemArea.getField("Companion").get(null);
//$$             List<Integer> indices = new ArrayList<>(end - DdsFakePlayerMenu.FAKE_STORAGE_START);
//$$             for (int i = DdsFakePlayerMenu.FAKE_STORAGE_START; i < end; i++) indices.add(i);
//$$             for (Method method : companion.getClass().getMethods()) {
//$$                 if (!method.getName().equals("invoke") || method.getParameterCount() != 3 || method.getReturnType() != itemArea) continue;
//$$                 try { return method.invoke(companion, vanillaSlots, indices, false); } catch (IllegalArgumentException ignored) {}
//$$             }
//$$         } catch (ReflectiveOperationException | LinkageError | RuntimeException e) { broken = true; throw new IllegalStateException("DDS IPN Level 1 adapter failed closed", e); }
//$$         broken = true; throw new IllegalStateException("DDS IPN Level 1 adapter failed closed");
//$$     }
//$$ }
//#else
public final class DdsIpnCompat {
    private DdsIpnCompat() {}
    public static boolean level1Ready() { return false; }
    public static boolean isFakeScreen() { return false; }
}
//#endif
