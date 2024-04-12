package org.avp.neoforge.service;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import org.avp.common.entity.attribute.*;

public class NeoForgeEntityAttributeRegistry {

    private static final NeoForgeEntityAttributeRegistry INSTANCE = new NeoForgeEntityAttributeRegistry();

    public static NeoForgeEntityAttributeRegistry getInstance() {
        return INSTANCE;
    }

    private NeoForgeEntityAttributeRegistry() {}

    public void createEntityAttributes(EntityAttributeCreationEvent event) {
        AVPEntityAttributesBindingRegistry.getBindings().forEach(binding -> {
            var entityType = binding.getKey().get();
            var attributeSupplier = binding.getValue();
            event.put(entityType, attributeSupplier);
        });
    }
}
