package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.avp.api.Holder;
import org.avp.common.service.EntityTypeService;

public class FabricEntityTypeService implements EntityTypeService {

    @Override
    public <T extends Entity> Holder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register(Holder<EntityType<?>> holder) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, holder.getResourceLocation(), holder.get());
    }

    @Override
    public SpawnEggItem createSpawnEggItem(Holder<? extends EntityType<? extends Mob>> holder, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new SpawnEggItem(holder.get(), backgroundColor, highlightColor, properties);
    }
}
