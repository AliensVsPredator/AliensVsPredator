package com.alien.common;

import com.alien.common.registry.AlienLifecycleRegistry;

public class AlienEvents {

    public static void onTagsUpdated() {
        AlienLifecycleRegistry.rebuildLookupMappings();
    }
}
