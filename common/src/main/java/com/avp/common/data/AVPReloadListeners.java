package com.avp.common.data;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import com.avp.service.Services;

public class AVPReloadListeners {

    public static final PreparableReloadListener GENE_BONUS_DATA_RELOAD_LISTENER = register(
        GeneBonusDataReloadListener.DIRECTORY_NAME,
        new GeneBonusDataReloadListener()
    );

    private static PreparableReloadListener register(String id, PreparableReloadListener listener) {
        return Services.REGISTRY.registerReloadListener(id, listener);
    }

    public static void initialize() {}
}
