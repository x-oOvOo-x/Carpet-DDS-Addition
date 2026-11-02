/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.core.inventory;

/**
 * Marks a server menu that implements DDS' reserved SWAP button 9 as an exact
 * swap between the clicked storage slot and the menu carried stack.
 *
 * Backing-container direct access is not sufficient: client automation may
 * predict this operation only when the current server menu implements it too.
 */
public interface DdsCarriedScratchSwap {
}
