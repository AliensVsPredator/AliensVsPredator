package com.blib.neoforge.internal.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.internal.common.registry.BLibRegistries;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class NeoForgeBLibModContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    public NeoForgeBLibModContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = BLibRegistries.REGISTRATION_ORDER
            .stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    registry -> createDeferredRegistry(mod.getId(), registry)
                )
            );

        this.entityAttributeSupplierPairs = new ArrayList<>();
    }

    public void registerEntityAttribute(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        entityAttributeSupplierPairs.add(new Tuple2<>(holder, attributeSupplierBuilderSupplier));
    }

    @SuppressWarnings("unchecked")
    public <T> DeferredRegister<T> getDeferredRegister(Registry<T> registry) {
        return (DeferredRegister<T>) registryToDeferredRegisterMap.get(registry);
    }

    public Collection<DeferredRegister<?>> getDeferredRegisters() {
        return Collections.unmodifiableCollection(registryToDeferredRegisterMap.values());
    }

    public List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityAttributeSupplierPairs() {
        return Collections.unmodifiableList(entityAttributeSupplierPairs);
    }

    public BLibMod getMod() {
        return mod;
    }
}
