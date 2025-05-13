package com.avp.common.entity.living.alien;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Objects;

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
        var alienVariantTypeOption = AlienVariantTypes.getFor(entityType)
            .map(AlienVariantType::resinBlockTag);

        var isValidResinPos = alienVariantTypeOption.isSomeAnd(belowState::is);

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
            isSpawnPositionWithinHive(entityType, serverLevelAccessor, blockPos);
    }

    private static boolean isSpawnPositionWithinHive(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        BlockPos blockPos
    ) {
        var alienVariantTypeOption = AlienVariantTypes.getFor(entityType);

        return HiveLevelData.getOrCreate(serverLevelAccessor.getLevel())
            .andThen(
                hiveLevelData -> hiveLevelData.findNearestHive(
                    blockPos,
                    // Find the nearest hive for this alien type's variant type.
                    hive -> alienVariantTypeOption.isSomeAnd(
                        alienVariantType -> Objects.equals(hive.getVariant(), alienVariantType.variant())
                    )
                )
            )
            .isSomeAnd(nearestHive ->
            // Aliens can not spawn in hives that are dead.
            nearestHive.isAlive()
                // AND Hive is not angry/aggro'd.
                && !nearestHive.isAngry()
                // AND spawn position must be within range of the hive.
                && nearestHive.getSpaceManager().isBlockPosWithinHive(blockPos)
            );
    }
}
