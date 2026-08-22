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

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

/** UUID identifies an annotation; BlockPos tracks its current block. */
public final class BlockAnnotation {
    private final UUID id;
    private final String dimension, blockId;
    private BlockPos pos;
    private String rawText;

    public BlockAnnotation(UUID id, String dimension, BlockPos pos, String blockId, String rawText) {
        this.id = id;
        this.dimension = dimension;
        this.pos = pos.immutable();
        this.blockId = blockId;
        this.rawText = rawText;
    }

    public UUID getId() { return id; }
    public String getDimension() { return dimension; }
    public BlockPos getPos() { return pos; }
    public void setPos(BlockPos pos) { this.pos = pos.immutable(); }
    public String getBlockId() { return blockId; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public Vec3 getDisplayPosition() {
        return new Vec3(pos.getX() + 0.5D, pos.getY() + EnhancedNameTag.LABEL_Y_OFFSET, pos.getZ() + 0.5D);
    }
}
