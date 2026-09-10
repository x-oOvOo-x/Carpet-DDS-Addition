/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

/** Stable DDS Protocol v1 packet ids and fake-player menu wire values. */
public final class DdsFakePlayerPackets {
    public static final String ACTION_C2S = "fake_player_action_c2s";
    public static final String OPEN_MENU_S2C = "fake_player_open_menu_s2c";
    public static final int MENU_NONE = 0, MENU_INVENTORY = 1, MENU_ENDER_3 = 3, MENU_ENDER_6 = 6;
    private DdsFakePlayerPackets() {}
    public static boolean isMenuLayout(int layout) {
        return layout == MENU_INVENTORY || layout == MENU_ENDER_3 || layout == MENU_ENDER_6;
    }
}
