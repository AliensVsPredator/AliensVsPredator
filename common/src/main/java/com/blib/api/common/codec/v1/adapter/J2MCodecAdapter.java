package com.blib.api.common.codec.v1.adapter;

import com.just.codec.schema.CodecSchema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import com.blib.api.common.codec.v1.BLibCodecs;

public class J2MCodecAdapter<A> implements Codec<A> {

    private final com.just.codec.Codec<A> codec;

    public J2MCodecAdapter(com.just.codec.Codec<A> codec) {
        this.codec = codec;
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        @SuppressWarnings("unchecked")
        var codecSchema = (CodecSchema<T>) BLibCodecs.DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
        var result = codec.decode(codecSchema, input);

        return result.isOk()
            ? DataResult.success(new Pair<>(result.unwrap(), input))
            : DataResult.error(() -> "Failed to decode");
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        @SuppressWarnings("unchecked")
        var codecSchema = (CodecSchema<T>) BLibCodecs.DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
        return DataResult.success(codec.encode(codecSchema, input));
    }
}
