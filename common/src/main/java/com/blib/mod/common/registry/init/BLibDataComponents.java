package com.blib.mod.common.registry.init;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.UUID;
import java.util.function.UnaryOperator;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibDataComponents {

    private static final BLibRegistry<DataComponentType<?>> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.DATA_COMPONENT_TYPE);

    public static final BLibHolder<DataComponentType<UUID>> AZ_ID = register(
        "az_id",
        builder -> builder.persistent(UUIDUtil.CODEC)
            .networkSynchronized(UUIDUtil.STREAM_CODEC)
    );

    private static <T> BLibHolder<DataComponentType<T>> register(String path, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return REGISTRY.createHolder(path, () -> unaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
