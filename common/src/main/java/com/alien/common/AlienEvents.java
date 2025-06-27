package com.alien.common;

import com.alien.common.registry.GrowthStageRegistry;
import com.alien.common.registry.InfectionRegistry;
import com.lib.common.registry.GeneBonusDataRegistry;

public class AlienEvents {

    public static void onTagsUpdated() {
        GeneBonusDataRegistry.rebuildLookupMappings();
        GrowthStageRegistry.rebuildLookupMappings();
        InfectionRegistry.rebuildLookupMappings();
    }
}
