package com.avp.fabric.service;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.lifecycle.AlienLifecycle;
import com.avp.common.lifecycle.registry.AlienLifecycleRegistry;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    private final List<LiteralArgumentBuilder<CommandSourceStack>> literalArgumentBuilders;

    public FabricRegistryService() {
        this.literalArgumentBuilders = new ArrayList<>();
    }

    @Override
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        var reference = Registry.registerForHolder(registry, AVPResources.location(id), supplier.get());
        @SuppressWarnings("unchecked")
        var holder = (Holder<T>) reference;
        return new AVPDeferredHolder<>(holder::value, () -> holder);
    }

    @Override
    public void registerCommand(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilders.add(literalArgumentBuilder);
    }

    @Override
    public Supplier<AlienLifecycle> registerAlienLifecycle(Supplier<AlienLifecycle> alienLifecycleSupplier) {
        var alienLifecycle = AlienLifecycleRegistry.register(alienLifecycleSupplier.get());
        return () -> alienLifecycle;
    }

    public void registerEntityAttributes(
        Supplier<? extends EntityType<? extends LivingEntity>> entityTypeSupplier,
        Supplier<AttributeSupplier.Builder> attributeSupplierBuilderSupplier
    ) {
        FabricDefaultAttributeRegistry.register(entityTypeSupplier.get(), attributeSupplierBuilderSupplier.get());
    }

    public List<LiteralArgumentBuilder<CommandSourceStack>> getLiteralArgumentBuilders() {
        return literalArgumentBuilders;
    }
}
