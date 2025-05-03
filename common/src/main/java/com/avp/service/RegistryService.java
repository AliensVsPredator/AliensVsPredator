package com.avp.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.registry.AVPDeferredHolder;

public interface RegistryService {

    <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier);

    void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    Supplier<AlienLifecycle> registerAlienLifecycle(Supplier<AlienLifecycle> alienLifecycleSupplier);

    void registerEntityAttributes(
        Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    );
}
