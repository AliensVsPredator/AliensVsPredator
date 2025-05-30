package com.predator.common.gameplay.entity.living.yautja;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;

import com.avp.common.util.AVPPredicates;

public class YautjaSpawning {

    private static final int MIN_Y_LEVEL = 62;

    private static final int MINIMUM_DISTANCE_BETWEEN_YAUTJA_IN_BLOCKS = 32 * 16; // 32 chunks * 16 blocks each chunk.

    public static final SpawnPlacements.SpawnPredicate<Yautja> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> blockPos.getY() >= MIN_Y_LEVEL &&
        checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);

    public static boolean checkSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        return serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL
            && Mob.checkMobSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource)
            && !anyNearbyYautja(serverLevelAccessor, blockPos, MINIMUM_DISTANCE_BETWEEN_YAUTJA_IN_BLOCKS);
    }

    public static boolean anyNearbyYautja(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, int requiredDistanceInBlocks) {
        var allYautja = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Yautja.class), AVPPredicates.alwaysTrue());
        var requiredDistanceSquared = requiredDistanceInBlocks * requiredDistanceInBlocks;
        return allYautja.stream()
            .anyMatch(yautja -> yautja.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < requiredDistanceSquared);
    }
}
