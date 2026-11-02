/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.inventory;

/** Maps a Provider-facing slot index to its canonical backing-container index. */
public interface DdsCanonicalSlotMapping {
    int ddsCanonicalSlot(int providerSlot);
}
