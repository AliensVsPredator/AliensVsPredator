package com.blib.common.registry.init;

import com.just.codec.stream.impl.StreamCodecs;

import java.util.function.Function;

import com.blib.BLib;
import com.blib.common.network.data.DataSyncKey;
import com.blib.common.registry.BLibBuiltInRegistries;
import com.blib.common.registry.BLibHolder;
import com.blib.common.registry.BLibRegistry;

public class BLibDataSyncKeys {

    private static final BLibRegistry<DataSyncKey<?>> REGISTRY = BLib.MOD.registries().create(BLibBuiltInRegistries.DATA_SYNC_KEYS);

    // TODO: Move to human module.
    public static final BLibHolder<DataSyncKey<Boolean>> ENTITY_HAS_WARP_EFFECT = register(
        "entity_has_warp_effect",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final BLibHolder<DataSyncKey<Boolean>> ENTITY_HAS_TARGET = register(
        "entity_has_target",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final BLibHolder<DataSyncKey<Boolean>> ENTITY_IS_MOVING_HORIZONTALLY = register(
        "entity_is_moving_horizontally",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    private static <T> BLibHolder<DataSyncKey<T>> register(String path, Function<DataSyncKey.Builder<T>, DataSyncKey<T>> factory) {
        var resourceLocation = BLib.MOD.resources().createLocation(path);
        return REGISTRY.createHolder(path, () -> factory.apply(new DataSyncKey.Builder<>(resourceLocation)));
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
