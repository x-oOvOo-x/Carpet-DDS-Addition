/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.quickcontaineraccess.network;

/** Stable DDS Protocol v1 packet ids owned by quickContainerAccess. */
public final class QuickContainerAccessPackets {
    public static final String OPEN_SLOT_C2S = "qca_open_slot_c2s";
    public static final String OPEN_INVENTORY_SLOT_C2S = "qca_open_inventory_slot_c2s";
    public static final String STORAGE_CLICK_C2S = "qca_storage_click_c2s";
    public static final String CREATIVE_STORAGE_C2S = "qca_creative_storage_c2s";
    public static final String NAVIGATE_BACK_C2S = "qca_navigate_back_c2s";
    public static final String CREATIVE_STORAGE_RESULT_S2C = "qca_creative_storage_result_s2c";
    public static final String NAVIGATION_STATE_S2C = "qca_navigation_state_s2c";
    private QuickContainerAccessPackets() {}
}
