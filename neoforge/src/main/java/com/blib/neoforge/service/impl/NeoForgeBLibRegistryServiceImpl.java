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

    private final Map<BLibMod, NeoForgeBLibModContainer> modToContainerMap;

    public NeoForgeBLibRegistryServiceImpl() {
        modToContainerMap = new ConcurrentHashMap<>();
    }

    public void finalize(BLibMod mod, IEventBus eventBus) {
        getModContainer(mod)
            .getDeferredRegisters()
            .forEach(deferredRegister -> deferredRegister.register(eventBus));

        eventBus.<EntityAttributeCreationEvent>addListener(event -> onRegisterEntityAttributes(mod, event));
    }

    @Override
    public <T> Holder<T> register(BLibHolder<T> holder, Supplier<? extends T> valueFactory) {
        var blibRegistry = holder.getRegistry();
        var modContainer = getModContainer(blibRegistry.getMod());
        var backingRegistry = blibRegistry.getBackingRegistry();
        @SuppressWarnings("unchecked")
        var deferredRegister = (DeferredRegister<T>) modContainer.getDeferredRegister(backingRegistry);

        if (deferredRegister == null) {
            throw new IllegalArgumentException("Unhandled registry: " + backingRegistry);
        }

        return deferredRegister.register(holder.getPath(), valueFactory);
    }

    @Override
    public void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        getModContainer(holder)
            .registerEntityAttribute(holder, attributeSupplierBuilderSupplier);
    }

    private void onRegisterEntityAttributes(BLibMod mod, EntityAttributeCreationEvent event) {
        getModContainer(mod).getEntityAttributeSupplierPairs()
            .forEach(pair -> event.put(pair.v1().get(), pair.v2().get().build()));
    }

    private NeoForgeBLibModContainer getModContainer(BLibHolder<?> holder) {
        return getModContainer(holder.getRegistry().getMod());
    }

    private NeoForgeBLibModContainer getModContainer(BLibMod mod) {
        return modToContainerMap.computeIfAbsent(mod, $ -> new NeoForgeBLibModContainer(mod));
    }
}
