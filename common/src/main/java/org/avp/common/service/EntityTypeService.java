package org.avp.common.service;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface EntityTypeService {

    <T extends Entity> Holder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier);

    void register(Holder<EntityType<?>> holder);

    SpawnEggItem createSpawnEggItem(
        Holder<? extends EntityType<? extends Mob>> holder,
        int backgroundColor,
        int highlightColor,
        Item.Properties properties
    );
}
