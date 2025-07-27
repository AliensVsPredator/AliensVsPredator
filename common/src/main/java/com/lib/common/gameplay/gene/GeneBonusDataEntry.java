package com.lib.common.gameplay.gene;

import com.bvanseg.just.serialization.codec.stream.RecordStreamCodec;
import com.bvanseg.just.serialization.codec.stream.StreamCodec;
import com.bvanseg.just.serialization.codec.stream.impl.StreamCodecs;
import com.lib.common.util.codec.stream.impl.MojangStreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

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

    public static final StreamCodec<GeneBonusDataEntry> STREAM_CODEC = RecordStreamCodec.of(
        MojangStreamCodecs.RESOURCE_LOCATION,
        GeneBonusDataEntry::id,
        GeneOperationType.STREAM_CODEC,
        GeneBonusDataEntry::operation,
        StreamCodecs.DOUBLE,
        GeneBonusDataEntry::value,
        GeneBonusDataEntry::new
    );

    public static final StreamCodec<List<GeneBonusDataEntry>> LIST_STREAM_CODEC = STREAM_CODEC.asList();

    public GeneModifierKey toKey() {
        return new GeneModifierKey(id, operation);
    }
}
