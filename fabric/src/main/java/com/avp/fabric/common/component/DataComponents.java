package com.avp.fabric.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

import java.util.function.UnaryOperator;

import com.avp.fabric.AVPResources;

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

    public static final DataComponentType<Integer> CANISTER_CAPACITY = register(
        "canister_capacity",
        builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        var resourceLocation = AVPResources.location(id);
        var component = unaryOperator.apply(DataComponentType.builder()).build();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, resourceLocation, component);
    }

    public static void initialize() {}
}
