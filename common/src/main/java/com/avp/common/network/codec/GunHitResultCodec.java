package com.avp.common.network.codec;

import com.bvanseg.just.serialization.codec.stream.StreamCodec;
import com.bvanseg.just.serialization.codec.stream.schema.StreamCodecSchema;
import com.human.common.gameplay.item.gun.attack.GunHitResult;
import org.jetbrains.annotations.NotNull;

public class GunHitResultCodec implements StreamCodec<GunHitResult> {

    private static final int BLOCK_HIT_RESULT_CODE = 0;

    private static final int ENTITY_HIT_RESULT_CODE = 1;

    @Override
    public @NotNull <T> GunHitResult decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
        var typeCode = streamCodecSchema.readByte(input);

        return switch (typeCode) {
            case BLOCK_HIT_RESULT_CODE -> GunHitResult.Block.STREAM_CODEC.decode(streamCodecSchema, input);
            case ENTITY_HIT_RESULT_CODE -> GunHitResult.Entity.STREAM_CODEC.decode(streamCodecSchema, input);
            default -> throw new IllegalStateException("Unexpected gun hit result code: " + typeCode);
        };
    }

    @Override
    public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull GunHitResult value) {
        var typeCode = switch (value) {
            case GunHitResult.Block ignored -> BLOCK_HIT_RESULT_CODE;
            case GunHitResult.Entity ignored -> ENTITY_HIT_RESULT_CODE;
        };

        streamCodecSchema.writeByte(input, (byte) typeCode);

        switch (value) {
            case GunHitResult.Block block -> GunHitResult.Block.STREAM_CODEC.encode(streamCodecSchema, input, block);
            case GunHitResult.Entity entity -> GunHitResult.Entity.STREAM_CODEC.encode(streamCodecSchema, input, entity);
        }
    }
}
