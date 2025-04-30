package com.avp.fabric.common.entity.living.human.marine;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.fabric.AVPFabric;
import com.avp.common.block.AVPBlockTags;
import com.avp.fabric.common.config.AVPConfig;
import com.avp.fabric.common.entity.living.human.AbstractHuman;

public class MarineSpawning {

    public static final SpawnPlacements.SpawnPredicate<Marine> PREDICATE = createPredicate(
        AVPFabric.config.spawnConfigs.MARINE_SPAWN
    );

    public static <T extends AbstractHuman> SpawnPlacements.SpawnPredicate<T> createPredicate(
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
            var spawnableBlock = AVPBlockTags.MARINE_SPAWN_BLOCKS;
            var isValidPos = belowState.is(spawnableBlock);

            return isValidPos;
        };
    }
}
