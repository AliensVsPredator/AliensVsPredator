package com.avp.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.lib.common.util.codec.stream.impl.MojangStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public record S2CBulletHitBlockPayload(
    BlockPos blockPos,
    Direction direction
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("bullet_hit_block");

    public static final CustomPacketPayload.Type<S2CBulletHitBlockPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CBulletHitBlockPayload> CODEC = RecordStreamCodec.of(
        MojangStreamCodecs.BLOCK_POS,
        S2CBulletHitBlockPayload::blockPos,
        MojangStreamCodecs.DIRECTION,
        S2CBulletHitBlockPayload::direction,
        S2CBulletHitBlockPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
