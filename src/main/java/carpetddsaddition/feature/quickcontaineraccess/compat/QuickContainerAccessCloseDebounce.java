/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.compat;

//#if MC >= 11404 && MC <= 260200
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.inventory.AbstractContainerMenu;
//#endif

/** Prevents rapid repeated source-slot right-clicks from immediately closing a newly opened QCA menu. */
final class QuickContainerAccessCloseDebounce {
    private QuickContainerAccessCloseDebounce() {}

    //#if MC >= 11404 && MC <= 260200
    static final long GRACE_NANOS = 300_000_000L;
    private static final Map<AbstractContainerMenu, Long> LAST_SOURCE_CLICK = new IdentityHashMap<>();

    static synchronized void opened(AbstractContainerMenu menu) {
        if (menu != null) LAST_SOURCE_CLICK.put(menu, System.nanoTime());
    }

    static synchronized boolean suppress(AbstractContainerMenu menu) {
        Long previous = LAST_SOURCE_CLICK.get(menu);
        if (previous == null) return false;
        long now = System.nanoTime();
        if (!withinGrace(previous, now)) return false;
        LAST_SOURCE_CLICK.put(menu, now); // Require a quiet period before the same source slot can close the menu.
        return true;
    }

    static synchronized void forget(AbstractContainerMenu menu) { LAST_SOURCE_CLICK.remove(menu); }
    static synchronized void clear() { LAST_SOURCE_CLICK.clear(); }

    static boolean withinGrace(long previous, long now) {
        long elapsed = now - previous;
        return elapsed >= 0L && elapsed < GRACE_NANOS;
    }
    //#endif
}
