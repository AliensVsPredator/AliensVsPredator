package com.blib.mod.common.registry.init;

import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.data_sync.v1.model.DataSyncKey;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibDataSyncKeys {

    private static final BLibRegistry<DataSyncKey<?>> REGISTRY = BLib.MOD.registries().create(BLibBuiltInRegistries.DATA_SYNC_KEYS);

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

    public static final BLibHolder<DataSyncKey<List<ResourceLocation>>> ENTITY_DETACHED_LIMBS = register(
        "entity_detached_limbs",
        builder -> builder.networkSynchronized(BLibCodecs.Stream.RESOURCE_LOCATION.asList())
            .build(List.of())
    );

    private static <T> BLibHolder<DataSyncKey<T>> register(String path, Function<DataSyncKey.Builder<T>, DataSyncKey<T>> factory) {
        var resourceLocation = BLib.MOD.resources().createLocation(path);
        return REGISTRY.createHolder(path, () -> factory.apply(new DataSyncKey.Builder<>(resourceLocation)));
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
