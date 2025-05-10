package com.avp.common.entity.living.alien.xenomorph.queen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import com.avp.AVP;
import com.avp.common.level.saveddata.HiveLevelData;
import com.avp.common.level.saveddata.QueenSpawnChunkData;
import com.avp.common.util.ChunkPosUtil;

public class QueenSpawning {

    private static final int MAX_Y_LEVEL = -24;

    public static final SpawnPlacements.SpawnPredicate<Queen> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var queenSpawnChunkDataOption = QueenSpawnChunkData.getOrCreate(serverLevelAccessor.getLevel());
        var isChunkSpawnAvailable = queenSpawnChunkDataOption
            .isSomeAnd(queenSpawnChunkData -> !queenSpawnChunkData.isChunkBlacklisted(blockPos));

        var canSpawn = blockPos.getY() <= MAX_Y_LEVEL
            && isChunkSpawnAvailable
            && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);

        if (canSpawn) {
            var queenSpawnChunkData = queenSpawnChunkDataOption.unwrap();
            // TODO: Refactor this distance value at a later date, all hive distance checks are begging for a refactor.
            var chunkRadiusToBlacklist = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS / 16;
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
        return Monster.checkMonsterSpawnRules(
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        )
            && HiveLevelData.getOrCreate(serverLevelAccessor.getLevel())
                .andThen(hiveLevelData -> hiveLevelData.findNearestHive(blockPos))
                .match(
                    // If there is hive, we need to make sure it's far enough away from where the queen wants to spawn.
                    nearestHive -> !nearestHive.isBlockPosWithinRangeOfHive(
                        blockPos,
                        AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS
                    ),
                    // No "nearest hive" present, so the queen is clear to spawn.
                    () -> true
                );
    }
}
