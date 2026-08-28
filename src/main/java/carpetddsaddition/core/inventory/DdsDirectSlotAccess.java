/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.inventory;

/** Marks backing-container slots that may be mutated directly by DDS inventory features. */
public interface DdsDirectSlotAccess {
    boolean ddsAllowsDirectSlotAccess(int slot);
}
