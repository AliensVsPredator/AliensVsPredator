package org.avp.common.data.loot_table;

import org.avp.api.data.loot_table.AbstractAVPBlockLootTableProvider;
import org.avp.api.registry.AVPDeferredBlockRegistry;
import org.avp.common.AVPConstants;

public class AVPBlockLootTableProvider extends AbstractAVPBlockLootTableProvider {

    protected AVPBlockLootTableProvider() {
        super(AVPConstants.MOD_ID);
    }

    @Override
    public void generate() {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var data = tuple.second();
            var lootProvider = data.lootProvider();
            var lootTableBuilder = lootProvider.apply(block);

            add(block, lootTableBuilder);
        });
    }
}
