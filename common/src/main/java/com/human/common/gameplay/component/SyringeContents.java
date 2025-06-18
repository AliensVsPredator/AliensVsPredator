package com.human.common.gameplay.component;

import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneModifierKey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static final StreamCodec<FriendlyByteBuf, SyringeContents> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, GeneBonusDataEntry.STREAM_CODEC),
        SyringeContents::geneBonusDataEntries,
        SyringeContents::new
    );

    public Map<GeneModifierKey, Double> toMap() {
        return geneBonusDataEntries.stream()
            .collect(Collectors.toMap(GeneBonusDataEntry::toKey, GeneBonusDataEntry::value));
    }
}
