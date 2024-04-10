package org.avp.neoforge.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

import org.avp.common.entity.spawn.AVPEntitySpawns;

public class AVPNeoForgeEntitySpawns {

    @SuppressWarnings("unchecked")
    public static void handleSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event) {
        AVPEntitySpawns.getEntries().forEach(entitySpawnData -> {
            var entityType = (EntityType<Mob>) entitySpawnData.entityTypeGameObject().get();
            var placementType = entitySpawnData.spawnPlacementType();
            var heightMapType = entitySpawnData.heightMapType();
            var predicate = (SpawnPlacements.SpawnPredicate<Mob>) entitySpawnData.spawnPredicate();
            event.register(entityType, placementType, heightMapType, predicate, SpawnPlacementRegisterEvent.Operation.AND);
        });
    }

    private AVPNeoForgeEntitySpawns() {
        throw new UnsupportedOperationException();
    }
}
