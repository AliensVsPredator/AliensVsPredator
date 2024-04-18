package org.avp.neoforge.util;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.Holder;

public class NeoForgeHolder<T> extends Holder<T> {

    public NeoForgeHolder(DeferredRegister<T> deferredRegister, String registryName, Supplier<T> supplier) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
