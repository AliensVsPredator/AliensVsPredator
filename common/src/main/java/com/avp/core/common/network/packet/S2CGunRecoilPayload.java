package com.avp.core.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;

public record S2CGunRecoilPayload(
    float recoil
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("gun_recoil");

    public static final Type<S2CGunRecoilPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, S2CGunRecoilPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        S2CGunRecoilPayload::recoil,
        S2CGunRecoilPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
