/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.mixin.feature.undoredo;

//#if MC >= 12109
//$$ import carpetddsaddition.feature.undoredo.compat.UndoChunkAccess;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.level.chunk.ChunkAccess;
//$$ import net.minecraft.world.level.chunk.LevelChunkSection;
//$$ import net.minecraft.world.level.levelgen.Heightmap;
//$$ import net.minecraft.world.level.lighting.ChunkSkyLightSources;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import java.util.Map;
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12109
//$$ @Mixin(ChunkAccess.class)
//$$ public abstract class ChunkAccessMixin implements UndoChunkAccess {
//$$     @Shadow @Final protected LevelChunkSection[] sections;
//$$     @Shadow @Final protected Map<Heightmap.Types, Heightmap> heightmaps;
//$$     @Shadow protected ChunkSkyLightSources skyLightSources;
//$$     @Shadow public abstract void markUnsaved();
//$$
//$$     @Override
//$$     public BlockState dds$setBlockStateDirect(BlockPos pos, BlockState state) {
//$$         ChunkAccess chunk = (ChunkAccess) (Object) this;
//$$         int sectionIndex = chunk.getSectionIndex(pos.getY()), localX = pos.getX() & 15,
//$$                 localY = pos.getY() & 15, localZ = pos.getZ() & 15;
//$$         LevelChunkSection section = sections[sectionIndex];
//$$         BlockState previous = section.setBlockState(localX, localY, localZ, state, false);
//$$         for (Heightmap heightmap : heightmaps.values()) heightmap.update(localX, pos.getY(), localZ, state);
//$$         if (skyLightSources != null) skyLightSources.update(chunk, localX, pos.getY(), localZ);
//$$         markUnsaved();
//$$         return previous;
//$$     }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.UndoRedoChunkAccessTarget")
public abstract class ChunkAccessMixin {}
//#endif