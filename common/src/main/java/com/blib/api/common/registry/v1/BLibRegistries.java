package com.blib.api.common.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataSyncKey;
import com.blib.api.common.faction.v1.FactionType;
import com.blib.api.common.storage.v1.DataStoreType;
import com.blib.mod.BLib;

public class BLibRegistries {

    public static final ResourceKey<Registry<DataStoreType<?>>> DATA_STORE_TYPES = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("data_store_types")
    );

    public static final ResourceKey<Registry<DataSyncKey<?>>> DATA_SYNC_KEYS = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("data_sync_keys")
    );

    public static final ResourceKey<Registry<FactionType<?>>> FACTION_TYPES = ResourceKey.createRegistryKey(
        BLib.MOD.resources().createLocation("faction_types")
    );

    @ApiStatus.Internal
    private BLibRegistries() {
        throw new UnsupportedOperationException();
    }
}
