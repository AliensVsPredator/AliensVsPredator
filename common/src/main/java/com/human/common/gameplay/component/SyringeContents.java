package com.human.common.gameplay.component;

import com.bvanseg.just.serialization.codec.stream.RecordStreamCodec;
import com.bvanseg.just.serialization.codec.stream.StreamCodec;
import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record SyringeContents(
    List<GeneBonusDataEntry> geneBonusDataEntries
) {

    public static final SyringeContents EMPTY = new SyringeContents(List.of());

    public static final Codec<SyringeContents> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.list(GeneBonusDataEntry.CODEC)
                .fieldOf("geneBonusDataEntries")
                .forGetter(SyringeContents::geneBonusDataEntries)
        ).apply(instance, SyringeContents::new)
    );

    public static final StreamCodec<SyringeContents> STREAM_CODEC = RecordStreamCodec.of(
        GeneBonusDataEntry.LIST_STREAM_CODEC,
        SyringeContents::geneBonusDataEntries,
        SyringeContents::new
    );
}
