package com.avp.common.data;

import com.alien.common.data.GrowthStageReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import com.avp.service.Services;

public class AVPReloadListeners {

    public static final PreparableReloadListener GROWTH_STAGES_RELOAD_LISTENER = register(
        GrowthStageReloadListener.DIRECTORY_NAME,
        new GrowthStageReloadListener()
    );

    private static PreparableReloadListener register(String id, PreparableReloadListener listener) {
        return Services.REGISTRY.registerReloadListener(id, listener);
    }

    public static void initialize() {}
}
