package org.avp.common.service;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.avp.api.GameObject;

import java.util.function.Supplier;

public interface EntityTypeService {

    <T extends Entity> GameObject<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier);

    void register(GameObject<EntityType<?>> holder);

    SpawnEggItem createSpawnEggItem(GameObject<? extends EntityType<? extends Mob>> holder, int backgroundColor, int highlightColor, Item.Properties properties);
}
