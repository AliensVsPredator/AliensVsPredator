package com.avp.neoforge.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, AVP.MOD_ID);

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(Registry<T> registry, String id, Supplier<T> supplier) {
        if (registry == BuiltInRegistries.ITEM) {
            return (Supplier<T>) ITEM_REGISTRY.register(id, (Supplier<Item>) supplier);
        }

        throw new IllegalArgumentException("Received registration attempt for an unhandled registry. Registry: " + registry);
    }

    public void initialize(IEventBus modBus) {
        ITEM_REGISTRY.register(modBus);
    }
}
