package org.avp.neoforge.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.Holder;

public class NeoForgeEntityHolder<T extends Entity> extends Holder<EntityType<T>> {

    public NeoForgeEntityHolder(DeferredRegister<EntityType<?>> deferredRegister, String registryName, Supplier<EntityType<T>> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
