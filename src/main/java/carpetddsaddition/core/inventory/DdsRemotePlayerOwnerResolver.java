/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Resolves the authoritative remote player behind optional provider inventory views. */
public final class DdsRemotePlayerOwnerResolver {
    private DdsRemotePlayerOwnerResolver() {}

    public static ServerPlayer owner(Object value) {
        if (value instanceof DdsInventoryOwnerView)
            return ((DdsInventoryOwnerView) value).ddsInventoryOwner();
        return orgOwner(value);
    }

    public static ServerPlayer remoteInventoryOwnerForMenu(ServerPlayer viewer, AbstractContainerMenu menu) {
        if (viewer == null || menu == null) return null;

        ServerPlayer owner = directInventoryOwner(menu);
        if (owner != null && owner != viewer) return owner;

        for (Slot slot : menu.slots) {
            owner = directInventoryOwner(slot.container);
            if (owner != null && owner != viewer) return owner;
        }
        return null;
    }

    private static ServerPlayer directInventoryOwner(Object value) {
        if (value instanceof DdsInventoryOwnerView) {
            DdsInventoryOwnerView view = (DdsInventoryOwnerView) value;
            if (view.ddsCanonicalContainer() == null) return view.ddsInventoryOwner();
            return null;
        }
        if (value == null) return null;
        String name = value.getClass().getName();
        if (name.endsWith(".PlayerInventoryScreenHandler")
                || name.endsWith(".WithButtonPlayerInventoryScreenHandler"))
            return orgOwner(value);
        return null;
    }

    private static ServerPlayer orgOwner(Object value) {
        if (value == null) return null;
        String name = value.getClass().getName();
        try {
            if (name.endsWith(".PlayerInventoryScreenHandler")
                    || name.endsWith(".PlayerEnderChestScreenHandler")) {
                Field field = findField(value.getClass(), "player");
                if (field == null) return null;
                field.setAccessible(true);
                Object owner = field.get(value);
                return owner instanceof ServerPlayer ? (ServerPlayer) owner : null;
            }
            if (name.endsWith(".WithButtonPlayerInventoryScreenHandler")) {
                Field field = findField(value.getClass(), "inventory");
                if (field == null) return null;
                field.setAccessible(true);
                Object inventory = field.get(value);
                if (inventory == null) return null;
                Method method = inventory.getClass().getMethod("getPlayer");
                Object owner = method.invoke(inventory);
                return owner instanceof ServerPlayer ? (ServerPlayer) owner : null;
            }
        } catch (ReflectiveOperationException | LinkageError | SecurityException ignored) {}
        return null;
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try { return current.getDeclaredField(name); }
            catch (NoSuchFieldException ignored) {}
        }
        return null;
    }

}
