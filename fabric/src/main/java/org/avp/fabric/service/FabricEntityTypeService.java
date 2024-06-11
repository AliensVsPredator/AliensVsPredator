package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.EntityTypeService;
import org.avp.common.AVPResources;
import org.avp.common.registry.holder.AVPHolder;

import java.util.function.Supplier;

public class FabricEntityTypeService implements EntityTypeService {

    @Override
    public <T extends Entity> BLHolder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register(BLHolder<EntityType<?>> holder) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, holder.getResourceLocation(), holder.get());
    }

    @Override
    public SpawnEggItem createSpawnEggItem(
        BLHolder<? extends EntityType<? extends Mob>> holder,
        int backgroundColor,
        int highlightColor,
        Item.Properties properties
    ) {
        return new SpawnEggItem(holder.get(), backgroundColor, highlightColor, properties);
    }
}
