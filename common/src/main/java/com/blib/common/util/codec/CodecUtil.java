package com.blib.common.util.codec;

import com.blib.common.util.codec.schema.CodecSchemas;
import com.just.codec.schema.CodecSchema;
import com.just.core.functional.result.Result;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtOps;

import java.util.Map;

public class CodecUtil {

    private static final Map<CodecSchema<?>, DynamicOps<?>> CODEC_SCHEMA_TO_DYNAMIC_OPS = Map.of(CodecSchemas.NBT, NbtOps.INSTANCE);

    private static final Map<DynamicOps<?>, CodecSchema<?>> DYNAMIC_OPS_TO_CODEC_SCHEMA = Map.of(NbtOps.INSTANCE, CodecSchemas.NBT);

    public static <A> com.just.codec.Codec<A> adapt(com.mojang.serialization.Codec<A> codec) {
        return new com.just.codec.Codec<>() {

            @Override
            public <T> T encode(CodecSchema<T> codecSchema, A value) {
                @SuppressWarnings("unchecked")
                var ops = (DynamicOps<T>) CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
                var result = codec.encodeStart(ops, value);

                return result.isSuccess()
                    ? result.result().get()
                    : null;
            }

            @Override
            public <T> Result<A, T> decode(CodecSchema<T> codecSchema, T input) {
                @SuppressWarnings("unchecked")
                var ops = (DynamicOps<T>) CODEC_SCHEMA_TO_DYNAMIC_OPS.get(codecSchema);
                var result = codec.decode(ops, input);

                return result.isSuccess()
                    ? Result.ok(result.result().get().getFirst())
                    : Result.err(input);
            }
        };
    }

    public static <A> com.mojang.serialization.Codec<A> adapt(com.just.codec.Codec<A> codec) {
        return new Codec<>() {

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                @SuppressWarnings("unchecked")
                var codecSchema = (CodecSchema<T>) DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
                var result = codec.decode(codecSchema, input);

                return result.isOk()
                    ? DataResult.success(new Pair<>(result.unwrap(), input))
                    // FIXME:
                    : DataResult.error(() -> "");
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                @SuppressWarnings("unchecked")
                var codecSchema = (CodecSchema<T>) DYNAMIC_OPS_TO_CODEC_SCHEMA.get(ops);
                return DataResult.success(codec.encode(codecSchema, input));
            }
        };
    }
}
