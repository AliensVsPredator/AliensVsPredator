package com.avp.common.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class C2SGunReloadPayload implements CustomPacketPayload {

    public static final C2SGunReloadPayload INSTANCE = new C2SGunReloadPayload();

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("gun_reload");

    public static final CustomPacketPayload.Type<C2SGunReloadPayload> TYPE = new CustomPacketPayload.Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, C2SGunReloadPayload> CODEC = StreamCodec.unit(INSTANCE);

    private C2SGunReloadPayload() {}

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
