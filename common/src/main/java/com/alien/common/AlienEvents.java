package com.alien.common;

import com.alien.common.registry.GrowthStageRegistry;

public class AlienEvents {

    public static void onTagsUpdated() {
        GrowthStageRegistry.rebuildLookupMappings();
    }
}
