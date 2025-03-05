package com.avp.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ArmorCaseContainerContents(
    ItemStack head,
    ItemStack chest,
    ItemStack legs,
    ItemStack feet
) {

    public static final ArmorCaseContainerContents EMPTY = new ArmorCaseContainerContents(
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY,
        ItemStack.EMPTY
    );

    public static final Codec<ArmorCaseContainerContents> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("head").forGetter(ArmorCaseContainerContents::head),
            ItemStack.OPTIONAL_CODEC.fieldOf("chest").forGetter(ArmorCaseContainerContents::chest),
            ItemStack.OPTIONAL_CODEC.fieldOf("legs").forGetter(ArmorCaseContainerContents::legs),
            ItemStack.OPTIONAL_CODEC.fieldOf("feet").forGetter(ArmorCaseContainerContents::feet)
        )
            .apply(instance, ArmorCaseContainerContents::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorCaseContainerContents> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        ArmorCaseContainerContents::head,
        ItemStack.OPTIONAL_STREAM_CODEC,
        ArmorCaseContainerContents::chest,
        ItemStack.OPTIONAL_STREAM_CODEC,
        ArmorCaseContainerContents::legs,
        ItemStack.OPTIONAL_STREAM_CODEC,
        ArmorCaseContainerContents::feet,
        ArmorCaseContainerContents::new
    );
}
