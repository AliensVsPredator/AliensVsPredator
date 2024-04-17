package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.item.AVPSpawnEggItems;
import org.avp.common.service.EntityTypeService;
import org.avp.common.service.Services;
import org.avp.neoforge.util.NeoForgeEntityGameObject;

public class NeoForgeEntityTypeService implements EntityTypeService {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
        BuiltInRegistries.ENTITY_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends Entity> GameObject<EntityType<T>> createHolder(
        String registryName,
        Supplier<EntityType<T>> supplier
    ) {
        return new NeoForgeEntityGameObject<>(ENTITY_TYPES, registryName, supplier);
    }

    @Override
    public void register(GameObject<EntityType<?>> holder) { /* NO-OP FOR FORGE */ }

    @Override
    public SpawnEggItem createSpawnEggItem(GameObject<? extends EntityType<? extends Mob>> holder, int backgroundColor, int highlightColor, Item.Properties properties) {
        return new DeferredSpawnEggItem(holder::get, backgroundColor, highlightColor, properties);
    }
}
