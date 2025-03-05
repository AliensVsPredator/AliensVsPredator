package com.avp.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.UnaryOperator;

import com.avp.AVPResources;

public class DataComponents {

    public static final DataComponentType<Integer> AMMUNITION = register(
        "ammunition",
        builder -> builder.persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    public static final DataComponentType<ArmorCaseContainerContents> ARMOR_CASE_CONTAINER = register(
        "armor_case_container",
        builder -> builder.persistent(ArmorCaseContainerContents.CODEC)
            .networkSynchronized(ArmorCaseContainerContents.STREAM_CODEC)
            .cacheEncoding()
    );

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        var resourceLocation = AVPResources.location(id);
        var component = unaryOperator.apply(DataComponentType.builder()).build();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, resourceLocation, component);
    }

    public static void initialize() {}
}
