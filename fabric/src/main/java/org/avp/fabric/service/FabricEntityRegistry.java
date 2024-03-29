package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

import org.avp.common.AVPResources;
import org.avp.common.service.EntityRegistry;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class FabricEntityRegistry implements EntityRegistry {

    @Override
    public <T extends Entity> GameObject<EntityType<T>> register(
        String registryName,
        Supplier<EntityType<T>> supplier
    ) {
        var gameObject = new GameObject<>(registryName, supplier);
        var resourceLocation = AVPResources.location(registryName);
        var entityType = gameObject.get();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return gameObject;
    }
}
