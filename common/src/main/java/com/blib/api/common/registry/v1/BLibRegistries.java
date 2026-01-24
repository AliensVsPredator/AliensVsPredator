package com.blib.api.common.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataSyncKey;
import com.blib.mod.BLib;

public class BLibRegistries {

    public static final ResourceKey<Registry<DataSyncKey<?>>> DATA_SYNC_KEYS = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("data_sync_keys")
    );

    @ApiStatus.Internal
    private BLibRegistries() {
        throw new UnsupportedOperationException();
    }
}
