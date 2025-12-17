package com.blib.internal.service;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.BLibMod;

@ApiStatus.Internal
public interface BLibFactoryService {

    <T> Registry<T> createCustomRegistry(BLibMod mod, ResourceKey<Registry<T>> registryResourceKey, boolean shouldSync);

    <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColor,
        int secondaryEggColor,
        Item.Properties itemProperties
    );
}
