package com.human.common.registry.init;

import com.human.common.gameplay.component.ArmorCaseContainerContents;
import com.human.common.gameplay.component.GeneReaderContents;
import com.human.common.gameplay.component.GeneReaderMode;
import com.human.common.gameplay.component.SyringeContents;
import com.human.common.gameplay.component.SyringeMode;
import com.lib.common.util.codec.stream.adapter.JustStreamCodecToMojangStreamCodecAdapter;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

import java.util.function.UnaryOperator;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class HumanDataComponents {

    public static final AVPDeferredHolder<DataComponentType<Integer>> AMMUNITION = register(
        "ammunition",
        builder -> builder.persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<ArmorCaseContainerContents>> ARMOR_CASE_CONTAINER = register(
        "armor_case_container",
        builder -> builder.persistent(ArmorCaseContainerContents.CODEC)
            .networkSynchronized(new JustStreamCodecToMojangStreamCodecAdapter<>(ArmorCaseContainerContents.STREAM_CODEC))
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<Integer>> CANISTER_CAPACITY = register(
        "canister_capacity",
        builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<GeneReaderContents>> GENE_READER_CONTENTS = register(
        "gene_reader_contents",
        builder -> builder.persistent(GeneReaderContents.CODEC)
            .networkSynchronized(new JustStreamCodecToMojangStreamCodecAdapter<>(GeneReaderContents.STREAM_CODEC))
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<GeneReaderMode>> GENE_READER_MODE = register(
        "gene_reader_mode",
        builder -> builder.persistent(GeneReaderMode.CODEC)
            .networkSynchronized(new JustStreamCodecToMojangStreamCodecAdapter<>(GeneReaderMode.STREAM_CODEC))
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<Boolean>> IS_FIRING = register(
        "is_firing",
        builder -> builder.persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL)
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<SyringeContents>> SYRINGE_CONTENTS = register(
        "syringe_contents",
        builder -> builder.persistent(SyringeContents.CODEC)
            .networkSynchronized(new JustStreamCodecToMojangStreamCodecAdapter<>(SyringeContents.STREAM_CODEC))
            .cacheEncoding()
    );

    public static final AVPDeferredHolder<DataComponentType<SyringeMode>> SYRINGE_MODE = register(
        "syringe_mode",
        builder -> builder.persistent(SyringeMode.CODEC)
            .networkSynchronized(new JustStreamCodecToMojangStreamCodecAdapter<>(SyringeMode.STREAM_CODEC))
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
