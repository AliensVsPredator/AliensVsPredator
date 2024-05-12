package org.avp.common.data.loot_table.block;

import org.avp.common.data.loot_table.AbstractAVPBlockLootTableProvider;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPBlockLootTableProvider extends AbstractAVPBlockLootTableProvider {

    @Override
    public void generate() {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var data = tuple.second();
            var lootProvider = data.getLootProvider();
            var lootTableBuilder = lootProvider.apply(block);

            add(block, lootTableBuilder);
        });
    }
}
