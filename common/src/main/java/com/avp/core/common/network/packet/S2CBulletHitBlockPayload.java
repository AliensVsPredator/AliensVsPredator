package com.avp.core.common.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;

public record S2CBulletHitBlockPayload(
    BlockPos blockPos,
    Direction direction
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("bullet_hit_block");

    public static final CustomPacketPayload.Type<S2CBulletHitBlockPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, S2CBulletHitBlockPayload> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        S2CBulletHitBlockPayload::blockPos,
        Direction.STREAM_CODEC,
        S2CBulletHitBlockPayload::direction,
        S2CBulletHitBlockPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
