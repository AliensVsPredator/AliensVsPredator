package com.blib.neoforge.service.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

import com.blib.internal.service.BLibFactoryService;

public class NeoForgeBLibFactoryServiceImpl implements BLibFactoryService {

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColor,
        int secondaryEggColor,
        Item.Properties itemProperties
    ) {
        return () -> new DeferredSpawnEggItem(entityType, primaryEggColor, secondaryEggColor, itemProperties);
    }
}
