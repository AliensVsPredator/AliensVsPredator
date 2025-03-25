package com.avp.common.entity.living.yautja;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;

import com.avp.AVP;

public class YautjaSpawning {

    public static final SpawnPlacements.SpawnPredicate<Yautja> PREDICATE = (
        entityType,
        serverLevelAccessor,
        mobSpawnType,
        blockPos,
        randomSource
    ) -> {
        var maxY = AVP.config.spawnConfigs.YAUTJA_SPAWN.maxY;
        var minY = AVP.config.spawnConfigs.YAUTJA_SPAWN.minY;

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
        return serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL
                && Mob.checkMobSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }
}
