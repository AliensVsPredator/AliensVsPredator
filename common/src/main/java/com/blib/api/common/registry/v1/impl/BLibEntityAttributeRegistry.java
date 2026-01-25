package com.blib.api.common.registry.v1.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibEntityAttributeRegistry {

    private final BLibMod mod;

    @ApiStatus.Internal
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
