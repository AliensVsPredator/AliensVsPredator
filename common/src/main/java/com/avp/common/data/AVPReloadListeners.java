package com.avp.common.data;

import net.minecraft.server.packs.resources.PreparableReloadListener;

import com.avp.service.Services;

public class AVPReloadListeners {

    // FIXME:
    // public static final PreparableReloadListener GENE_BONUS_DATA_RELOAD_LISTENER = register(
    // GeneBonusDataReloadListener.DIRECTORY_NAME,
    // new GeneBonusDataReloadListener()
    // );
    //
    // public static final PreparableReloadListener GROWTH_STAGES_RELOAD_LISTENER = register(
    // GrowthStageReloadListener.DIRECTORY_NAME,
    // new GrowthStageReloadListener()
    // );
    //
    // public static final PreparableReloadListener INFECTIONS_RELOAD_LISTENER = register(
    // InfectionReloadListener.DIRECTORY_NAME,
    // new InfectionReloadListener()
    // );

    private static PreparableReloadListener register(String id, PreparableReloadListener listener) {
        return Services.REGISTRY.registerReloadListener(id, listener);
    }

    public static void initialize() {}
}
