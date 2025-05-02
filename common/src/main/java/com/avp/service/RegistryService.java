package com.avp.service;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public interface RegistryService {

    <T> Supplier<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier);

    <T> Holder<T> registerHolder(Registry<? super T> registry, String id, Supplier<? extends T> supplier);
}
