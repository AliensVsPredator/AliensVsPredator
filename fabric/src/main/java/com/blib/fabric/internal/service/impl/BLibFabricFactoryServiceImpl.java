package com.blib.fabric.internal.service.impl;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibFactoryService;

@ApiStatus.Internal
public class BLibFabricFactoryServiceImpl implements BLibFactoryService {

    @Override
    public <T> Registry<T> createCustomRegistry(BLibMod mod, ResourceKey<Registry<T>> registryResourceKey, boolean shouldSync) {
        var builder = FabricRegistryBuilder.createSimple(registryResourceKey);

        if (shouldSync) {
            builder.attribute(RegistryAttribute.SYNCED);
        }

        return builder.buildAndRegister();
    }

    @Override
    public <E extends Mob> Supplier<SpawnEggItem> createSpawnEggSupplier(
        Supplier<EntityType<E>> entityType,
        int primaryEggColor,
        int secondaryEggColor,
        Item.Properties itemProperties
    ) {
        return () -> new SpawnEggItem(entityType.get(), primaryEggColor, secondaryEggColor, itemProperties);
    }
}
