package com.human.common.gameplay.component;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record GeneReaderContents(
    Map<GeneReaderMode, List<GeneBonusDataEntry>> geneBonusDataEntriesByMode
) {

    public static final GeneReaderContents EMPTY = new GeneReaderContents(new EnumMap<>(GeneReaderMode.class));

    public static final Codec<GeneReaderContents> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.unboundedMap(GeneReaderMode.CODEC, GeneBonusDataEntry.CODEC.listOf())
                .fieldOf("geneBonusDataEntriesByMode")
                .forGetter(GeneReaderContents::geneBonusDataEntriesByMode)
        ).apply(instance, GeneReaderContents::new)
    );

    public static final StreamCodec<GeneReaderContents> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull GeneReaderContents value) {
            var map = value.geneBonusDataEntriesByMode();

            streamCodecSchema.writeVarInt(input, map.size());

            for (Map.Entry<GeneReaderMode, List<GeneBonusDataEntry>> entry : map.entrySet()) {
                streamCodecSchema.write(input, GeneReaderMode.STREAM_CODEC, entry.getKey());
                streamCodecSchema.write(input, GeneBonusDataEntry.LIST_STREAM_CODEC, entry.getValue());
            }
        }

        @Override
        public @NotNull <T> GeneReaderContents decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var size = streamCodecSchema.readVarInt(input);
            var map = new EnumMap<GeneReaderMode, List<GeneBonusDataEntry>>(GeneReaderMode.class);

            for (var i = 0; i < size; i++) {
                var mode = GeneReaderMode.STREAM_CODEC.decode(streamCodecSchema, input);
                var entries = GeneBonusDataEntry.LIST_STREAM_CODEC.decode(streamCodecSchema, input);
                map.put(mode, entries);
            }

            return new GeneReaderContents(map);
        }
    };
}
