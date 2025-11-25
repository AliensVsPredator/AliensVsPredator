package com.blib.neoforge.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.service.BLibRegistryService;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class NeoForgeBLibRegistryServiceImpl implements BLibRegistryService {

    private final Map<BLibMod, NeoForgeBLibModRegistryContainer> modIdToRegistryContainerMap;

    public NeoForgeBLibRegistryServiceImpl() {
        modIdToRegistryContainerMap = new ConcurrentHashMap<>();
    }

    public void finalize(BLibMod mod, IEventBus modBus) {
        getRegistryContainer(mod)
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(modBus));

        modBus.<EntityAttributeCreationEvent>addListener(event -> registerEntityAttributes(mod, event));
    }

    public void registerEntityAttributes(BLibMod mod, EntityAttributeCreationEvent event) {
        getRegistryContainer(mod).getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.v1().get(), pair.v2().get().build()));
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder) {
        var blibRegistry = holder.getRegistry();
        var modContainer = getRegistryContainer(blibRegistry.getMod());
        var backingRegistry = blibRegistry.getBackingRegistry();
        @SuppressWarnings("unchecked")
        var deferredRegister = (DeferredRegister<T>) modContainer.getDeferredRegister(backingRegistry);

        if (deferredRegister == null) {
            throw new IllegalArgumentException("Unhandled registry: " + backingRegistry);
        }

        return deferredRegister.register(holder.getPath(), holder.getValueFactory());
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getRegistryContainer(holder)
            .registerEntityAttribute(holder, attributeSupplierBuilderSupplier);
    }

    private NeoForgeBLibModRegistryContainer getRegistryContainer(BLibHolder<?> holder) {
        return getRegistryContainer(holder.getRegistry().getMod());
    }

    private NeoForgeBLibModRegistryContainer getRegistryContainer(BLibMod mod) {
        return modIdToRegistryContainerMap.computeIfAbsent(mod, $ -> new NeoForgeBLibModRegistryContainer(mod));
    }
}
