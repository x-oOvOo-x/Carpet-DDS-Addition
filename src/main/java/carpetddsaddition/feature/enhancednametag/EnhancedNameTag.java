/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.feature.enhancednametag;

import carpetddsaddition.generated.DDSRules;

public final class EnhancedNameTag {
    public static final double TARGET_DISTANCE = 16.0D, LABEL_Y_OFFSET = 1.15D;
    public static final int TRACK_INTERVAL_TICKS = 2;
    public static final float TEXT_SCALE = 0.75F;
    private EnhancedNameTag() {}

    /** Disabling the rule blocks edits only; existing entity/block labels remain active. */
    public static boolean editingEnabled() { return DDSRules.enhancedNameTag(); }
}
