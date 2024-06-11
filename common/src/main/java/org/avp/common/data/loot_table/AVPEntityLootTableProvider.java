package org.avp.common.data.loot_table;

import org.avp.api.data.loot_table.AbstractAVPEntityLootTableProvider;
import org.avp.common.registry.AVPEntityDataRegistry;

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
