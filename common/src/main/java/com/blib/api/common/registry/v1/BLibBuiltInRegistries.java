package com.blib.api.common.registry.v1;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataSyncKey;
import com.blib.mod.BLib;

public class BLibBuiltInRegistries {

    public static final Registry<DataSyncKey<?>> DATA_SYNC_KEYS = BLibCustomRegistryBuilder.create(BLib.MOD, BLibRegistries.DATA_SYNC_KEYS)
        .shouldSync(true)
        .build();

    @ApiStatus.Internal
    private BLibBuiltInRegistries() {
        throw new UnsupportedOperationException();
    }
}
