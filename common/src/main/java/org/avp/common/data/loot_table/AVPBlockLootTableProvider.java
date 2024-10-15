package org.avp.common.data.loot_table;

import org.avp.api.common.data.loot_table.AbstractAVPBlockLootTableProvider;
import org.avp.common.AVPConstants;
import org.avp.common.registry.block.AVPBlockDataRegistry;

public class AVPBlockLootTableProvider extends AbstractAVPBlockLootTableProvider {

    protected AVPBlockLootTableProvider() {
        super(AVPConstants.MOD_ID);
    }

    @Override
    public void generate() {
        AVPBlockDataRegistry.INSTANCE.getEntries().forEach(entry -> {
            var block = entry.getHolder().get();
            var lootProvider = entry.getLootTableBuilder();
            var lootTableBuilder = lootProvider.apply(block);

            add(block, lootTableBuilder);
        });
    }
}
