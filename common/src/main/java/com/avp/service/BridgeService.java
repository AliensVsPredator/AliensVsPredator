package com.avp.service;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public interface BridgeService {

    <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(
            Supplier<EntityType<E>> entityType,
            int primaryEggColour,
            int secondaryEggColour,
            Item.Properties itemProperties
    );
}
