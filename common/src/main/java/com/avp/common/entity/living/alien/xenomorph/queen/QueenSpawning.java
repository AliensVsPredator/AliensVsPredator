package com.avp.common.entity.living.alien.xenomorph.queen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;

import com.avp.AVP;
import com.avp.common.level.saveddata.QueenSpawnChunkData;
import com.avp.common.util.AVPPredicates;
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
            // TODO: Replace this with a dedicated config value in terms of chunks.
            var chunkRadiusToBlacklist = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS / 16;
            var nearbyChunkPositions = ChunkPosUtil.getChunksAround(blockPos, chunkRadiusToBlacklist);

            nearbyChunkPositions.forEach(queenSpawnChunkData::addChunkToBlacklist);
        }

        return canSpawn;
    };

    public static boolean checkSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        var minimumDistanceBetweenHivesInBlocks = AVP.config.hiveConfigs.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS;

        return Monster.checkMonsterSpawnRules(
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        ) &&
        // FIXME: Check for nearby hives instead of nearby queens.
            !anyNearbyQueens(serverLevelAccessor, blockPos, minimumDistanceBetweenHivesInBlocks);
    }

    public static boolean anyNearbyQueens(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, int requiredDistanceInBlocks) {
        var allQueens = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Queen.class), AVPPredicates.alwaysTrue());
        var requiredDistanceSquared = requiredDistanceInBlocks * requiredDistanceInBlocks;
        return allQueens.stream()
            .anyMatch(queen -> queen.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < requiredDistanceSquared);
    }
}
