package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.avp.api.GameObject;
import org.avp.common.service.EntityTypeService;

public class FabricEntityTypeService implements EntityTypeService {

    @Override
    public <T extends Entity> GameObject<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier) {
        return new GameObject<>(registryName, supplier);
    }

    @Override
    public void register(GameObject<EntityType<?>> holder) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, holder.getResourceLocation(), holder.get());
    }

    @Override
    public SpawnEggItem createSpawnEggItem(GameObject<? extends EntityType<? extends Mob>> holder, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new SpawnEggItem(holder.get(), backgroundColor, highlightColor, properties);
    }
}
