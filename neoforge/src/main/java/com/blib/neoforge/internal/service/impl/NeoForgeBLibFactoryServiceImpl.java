package com.blib.neoforge.internal.service.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.internal.service.BLibFactoryService;

@ApiStatus.Internal
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
