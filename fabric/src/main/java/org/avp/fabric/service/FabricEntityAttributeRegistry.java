package org.avp.fabric.service;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import java.util.function.Supplier;

import org.avp.api.registry.AVPDeferredRegistry;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.registry.AVPEntityDataRegistry;

public class FabricEntityAttributeRegistry extends AVPDeferredRegistry<Void> {

    public static final FabricEntityAttributeRegistry INSTANCE = new FabricEntityAttributeRegistry();

    private FabricEntityAttributeRegistry() {}

    @Override
    protected BLHolder<Void> createHolder(String registryName, Supplier<Void> supplier) {
        return null;
    }

    @Override
    public void register() {
        AVPEntityDataRegistry.INSTANCE.getLivingEntries().forEach(entityData -> {
            var entityType = entityData.getHolder().get();
            var attributeSupplierOptional = entityData.getAttributeSupplier();
            attributeSupplierOptional.ifPresent(
                attributeSupplier -> FabricDefaultAttributeRegistry.register(entityType, attributeSupplier)
            );
        });
    }
}
