package org.avp.neoforge.service;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.avp.common.entity.type.AVPEntityDataRegistry;

public class NeoForgeEntityAttributeRegistry {

    private static final NeoForgeEntityAttributeRegistry INSTANCE = new NeoForgeEntityAttributeRegistry();

    public static NeoForgeEntityAttributeRegistry getInstance() {
        return INSTANCE;
    }

    private NeoForgeEntityAttributeRegistry() {}

    public void createEntityAttributes(EntityAttributeCreationEvent event) {
        AVPEntityDataRegistry.INSTANCE.getLivingEntries().forEach(entityData -> {
            var entityType = entityData.getHolder().get();
            var attributeSupplierOptional = entityData.getAttributeSupplier();
            attributeSupplierOptional.ifPresent(attributeSupplier -> event.put(entityType, attributeSupplier));
        });
    }
}
