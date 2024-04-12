package org.avp.common.entity.spawn;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

import org.avp.api.GameObject;
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
                Monster::checkMonsterSpawnRules
            )
        );
    }

    static {
        registerMonsterSpawn(AVPBaseAlienEntityTypes.BOILER, 7, 1, 1);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.DRONE, 50, 1, 2);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.PRAETORIAN, 10, 1, 1);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.QUEEN, 5, 1, 1);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.SPITTER, 10, 1, 1);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.WARRIOR, 25, 1, 1);

        registerMonsterSpawn(AVPRunnerAlienEntityTypes.DRONE_RUNNER, 50, 1, 3);
        registerMonsterSpawn(AVPRunnerAlienEntityTypes.WARRIOR_RUNNER, 25, 1, 2);
        registerMonsterSpawn(AVPRunnerAlienEntityTypes.CRUSHER, 10, 1, 1);

        registerMonsterSpawn(AVPYautjaEntityTypes.YAUTJA, 1, 1, 1);
    }

    private AVPEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
