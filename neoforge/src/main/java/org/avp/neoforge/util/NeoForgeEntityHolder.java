package org.avp.neoforge.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.common.registry.holder.AVPHolder;

public class NeoForgeEntityHolder<T extends Entity> extends AVPHolder<EntityType<T>> {

    public NeoForgeEntityHolder(DeferredRegister<EntityType<?>> deferredRegister, String registryName, Supplier<EntityType<T>> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
