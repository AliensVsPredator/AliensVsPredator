package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: list of pool ids authored in {@code projectName}'s datapack. Sent in response to
 * {@link C2SListPoolsPayload} and proactively after a successful delete so the content browser doesn't have to
 * re-request.
 */
public record S2CPoolListPayload(
    String projectName,
    List<ResourceLocation> poolIds
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("pool_list");

    public static final Type<S2CPoolListPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CPoolListPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CPoolListPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CPoolListPayload::poolIds,
        S2CPoolListPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
