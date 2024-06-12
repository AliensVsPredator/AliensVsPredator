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

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.EntityTypeService;
import org.avp.common.AVPConstants;
import org.avp.neoforge.util.NeoForgeEntityHolder;

public class NeoForgeEntityTypeService implements EntityTypeService {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
        BuiltInRegistries.ENTITY_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends Entity> BLHolder<EntityType<T>> createHolder(
        String registryName,
        Supplier<EntityType<T>> supplier
    ) {
        return new NeoForgeEntityHolder<>(ENTITY_TYPES, registryName, supplier);
    }

    @Override
    public void register(BLHolder<EntityType<?>> holder) { /* NO-OP FOR FORGE */ }

    @Override
    public SpawnEggItem createSpawnEggItem(
        BLHolder<? extends EntityType<? extends Mob>> holder,
        int backgroundColor,
        int highlightColor,
        Item.Properties properties
    ) {
        return new DeferredSpawnEggItem(holder::get, backgroundColor, highlightColor, properties);
    }
}
