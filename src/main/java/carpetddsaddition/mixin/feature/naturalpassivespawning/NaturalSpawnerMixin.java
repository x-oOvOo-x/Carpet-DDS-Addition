/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition. If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.mixin.feature.naturalpassivespawning;

import carpetddsaddition.feature.naturalpassivespawning.NaturalPassiveSpawning;
import carpetddsaddition.feature.naturalpassivespawning.compat.NaturalPassiveSpawningCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
//#if MC < 11601
import net.minecraft.world.level.biome.Biome;
//#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    //#if MC >= 11601
    //$$ @Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
    //$$ private static void carpetDDSAddition$skipPassiveCategory116(MobCategory category, ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnPredicate predicate, NaturalSpawner.AfterSpawnCallback callback, CallbackInfo ci) {
    //$$     if (NaturalPassiveSpawningCompat.shouldSkipCategory(category)) ci.cancel();
    //$$ }
    //#elseif MC >= 11500
    @Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
    private static void carpetDDSAddition$skipPassiveCategory115(MobCategory category, ServerLevel level, LevelChunk chunk, BlockPos pos, CallbackInfo ci) {
        if (NaturalPassiveSpawningCompat.shouldSkipCategory(category)) ci.cancel();
    }
    //#else
    //$$ @Inject(method = "spawnCategoryForChunk", at = @At("HEAD"), cancellable = true)
    //$$ private static void carpetDDSAddition$skipPassiveCategory114(MobCategory category, Level level, LevelChunk chunk, BlockPos pos, CallbackInfo ci) {
    //$$     if (NaturalPassiveSpawningCompat.shouldSkipCategory(category)) ci.cancel();
    //$$ }
    //#endif

    @Inject(method = "spawnMobsForChunkGeneration", at = @At("HEAD"), cancellable = true)
    private static void carpetDDSAddition$skipChunkGenerationCreatures(CallbackInfo ci) {
        if (!NaturalPassiveSpawning.land()) ci.cancel();
    }

    //#if MC >= 11601
    //$$ @Inject(method = "getMobForSpawn", at = @At("HEAD"), cancellable = true)
    //$$ private static void carpetDDSAddition$filterPassiveEntityType116(ServerLevel level, EntityType<?> type, CallbackInfoReturnable<Mob> cir) {
    //$$     if (!NaturalPassiveSpawningCompat.allowsEntityType(type)) cir.setReturnValue(null);
    //$$ }
    //#else
    @Inject(method = "getRandomSpawnMobAt", at = @At("RETURN"), cancellable = true)
    private static void carpetDDSAddition$filterPassiveEntityTypeLegacy(CallbackInfoReturnable<Biome.SpawnerData> cir) {
        Biome.SpawnerData data = cir.getReturnValue();
        if (data != null && !NaturalPassiveSpawningCompat.allowsEntityType(data.type)) cir.setReturnValue(null);
    }
    //#endif
}
