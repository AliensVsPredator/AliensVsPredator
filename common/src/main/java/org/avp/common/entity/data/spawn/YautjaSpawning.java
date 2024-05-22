package org.avp.common.entity.data.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.avp.common.entity.living.Yautja;
import org.avp.common.util.AVPPredicates;

public class YautjaSpawning {

    private static final int SPAWN_MIN_Y_LEVEL = 60;
    private static final int SPAWN_MIN_DISTANCE_IN_BLOCKS = 256;
    private static final int SPAWN_DISTANCE_SQUARED = SPAWN_MIN_DISTANCE_IN_BLOCKS * SPAWN_MIN_DISTANCE_IN_BLOCKS;

    public static boolean anyNearbyPredators(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos) {
        var allYautja = serverLevelAccessor.getLevel().getEntities(EntityTypeTest.forClass(Yautja.class), AVPPredicates.ALWAYS_TRUE);
        return allYautja.stream().anyMatch(yautja -> yautja.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < SPAWN_DISTANCE_SQUARED);
    }

    public static boolean checkPredatorSpawnRules(
        EntityType<? extends Monster> entityType,
        ServerLevelAccessor serverLevelAccessor,
        MobSpawnType mobSpawnType,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        return blockPos.getY() > SPAWN_MIN_Y_LEVEL &&
            Monster.checkAnyLightMonsterSpawnRules(
                entityType,
                serverLevelAccessor,
                mobSpawnType,
                blockPos,
                randomSource
            ) &&
            !anyNearbyPredators(serverLevelAccessor, blockPos);
    }

    private YautjaSpawning() {
        throw new UnsupportedOperationException();
    }
}
