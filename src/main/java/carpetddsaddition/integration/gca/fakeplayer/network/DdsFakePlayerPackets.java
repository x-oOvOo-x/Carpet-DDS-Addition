/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

/** Active DDS protocol v1 fake-player packet identifiers. */
public final class DdsFakePlayerPackets {
    public static final String ACTION_C2S = "fake_player_action_c2s";
    public static final String PROVIDER_ACTION_C2S = "fake_player_provider_action_c2s";
    public static final String CANONICAL_ENDER_C2S = "player_inventory_canonical_ender_c2s";
    public static final String RETURN_PARENT_C2S = "player_inventory_return_parent_c2s";
    public static final String CONTEXT_S2C = "fake_player_context_s2c";
    public static final String STATE_S2C = "fake_player_state_s2c";
    /** Capability marker advertised through HELLO; it is never emitted as a feature payload. */
    public static final String SESSION_V3_CAPABILITY_S2C = "player_inventory_session_v3_s2c";
    public static final String STRUCTURE_RESULT_S2C = "player_inventory_structure_result_s2c";
    private DdsFakePlayerPackets() {}
}
