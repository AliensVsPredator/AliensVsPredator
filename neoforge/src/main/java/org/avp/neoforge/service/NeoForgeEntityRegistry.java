package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.item.AVPSpawnEggItems;
import org.avp.common.service.EntityRegistry;
import org.avp.common.service.Services;
import org.avp.neoforge.util.NeoForgeEntityGameObject;

public class NeoForgeEntityRegistry implements EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
        BuiltInRegistries.ENTITY_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends Entity> GameObject<EntityType<T>> register(
        String registryName,
        Supplier<EntityType<T>> supplier
    ) {
        return new NeoForgeEntityGameObject<>(ENTITY_TYPES, registryName, supplier);
    }

    @Override
    public <T extends Mob> GameObject<EntityType<T>> registerWithSpawnEgg(
        String registryName,
        int backgroundColor,
        int highlightColor,
        Supplier<EntityType<T>> supplier
    ) {
        var gameObject = register(registryName, supplier);
        var spawnEggGameObject = Services.ITEM_REGISTRY.register(
            "spawn_egg_" + registryName,
            () -> new DeferredSpawnEggItem(gameObject::get, backgroundColor, highlightColor, new Item.Properties())
        );
        AVPSpawnEggItems.ENTRIES.add(spawnEggGameObject);
        return gameObject;
    }
}
