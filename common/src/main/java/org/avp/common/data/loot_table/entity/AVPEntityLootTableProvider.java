package org.avp.common.data.loot_table.entity;

import org.avp.common.data.loot_table.AbstractAVPEntityLootTableProvider;
import org.avp.common.entity.data.AVPEntityDataRegistry;

public class AVPEntityLootTableProvider extends AbstractAVPEntityLootTableProvider {

    @Override
    public void generate() {
        AVPEntityDataRegistry.INSTANCE.getEntries().forEach(entityData -> {
            var entityType = entityData.getHolder().get();
            var lootTableBuilderOptional = entityData.getLootTableBuilder();
            lootTableBuilderOptional.ifPresent(lootTableBuilder -> add(entityType, lootTableBuilder));
        });
    }
}
