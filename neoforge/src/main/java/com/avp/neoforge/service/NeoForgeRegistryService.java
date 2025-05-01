package com.avp.neoforge.service;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.service.RegistryService;

public class NeoForgeRegistryService implements RegistryService {

    private final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, AVP.MOD_ID);

    private final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_REGISTRY = DeferredRegister.create(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        AVP.MOD_ID
    );

    private final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, AVP.MOD_ID);

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        if (registry == BuiltInRegistries.BLOCK) {
            return (Supplier<T>) BLOCK_REGISTRY.register(id, (Supplier<Block>) supplier);
        } else if (registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            return (Supplier<T>) DATA_COMPONENT_REGISTRY.register(id, (Supplier<DataComponentType<?>>) supplier);
        } else if (registry == BuiltInRegistries.ITEM) {
            return (Supplier<T>) ITEM_REGISTRY.register(id, (Supplier<Item>) supplier);
        }

        throw new IllegalArgumentException("Received registration attempt for an unhandled registry. Registry: " + registry);
    }

    public void initialize(IEventBus modBus) {
        BLOCK_REGISTRY.register(modBus);
        DATA_COMPONENT_REGISTRY.register(modBus);
        ITEM_REGISTRY.register(modBus);
    }
}
