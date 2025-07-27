package com.avp.common.network.packet;

import com.bvanseg.just.serialization.codec.stream.RecordStreamCodec;
import com.bvanseg.just.serialization.codec.stream.StreamCodec;
import com.human.common.gameplay.item.gun.attack.GunHitResult;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.AVPResources;

public record C2SGunHitResultsPayload(
    List<GunHitResult> gunHitResults
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = AVPResources.location("gun_hit_results");

    public static final Type<C2SGunHitResultsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SGunHitResultsPayload> CODEC = RecordStreamCodec.of(
        GunHitResult.LIST_STREAM_CODEC,
        C2SGunHitResultsPayload::gunHitResults,
        C2SGunHitResultsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
