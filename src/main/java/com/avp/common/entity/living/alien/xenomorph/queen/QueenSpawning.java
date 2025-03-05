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
import com.avp.common.block.AVPBlockTags;
import com.avp.common.config.ConfigProperties;
import com.avp.common.util.AVPPredicates;

public class QueenSpawning {

    public static final SpawnPlacements.SpawnPredicate<Queen> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var properties = AVP.SPAWNING_CONFIG.properties();
        var maxY = properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.mobSpawning().maxY());
        var requiresResin = properties.getOrThrow(ConfigProperties.QUEEN_SPAWNING.requiresResin());
        var isValidSpawn = !requiresResin || serverLevelAccessor.getBlockState(blockPos.below()).is(AVPBlockTags.RESIN);

        return blockPos.getY() <= maxY &&
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
        var properties = AVP.HIVES_CONFIG.properties();
        var minimumDistanceBetweenHivesInBlocks = properties.getOrThrow(ConfigProperties.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS);

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
        var allQueens = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Queen.class), AVPPredicates.alwaysTrue());
        var requiredDistanceSquared = requiredDistanceInBlocks * requiredDistanceInBlocks;
        return allQueens.stream()
            .anyMatch(queen -> queen.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < requiredDistanceSquared);
    }
}
