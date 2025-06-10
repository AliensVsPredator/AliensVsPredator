package com.human.common.gameplay.entity.living.human.marine;

import net.minecraft.world.entity.SpawnPlacements;

import com.avp.common.registry.tag.AVPBlockTags;

public class MarineSpawning {

    public static final SpawnPlacements.SpawnPredicate<Marine> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var belowState = serverLevelAccessor.getBlockState(blockPos.below());
        var spawnableBlock = AVPBlockTags.MARINE_SPAWN_BLOCKS;

        return belowState.is(spawnableBlock);
    };
}
