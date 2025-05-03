package com.avp.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

import java.util.function.UnaryOperator;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPDataComponents {

    public static final AVPDeferredHolder<DataComponentType<Integer>> AMMUNITION = register(
        "ammunition",
        builder -> builder.persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<ArmorCaseContainerContents>> ARMOR_CASE_CONTAINER = register(
        "armor_case_container",
        builder -> builder.persistent(ArmorCaseContainerContents.CODEC)
            .networkSynchronized(ArmorCaseContainerContents.STREAM_CODEC)
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<Integer>> CANISTER_CAPACITY = register(
        "canister_capacity",
        builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    private static <T> AVPDeferredHolder<DataComponentType<T>> register(
        String id,
        UnaryOperator<DataComponentType.Builder<T>> unaryOperator
    ) {
        return Services.REGISTRY.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            id,
            () -> unaryOperator.apply(DataComponentType.builder()).build()
        );
    }

    public static void initialize() {}
}
