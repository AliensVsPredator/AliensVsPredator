package com.alien.common.gameplay.entity.living.alien.xenomorph.queen;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.level.saveddata.HiveLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Objects;

import com.avp.server.ServerLevelManagerAccessor;

public class QueenSpawning {

    private static final int MAX_OVERWORLD_Y_LEVEL = -24;

    public static final SpawnPlacements.SpawnPredicate<Queen> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var serverLevel = serverLevelAccessor.getLevel();
        var serverLevelManager = ((ServerLevelManagerAccessor) serverLevel).getServerLevelManager();

        if (serverLevelManager.getQueenSpawnCooldown().isActive()) {
            return false;
        }

        int maxYLevelForDimension;

        if (serverLevel.dimension() == Level.NETHER) {
            maxYLevelForDimension = serverLevel.dimensionType().logicalHeight();
        } else {
            maxYLevelForDimension = MAX_OVERWORLD_Y_LEVEL;
        }

        return blockPos.getY() <= maxYLevelForDimension
            && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
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
