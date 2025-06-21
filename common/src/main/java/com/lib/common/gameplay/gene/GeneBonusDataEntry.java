package com.lib.common.gameplay.gene;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record GeneBonusDataEntry(
    ResourceLocation id,
    GeneOperationType operation,
    double value
) {

    public static final Codec<GeneBonusDataEntry> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(GeneBonusDataEntry::id),
            GeneOperationType.CODEC.fieldOf("operation").forGetter(GeneBonusDataEntry::operation),
            Codec.DOUBLE.fieldOf("value").forGetter(GeneBonusDataEntry::value)
        ).apply(instance, GeneBonusDataEntry::new)
    );

    public static final StreamCodec<FriendlyByteBuf, GeneBonusDataEntry> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        GeneBonusDataEntry::id,
        GeneOperationType.STREAM_CODEC,
        GeneBonusDataEntry::operation,
        ByteBufCodecs.DOUBLE,
        GeneBonusDataEntry::value,
        GeneBonusDataEntry::new
    );

    public GeneModifierKey toKey() {
        return new GeneModifierKey(id, operation);
    }
}
