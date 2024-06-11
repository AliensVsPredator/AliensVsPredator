package org.avp.api.service;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;

public interface EntityTypeService {

    <T extends Entity> BLHolder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier);

    void register(BLHolder<EntityType<?>> holder);

    SpawnEggItem createSpawnEggItem(
        BLHolder<? extends EntityType<? extends Mob>> holder,
        int backgroundColor,
        int highlightColor,
        Item.Properties properties
    );
}
