package com.avp.core.common.component;

import com.avp.core.platform.service.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.avp.core.AVPResources;

public class DataComponents {

    public static final Supplier<DataComponentType<Integer>> AMMUNITION = register(
        "ammunition",
        builder -> builder.persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    public static final Supplier<DataComponentType<ArmorCaseContainerContents>> ARMOR_CASE_CONTAINER = register(
        "armor_case_container",
        builder -> builder.persistent(ArmorCaseContainerContents.CODEC)
            .networkSynchronized(ArmorCaseContainerContents.STREAM_CODEC)
            .cacheEncoding()
    );

    private static <T> Supplier<DataComponentType<T>> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return Services.REGISTRY.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, () -> unaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {}
}
