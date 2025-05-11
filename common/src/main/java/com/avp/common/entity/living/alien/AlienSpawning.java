package com.avp.common.entity.living.alien;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import com.avp.common.entity.living.alien.util.AlienVariantUtil;
import com.avp.common.level.saveddata.HiveLevelData;

public class AlienSpawning {

    @SuppressWarnings("unchecked")
    public static <T extends Alien> SpawnPlacements.SpawnPredicate<T> getTypedPredicate() {
        return (SpawnPlacements.SpawnPredicate<T>) PREDICATE;
    }

    private static final SpawnPlacements.SpawnPredicate<Alien> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var belowState = serverLevelAccessor.getBlockState(blockPos.below());
        var resinTag = AlienVariantUtil.getResinTagFor(entityType);
        var isValidResinPos = belowState.is(resinTag);

        return isValidResinPos
            && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
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
        ) &&
            isSpawnPositionWithinHive(serverLevelAccessor, blockPos);
    }

    // TODO:
    // We need to check that the entity type we're trying to spawn isn't going to get immediately clobbered by an
    // enemy strain hive.
    private static boolean isSpawnPositionWithinHive(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos) {
        return HiveLevelData.getOrCreate(serverLevelAccessor.getLevel())
            .andThen(hiveLevelData -> hiveLevelData.findNearestHive(blockPos))
            .isSomeAnd(nearestHive ->
            // Aliens can not spawn in hives that are dead.
            nearestHive.isAlive()
                // AND Hive is not angry/aggro'd.
                && !nearestHive.isAngry()
                // AND spawn position must be within range of the hive.
                && nearestHive.isBlockPosWithinRangeOfHive(blockPos)
            );
    }
}
