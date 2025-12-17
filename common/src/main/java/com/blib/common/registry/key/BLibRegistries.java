package com.blib.common.registry.key;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import com.blib.BLib;
import com.blib.common.network.data.DataSyncKey;

public class BLibRegistries {

    public static final ResourceKey<Registry<DataSyncKey<?>>> DATA_SYNC_KEYS = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("data_sync_keys")
    );
}
