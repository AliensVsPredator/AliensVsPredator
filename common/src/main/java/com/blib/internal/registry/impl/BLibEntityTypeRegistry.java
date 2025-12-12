package com.blib.internal.registry.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.BLibRegistry;
import com.blib.internal.service.BLibInternalServices;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class BLibEntityTypeRegistry extends BLibRegistry<EntityType<?>> {

    public BLibEntityTypeRegistry(BLibMod mod) {
        super(mod, BuiltInRegistries.ENTITY_TYPE);
    }

    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        BLibInternalServices.REGISTRY.registerEntityAttributes(holder, attributeSupplierBuilderSupplier);
    }
}
