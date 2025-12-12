package com.blib.common.registry.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.BLibRegistry;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.internal.service.BLibInternalServices;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class BLibEntityTypeRegistry extends BLibRegistry<EntityType<?>> {

    public BLibEntityTypeRegistry(BLibMod mod) {
        super(mod, BuiltInRegistries.ENTITY_TYPE);
    }

    public void registerAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        BLibInternalServices.REGISTRY.registerEntityAttributes(holder, attributeSupplierBuilderSupplier);
    }

    public <T extends Mob> void registerSpawnData(BLibEntitySpawnData<T> spawnData) {
        BLibInternalServices.REGISTRY.registerEntitySpawnData(spawnData);
    }
}
