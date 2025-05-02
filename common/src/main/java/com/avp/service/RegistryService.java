package com.avp.service;

import net.minecraft.core.Registry;

import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;

public interface RegistryService {

    <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier);
}
