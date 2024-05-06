package org.avp.common.entity.spawn;

import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.config.AVPConfig;
import org.avp.common.entity.living.Yautja;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;
import org.avp.common.entity.type.AVPYautjaEntityTypes;
import org.avp.common.registry.AVPDeferredRegistry;

public class AVPEntitySpawns extends AVPDeferredRegistry<EntitySpawnData<?>> {

    public static final AVPEntitySpawns INSTANCE = new AVPEntitySpawns();

    private <T extends Monster> void registerMonsterSpawn(
        Holder<EntityType<T>> entityTypeHolder,
        TagKey<Biome> biomeTagKey,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate,
        int weight,
        int minGroupSize,
        int maxGroupSize
    ) {
        entries.add(
            new Holder<>(
                entityTypeHolder.getResourceLocation().getPath() + "_spawns",
                () -> new EntitySpawnData<>(
                    entityTypeHolder,
                    biomeTagKey,
                    weight,
                    minGroupSize,
                    maxGroupSize,
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    spawnPredicate
                )
            )
        );
    }

    private <T extends Monster> void registerAlienSpawn(
        Holder<EntityType<T>> entityTypeHolder,
        SpawnPlacements.SpawnPredicate<T> spawnPredicate,
        int maxYLevel,
        int weight,
        int minGroupSize,
        int maxGroupSize
    ) {
        registerMonsterSpawn(
            entityTypeHolder,
            BiomeTags.IS_OVERWORLD,
            (e, s, m, b, r) -> b.getY() <= maxYLevel && spawnPredicate.test(e, s, m, b, r),
            weight,
            minGroupSize,
            maxGroupSize
        );
    }

    @Override
    protected Holder<EntitySpawnData<?>> createHolder(String registryName, Supplier<EntitySpawnData<?>> supplier) {
        return null;
    }

    @Override
    public void register() {
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.boiler,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_BOILER_SPAWNS,
            7,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.drone,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_DRONE_SPAWNS,
            50,
            1,
            2
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.praetorian,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_PRAETORIAN_SPAWNS,
            10,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.queen,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_QUEEN_SPAWNS,
            5,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.spitter,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_SPITTER_SPAWNS,
            10,
            1,
            1
        );
        registerAlienSpawn(
            AVPBaseAlienEntityTypes.INSTANCE.warrior,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_WARRIOR_SPAWNS,
            25,
            1,
            1
        );

        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.INSTANCE.droneRunner,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_DRONE_RUNNER_SPAWNS,
            50,
            1,
            3
        );
        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_WARRIOR_RUNNER_SPAWNS,
            25,
            1,
            2
        );
        registerAlienSpawn(
            AVPRunnerAlienEntityTypes.INSTANCE.crusher,
            Monster::checkMonsterSpawnRules,
            AVPConfig.Aliens.MAX_Y_LEVEL_FOR_CRUSHER_SPAWNS,
            10,
            1,
            1
        );

        registerMonsterSpawn(
            AVPYautjaEntityTypes.INSTANCE.YAUTJA, BiomeTags.IS_JUNGLE, Yautja::checkPredatorSpawnRules, 30, 1, 1
        );
    }

    private AVPEntitySpawns() {}
}
