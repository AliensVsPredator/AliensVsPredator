package com.blib.neoforge.internal.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.blib.common.gameplay.model.spawning.BLibEntitySpawnData;
import com.blib.internal.common.registry.BLibRegistries;
import com.just.core.functional.tuple.Tuple2;
import com.just.core.functional.tuple.Tuple4;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.ItemLike;
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

public class BLibNeoForgeModContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> compostableData;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    private final List<BLibEntitySpawnData<?>> entitySpawnDataEntries;

    public BLibNeoForgeModContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = BLibRegistries.REGISTRATION_ORDER
            .stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    registry -> createDeferredRegistry(mod.getId(), registry)
                )
            );

        this.compostableData = new ArrayList<>();
        this.entityAttributeSupplierPairs = new ArrayList<>();
        this.entitySpawnDataEntries = new ArrayList<>();
    }

    public List<Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean>> getCompostableData() {
        return Collections.unmodifiableList(compostableData);
    }

    public List<BLibEntitySpawnData<?>> getEntitySpawnDataEntries() {
        return Collections.unmodifiableList(entitySpawnDataEntries);
    }

    /* package-private */ void registerCompostable(Tuple4<BLibHolder<? extends ItemLike>, Float, Boolean, Boolean> tuple) {
        compostableData.add(tuple);
    }

    /* package-private */ void registerEntityAttributes(
        BLibHolder<? extends EntityType<? extends LivingEntity>> holder,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        entityAttributeSupplierPairs.add(new Tuple2<>(holder, attributeSupplierBuilderSupplier));
    }

    /* package-private */ <T extends Mob> void registerEntitySpawnData(BLibEntitySpawnData<T> spawnData) {
        entitySpawnDataEntries.add(spawnData);
    }

    @SuppressWarnings("unchecked")
    /* package-private */ <T> DeferredRegister<T> getDeferredRegister(Registry<T> registry) {
        return (DeferredRegister<T>) registryToDeferredRegisterMap.get(registry);
    }

    /* package-private */ Collection<DeferredRegister<?>> getDeferredRegisters() {
        return Collections.unmodifiableCollection(registryToDeferredRegisterMap.values());
    }

    /* package-private */ List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> getEntityAttributeSupplierPairs() {
        return Collections.unmodifiableList(entityAttributeSupplierPairs);
    }
}
