package org.avp.common.data.loot_table;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

import org.avp.common.data.loot_table.block.AVPBlockLootTableProvider;
import org.avp.common.data.loot_table.entity.AVPEntityLootTableProvider;

public class AVPLootTableProvider extends LootTableProvider {

    public AVPLootTableProvider(PackOutput packOutput) {
        super(
            packOutput,
            Set.of(),
            List.of(
                new SubProviderEntry(AVPBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(AVPEntityLootTableProvider::new, LootContextParamSets.ENTITY)
            )
        );
    }
}
