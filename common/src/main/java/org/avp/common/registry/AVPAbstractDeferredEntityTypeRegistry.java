package org.avp.common.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.avp.api.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AVPAbstractDeferredEntityTypeRegistry {

    protected final List<Holder<? extends EntityType<?>>> entries;

    protected AVPAbstractDeferredEntityTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends Entity> Holder<EntityType<T>> createHolder(String registryName, Supplier<EntityType<T>> supplier);

    public abstract void register();

    public List<Holder<? extends EntityType<?>>> getEntries() {
        return entries;
    }
}
