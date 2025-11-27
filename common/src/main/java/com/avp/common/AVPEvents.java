package com.avp.common;

import com.lib.common.registry.GeneBonusDataRegistry;

public class AVPEvents {

    public static void onTagsUpdated() {
        GeneBonusDataRegistry.rebuildLookupMappings();
    }
}
