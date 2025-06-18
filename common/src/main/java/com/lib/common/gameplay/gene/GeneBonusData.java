package com.lib.common.gameplay.gene;

import com.lib.common.data.EntityTypePredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record GeneBonusData(
    EntityTypePredicate entityTypePredicate,
    List<GeneBonusDataEntry> geneBonusDataEntries
) {

    public static final Codec<GeneBonusData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            EntityTypePredicate.CODEC
                .fieldOf("entityTypePredicate")
                .forGetter(GeneBonusData::entityTypePredicate),
            Codec.list(GeneBonusDataEntry.CODEC)
                .fieldOf("geneBonusDataEntries")
                .forGetter(GeneBonusData::geneBonusDataEntries)
        )
            .apply(instance, GeneBonusData::new)
    );
}
