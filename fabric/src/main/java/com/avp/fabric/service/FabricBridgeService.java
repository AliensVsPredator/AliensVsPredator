package com.avp.fabric.service;

import com.avp.service.BridgeService;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class FabricBridgeService implements BridgeService {

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColour,
        int secondaryEggColour,
        Item.Properties itemProperties
    ) {
        return () -> new SpawnEggItem(entityType.get(), primaryEggColour, secondaryEggColour, itemProperties);
    }
}
