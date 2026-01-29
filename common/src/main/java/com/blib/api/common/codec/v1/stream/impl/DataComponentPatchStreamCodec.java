package com.blib.api.common.codec.v1.stream.impl;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.data_component.v1.DataComponentPatchAccessor;

public class DataComponentPatchStreamCodec implements StreamCodec<DataComponentPatch> {

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull DataComponentPatch patch) {
        if (patch.isEmpty()) {
            schema.writeInt(input, 0);
            schema.writeInt(input, 0);
            return;
        }

        var present = new ArrayList<Map.Entry<DataComponentType<?>, Optional<?>>>();
        var removed = new ArrayList<DataComponentType<?>>();
        var map = ((DataComponentPatchAccessor) (Object) patch).blib$getMap();

        for (var entry : map.reference2ObjectEntrySet()) {
            if (entry.getValue().isPresent()) {
                present.add(entry);
            } else {
                removed.add(entry.getKey());
            }
        }

        schema.writeInt(input, present.size());
        schema.writeInt(input, removed.size());

        for (var entry : present) {
            @SuppressWarnings("unchecked")
            var type = (DataComponentType<Object>) entry.getKey();
            var value = entry.getValue().orElseThrow();

            schema.write(input, BLibCodecs.Stream.DATA_COMPONENT_TYPE, type);

            encodeComponent(input, type, value);
        }

        for (var type : removed) {
            schema.write(input, BLibCodecs.Stream.DATA_COMPONENT_TYPE, type);
        }
    }

    @Override
    public <T> @NotNull DataComponentPatch decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
        int presentCount = schema.readInt(input);
        int removedCount = schema.readInt(input);

        if (presentCount == 0 && removedCount == 0) {
            return DataComponentPatch.EMPTY;
        }

        var map = new Reference2ObjectArrayMap<DataComponentType<?>, Optional<?>>(Math.min(presentCount + removedCount, 65536));

        for (int i = 0; i < presentCount; i++) {
            var type = schema.read(input, BLibCodecs.Stream.DATA_COMPONENT_TYPE);
            var value = decodeComponent(input, type);
            map.put(type, Optional.ofNullable(value));
        }

        for (int i = 0; i < removedCount; i++) {
            var type = schema.read(input, BLibCodecs.Stream.DATA_COMPONENT_TYPE);
            map.put(type, Optional.empty());
        }

        return DataComponentPatchAccessor.blib$construct(map);
    }

    private static <A, T> void encodeComponent(T input, DataComponentType<A> type, A value) {
        if (input instanceof RegistryFriendlyByteBuf byteBuf) {
            type.streamCodec().encode(byteBuf, value);
            return;
        }

        throw new UnsupportedOperationException();
    }

    private static <A, T> @Nullable A decodeComponent(T input, DataComponentType<A> type) {
        if (input instanceof RegistryFriendlyByteBuf byteBuf) {
            return type.streamCodec().decode(byteBuf);
        }

        return null;
    }
}
