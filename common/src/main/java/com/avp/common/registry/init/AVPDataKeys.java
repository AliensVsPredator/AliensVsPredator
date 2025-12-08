package com.avp.common.registry.init;

import com.blib.common.network.data.DataKey;
import com.blib.common.registry.DataKeyRegistry;
import com.just.codec.stream.impl.StreamCodecs;

import java.util.function.Function;

import com.avp.AVPResources;

public class AVPDataKeys {

    public static final DataKey<Boolean> ENTITY_HAS_WARP_EFFECT = register(
        "entity_has_warp_effect",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final DataKey<Boolean> ENTITY_HAS_TARGET = register(
        "entity_has_target",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    public static final DataKey<Boolean> ENTITY_IS_MOVING_HORIZONTALLY = register(
        "entity_is_moving_horizontally",
        builder -> builder.networkSynchronized(StreamCodecs.BOOLEAN)
            .build(false)
    );

    private static <T> DataKey<T> register(String id, Function<DataKey.Builder<T>, DataKey<T>> factory) {
        var resourceLocation = AVPResources.location(id);
        var dataAccessor = factory.apply(new DataKey.Builder<>(resourceLocation));
        return DataKeyRegistry.register(resourceLocation, dataAccessor);
    }

    public static void initialize() {}
}
