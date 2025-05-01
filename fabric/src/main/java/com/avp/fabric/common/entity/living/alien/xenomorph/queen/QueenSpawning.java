package com.avp.fabric.common.entity.living.alien.xenomorph.queen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;

import com.avp.AVP;
import com.avp.common.block.AVPBlockTags;
import com.avp.common.util.TempAVPPredicates;

public class QueenSpawning {

    private static final int MAX_Y_LEVEL = -24;

    public static final SpawnPlacements.SpawnPredicate<Queen> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var requiresResin = AVP.config.spawnConfigs.QUEEN_SPAWN.requiresResin;
        var isValidSpawn = !requiresResin || serverLevelAccessor.getBlockState(blockPos.below()).is(AVPBlockTags.RESIN);

        return blockPos.getY() <= MAX_Y_LEVEL &&
            isValidSpawn &&
            checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    };

    public static final SpawnPlacements.SpawnPredicate<Queen> NETHER_PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var requiresResin = AVP.config.spawnConfigs.NETHER_QUEEN_SPAWN.requiresResin;
        var isValidSpawn = !requiresResin || serverLevelAccessor.getBlockState(blockPos.below()).is(AVPBlockTags.RESIN);

        return blockPos.getY() <= MAX_Y_LEVEL &&
            isValidSpawn &&
            checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
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
            !anyNearbyQueens(serverLevelAccessor, blockPos, minimumDistanceBetweenHivesInBlocks);
    }

    public static boolean anyNearbyQueens(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, int requiredDistanceInBlocks) {
        var allQueens = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Queen.class), TempAVPPredicates.alwaysTrue());
        var requiredDistanceSquared = requiredDistanceInBlocks * requiredDistanceInBlocks;
        return allQueens.stream()
            .anyMatch(queen -> queen.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < requiredDistanceSquared);
    }
}
