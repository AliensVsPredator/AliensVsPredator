package com.human.common.gameplay.component;

import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record GeneReaderContents(
    Map<GeneReaderMode, List<GeneBonusDataEntry>> geneBonusDataEntriesByMode
) {

    public static final GeneReaderContents EMPTY = new GeneReaderContents(new EnumMap<>(GeneReaderMode.class));

    public static final Codec<GeneReaderContents> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.unboundedMap(GeneReaderMode.CODEC, GeneBonusDataEntry.CODEC.listOf())
                .fieldOf("geneBonusDataEntriesByMode")
                .forGetter(GeneReaderContents::geneBonusDataEntriesByMode)
        ).apply(instance, GeneReaderContents::new)
    );

    public static final StreamCodec<FriendlyByteBuf, GeneReaderContents> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull GeneReaderContents decode(FriendlyByteBuf buf) {
            var size = buf.readVarInt();
            var map = new EnumMap<GeneReaderMode, List<GeneBonusDataEntry>>(GeneReaderMode.class);

            for (var i = 0; i < size; i++) {
                var mode = GeneReaderMode.STREAM_CODEC.decode(buf);
                var entries = buf.readList(GeneBonusDataEntry.STREAM_CODEC);
                map.put(mode, entries);
            }

            return new GeneReaderContents(map);
        }

        @Override
        public void encode(FriendlyByteBuf buf, GeneReaderContents contents) {
            var map = contents.geneBonusDataEntriesByMode();

            buf.writeVarInt(map.size());

            for (Map.Entry<GeneReaderMode, List<GeneBonusDataEntry>> entry : map.entrySet()) {
                GeneReaderMode.STREAM_CODEC.encode(buf, entry.getKey());
                buf.writeCollection(entry.getValue(), GeneBonusDataEntry.STREAM_CODEC);
            }
        }
    };
}
