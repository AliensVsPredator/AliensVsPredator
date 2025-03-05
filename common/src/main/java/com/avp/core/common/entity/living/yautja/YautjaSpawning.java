package com.avp.core.common.entity.living.yautja;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import com.avp.core.AVP;
import com.avp.core.common.config.ConfigProperties;

public class YautjaSpawning {

    public static final SpawnPlacements.SpawnPredicate<Yautja> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var properties = AVP.SPAWNING_CONFIG.properties();
        var maxY = properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.maxY());
        var minY = properties.getOrThrow(ConfigProperties.YAUTJA_SPAWNING.minY());

        return blockPos.getY() <= maxY &&
            blockPos.getY() >= minY &&
            checkSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
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
        );
    }
}
