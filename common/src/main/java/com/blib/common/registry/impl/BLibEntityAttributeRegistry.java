package com.blib.common.registry.impl;

import com.blib.BLibMod;
import com.blib.common.exception.BLibRegistrationException;
import com.blib.common.mod.BLibModState;
import com.blib.common.registry.BLibHolder;
import com.blib.internal.service.BLibInternalServices;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class BLibEntityAttributeRegistry {

    private final BLibMod mod;

    public BLibEntityAttributeRegistry(BLibMod mod) {
        this.mod = mod;
    }

    public void register(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register entity attributes outside of mod's initialization window. Mod State: %s".formatted(mod.state())
            );
        }

        BLibInternalServices.REGISTRY.registerEntityAttributes(holder, attributeSupplierBuilderSupplier);
    }
}
