package org.avp.common.entity.spawn;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import org.avp.api.GameObject;
import org.avp.common.entity.AVPBaseAlienEntityTypes;

import java.util.ArrayList;
import java.util.List;

public class AVPEntitySpawns {

    private static final List<EntitySpawnData<?>> ENTRIES = new ArrayList<>();

    public static List<EntitySpawnData<?>> getEntries() {
        return ENTRIES;
    }

    private static <T extends Monster> void registerMonsterSpawn(GameObject<EntityType<T>> entityTypeGameObject) {
        ENTRIES.add(new EntitySpawnData<>(
            entityTypeGameObject, SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules
        ));
    }

    static {
        registerMonsterSpawn(AVPBaseAlienEntityTypes.BOILER);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.DRONE);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.PRAETORIAN);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.QUEEN);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.SPITTER);
        registerMonsterSpawn(AVPBaseAlienEntityTypes.WARRIOR);
    }

    private AVPEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
