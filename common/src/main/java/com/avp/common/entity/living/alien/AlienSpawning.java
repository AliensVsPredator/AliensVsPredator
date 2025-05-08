package com.avp.common.entity.living.alien;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.config.AVPConfig;
import com.avp.common.entity.AVPEntityTypeTags;

public class AlienSpawning {

    public static <T extends Alien> SpawnPlacements.SpawnPredicate<T> createPredicate(
        AVPConfig.SpawnConfigs.SpawnSettings container
    ) {
        return (
            entityType,
            serverLevelAccessor,
            mobSpawnType,
            blockPos,
            randomSource
        ) -> {
            var belowState = serverLevelAccessor.getBlockState(blockPos.below());
            var resinBlock = entityType.is(AVPEntityTypeTags.NETHER_ALIENS) ? AVPBlockTags.NETHER_RESIN : AVPBlockTags.NORMAL_RESIN;
            var isValidResinPos = belowState.is(resinBlock);

            return isValidResinPos
                && checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
        };
    }

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
        );
    }
}
