package org.avp.common.worldgen;

import org.avp.common.registry.AVPRegistry;
import org.avp.common.worldgen.feature.AVPOreFeatures;

public class AVPWorldGenFeatures implements AVPRegistry {

    private static final AVPWorldGenFeatures INSTANCE = new AVPWorldGenFeatures();

    public static AVPWorldGenFeatures getInstance() {
        return INSTANCE;
    }

    @Override
    public void register() {
        AVPOreFeatures.getInstance().register();
    }
}
