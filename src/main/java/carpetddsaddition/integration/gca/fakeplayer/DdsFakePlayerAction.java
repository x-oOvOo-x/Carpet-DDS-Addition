/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer;

public enum DdsFakePlayerAction {
    ATTACK_INTERVAL("attack_interval"), ATTACK_CONTINUOUS("attack_continuous"), STOP_ATTACK("stop_attack"),
    USE_INTERVAL("use_interval"), USE_CONTINUOUS("use_continuous"), STOP_USE("stop_use"), STOP_ALL("stop_all"),
    DROP_ALL("drop_all"), DISCONNECT("disconnect"), SELECT_HOTBAR("select_hotbar");

    private static final DdsFakePlayerAction[] VALUES = values();
    private final String wireName;

    DdsFakePlayerAction(String wireName) { this.wireName = wireName; }
    public String wireName() { return wireName; }

    public static DdsFakePlayerAction fromWireName(String value) {
        if (value != null) for (DdsFakePlayerAction action : VALUES) if (action.wireName.equals(value)) return action;
        return null;
    }
}
