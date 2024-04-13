package org.avp.common.entity.spawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
import org.avp.common.config.AVPConfig;
import org.avp.common.entity.AVPBaseAlienEntityTypes;
import org.avp.common.entity.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.AVPYautjaEntityTypes;

public class AVPEntitySpawns {

    private static final List<EntitySpawnData<?>> ENTRIES = new ArrayList<>();

    public static List<EntitySpawnData<?>> getEntries() {
        return ENTRIES;
    }

    public static void forceInitialization() {
        // This function does not need to do anything.
    }

    private static <T extends Monster> void registerMonsterSpawn(
        GameObject<EntityType<T>> entityTypeGameObject,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate,
        int weight,
        int minGroupSize,
        int maxGroupSize
    ) {
        ENTRIES.add(
            new EntitySpawnData<>(
                entityTypeGameObject,
                weight,
                minGroupSize,
                maxGroupSize,
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                spawnPredicate
            )
        );
    }

    private static <T extends Monster> void registerAlienSpawn(
        GameObject<EntityType<T>> entityTypeGameObject,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate,
        int maxYLevel,
        int weight,
        int minGroupSize,
        int maxGroupSize
    ) {
        registerMonsterSpawn(
            entityTypeGameObject,
            (e, s, m, b, r) -> b.getY() <= maxYLevel && spawnPredicate.test(e, s, m, b, r),
            weight,
            minGroupSize,
            maxGroupSize
        );
    }

    static {
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.BOILER,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_BOILER_SPAWNS,
            7,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.DRONE,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_DRONE_SPAWNS,
            50,
            1,
            2
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.PRAETORIAN,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_PRAETORIAN_SPAWNS,
            10,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.QUEEN,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_QUEEN_SPAWNS,
            5,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.SPITTER,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_SPITTER_SPAWNS,
            10,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.WARRIOR,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_WARRIOR_SPAWNS,
            25,
            1,
            1
        );

        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.DRONE_RUNNER,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_DRONE_RUNNER_SPAWNS,
            50,
            1,
            3
        );
        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.WARRIOR_RUNNER,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_WARRIOR_RUNNER_SPAWNS,
            25,
            1,
            2
        );
        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.CRUSHER,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_CRUSHER_SPAWNS,
            10,
            1,
            1
        );

        registerMonsterSpawn(AVPYautjaEntityTypes.YAUTJA, Monster::checkMonsterSpawnRules, 1, 1, 1);
    }

    private AVPEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
