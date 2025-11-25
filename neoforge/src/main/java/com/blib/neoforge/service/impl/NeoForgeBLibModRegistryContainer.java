package com.blib.neoforge.service.impl;

import com.blib.BLibHolder;
import com.blib.BLibMod;
import com.just.core.functional.tuple.Tuple2;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
import java.util.stream.Stream;

class NeoForgeBLibModRegistryContainer {

    private static <T> @NotNull DeferredRegister<T> createDeferredRegistry(String modId, Registry<T> registry) {
        return DeferredRegister.create(registry, modId);
    }

    private final BLibMod mod;

    private final Map<Registry<?>, DeferredRegister<?>> registryToDeferredRegisterMap;

    private final List<Tuple2<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>>> entityAttributeSupplierPairs;

    public NeoForgeBLibModRegistryContainer(BLibMod mod) {
        this.mod = mod;
        this.registryToDeferredRegisterMap = Stream.of(
            BuiltInRegistries.ARMOR_MATERIAL,
            BuiltInRegistries.BLOCK,
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            BuiltInRegistries.CREATIVE_MODE_TAB,
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            BuiltInRegistries.DECORATED_POT_PATTERN,
            BuiltInRegistries.ENTITY_TYPE,
            BuiltInRegistries.GAME_EVENT,
            BuiltInRegistries.ITEM,
            BuiltInRegistries.MENU,
            BuiltInRegistries.MOB_EFFECT,
            BuiltInRegistries.PARTICLE_TYPE,
            BuiltInRegistries.POINT_OF_INTEREST_TYPE,
            BuiltInRegistries.RECIPE_SERIALIZER,
            BuiltInRegistries.RECIPE_TYPE,
            BuiltInRegistries.SOUND_EVENT,
            BuiltInRegistries.VILLAGER_PROFESSION
        ).collect(Collectors.toMap(Function.identity(), registry -> createDeferredRegistry(mod.getId(), registry)));

        this.entityAttributeSupplierPairs = new ArrayList<>();
    }

    public void registerEntityAttribute(BLibHolder<? extends EntityType<? extends LivingEntity>> holder, Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier) {
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
