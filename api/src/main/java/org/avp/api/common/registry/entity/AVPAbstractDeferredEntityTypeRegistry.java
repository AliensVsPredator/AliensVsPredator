package org.avp.api.common.registry.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;

public abstract class AVPAbstractDeferredEntityTypeRegistry {

    protected final List<BLHolder<? extends EntityType<?>>> entries;

    protected AVPAbstractDeferredEntityTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends Entity> BLHolder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier);

    public abstract void register();

    public List<BLHolder<? extends EntityType<?>>> getEntries() {
        return entries;
    }
}
