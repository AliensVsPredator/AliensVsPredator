package org.avp.fabric.service;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import org.avp.api.Holder;
import org.avp.common.entity.attribute.AVPEntityAttributesBindingRegistry;
import org.avp.common.registry.AVPDeferredRegistry;

import java.util.function.Supplier;

public class FabricEntityAttributeRegistry extends AVPDeferredRegistry<Void> {

    public static final FabricEntityAttributeRegistry INSTANCE = new FabricEntityAttributeRegistry();

    private FabricEntityAttributeRegistry() {}

    @Override
    protected Holder<Void> createHolder(String registryName, Supplier<Void> supplier) {
        return null;
    }

    @Override
    public void register() {
        AVPEntityAttributesBindingRegistry.getBindings().forEach(binding -> {
            var entityType = binding.getKey().get();
            var attributeSupplier = binding.getValue();
            FabricDefaultAttributeRegistry.register(entityType, attributeSupplier);
        });
    }
}
