package org.avp.neoforge.util;

import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.common.registry.holder.AVPHolder;

import java.util.function.Supplier;

public class NeoForgeHolder<T> extends AVPHolder<T> {

    public NeoForgeHolder(DeferredRegister<T> deferredRegister, String registryName, Supplier<T> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
