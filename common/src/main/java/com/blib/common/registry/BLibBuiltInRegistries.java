package com.blib.common.registry;

import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.common.network.data.DataSyncKey;
import com.blib.common.registry.key.BLibRegistries;

public class BLibBuiltInRegistries {

    public static final Registry<DataSyncKey<?>> DATA_SYNC_KEYS = BLibCustomRegistryBuilder.create(BLib.MOD, BLibRegistries.DATA_SYNC_KEYS)
        .shouldSync(true)
        .build();

    @ApiStatus.Internal
    private BLibBuiltInRegistries() {
        throw new UnsupportedOperationException();
    }
}
