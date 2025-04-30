package com.avp.service;

import net.minecraft.core.Registry;

import java.util.function.Supplier;

public interface RegistryService {

    <T> Supplier<T> register(Registry<T> registry, String id, Supplier<T> supplier);
}
