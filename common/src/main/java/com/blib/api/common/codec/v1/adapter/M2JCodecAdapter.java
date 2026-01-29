package com.blib.api.common.codec.v1.adapter;

import com.just.codec.Codec;
import com.just.codec.schema.CodecSchema;
import com.just.core.functional.result.Result;
import com.mojang.serialization.DynamicOps;

import com.blib.api.common.codec.v1.BLibCodecs;

public class M2JCodecAdapter<A> implements Codec<A> {

    private final com.mojang.serialization.Codec<A> codec;

    public M2JCodecAdapter(com.mojang.serialization.Codec<A> codec) {
        this.codec = codec;
    }

    @Override
    public <T> T encode(CodecSchema<T> codecSchema, A value) {
        @SuppressWarnings("unchecked")
        var ops = (DynamicOps<T>) BLibCodecs.CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
        var result = codec.encodeStart(ops, value);

        return result.isSuccess()
            ? result.result().get()
            : null;
    }

    @Override
    public <T> Result<A, T> decode(CodecSchema<T> codecSchema, T input) {
        @SuppressWarnings("unchecked")
        var ops = (DynamicOps<T>) BLibCodecs.CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
        var result = codec.decode(ops, input);

        return result.isSuccess()
            ? Result.ok(result.result().get().getFirst())
            : Result.err(input);
    }
}
