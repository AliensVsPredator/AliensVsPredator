package com.avp.common.network.packet;

import com.human.common.gameplay.item.gun.attack.GunHitResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.AVPResources;
import com.avp.common.network.codec.ListStreamCodec;

public record C2SGunHitResultsPayload(
    List<GunHitResult> gunHitResults
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("gun_hit_results");

    public static final Type<C2SGunHitResultsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<FriendlyByteBuf, C2SGunHitResultsPayload> CODEC = StreamCodec.composite(
        new ListStreamCodec<>(GunHitResult.STREAM_CODEC),
        C2SGunHitResultsPayload::gunHitResults,
        C2SGunHitResultsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
