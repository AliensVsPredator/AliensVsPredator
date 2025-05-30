package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import com.alien.common.gameplay.level.saveddata.QueenSpawnChunkData;
import com.lib.common.gameplay.util.spatial.chunk.ChunkPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Objects;

import com.avp.AVP;

public class QueenSpawning {

    private static final int MAX_OVERWORLD_Y_LEVEL = -24;

    public static final SpawnPlacements.SpawnPredicate<Queen> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var level = serverLevelAccessor.getLevel();
        var queenSpawnChunkDataOption = QueenSpawnChunkData.getOrCreate(level);
        var isChunkSpawnAvailable = queenSpawnChunkDataOption
            .isSomeAnd(queenSpawnChunkData -> !queenSpawnChunkData.isChunkBlacklisted(blockPos));

        int maxYLevelForDimension;

        if (level.dimension() == Level.NETHER) {
            maxYLevelForDimension = level.dimensionType().logicalHeight();
        } else {
            maxYLevelForDimension = MAX_OVERWORLD_Y_LEVEL;
        }

        var canSpawn = blockPos.getY() <= maxYLevelForDimension
            && isChunkSpawnAvailable
            && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);

        if (canSpawn) {
            var queenSpawnChunkData = queenSpawnChunkDataOption.unwrap();
            var chunkRadiusToBlacklist = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_NATURAL_QUEEN_SPAWNS_IN_CHUNKS;
            var nearbyChunkPositions = ChunkPosUtil.getChunksAround(blockPos, chunkRadiusToBlacklist);

            nearbyChunkPositions.forEach(queenSpawnChunkData::addChunkToBlacklist);
        }

        // NOTE: All spawn checks for the queen should go in the check above, as we need to be certain the queen will
        // be able to spawn in order to correctly blacklist chunks from having future queen spawns.
        return canSpawn;
    };

    public static boolean checkSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        var alienVariantTypeOption = AlienVariantTypes.getFor(entityType);

        return Monster.checkMonsterSpawnRules(
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        )
            && HiveLevelData.getOrCreate(serverLevelAccessor.getLevel())
                .andThen(
                    hiveLevelData -> hiveLevelData.findNearestHive(
                        blockPos,
                        // Find the nearest hive for this alien type's variant type.
                        hive -> alienVariantTypeOption.isSomeAnd(
                            alienVariantType -> Objects.equals(hive.getVariant(), alienVariantType.variant())
                        )
                    )
                )
                .match(
                    // If there is hive, we need to make sure it's far enough away from where the queen wants to spawn.
                    nearestHive -> !nearestHive.getSpaceManager().isBlockPosWithinHiveBuffer(blockPos),
                    // No "nearest hive" present, so the queen is clear to spawn.
                    () -> true
                );
    }
}
