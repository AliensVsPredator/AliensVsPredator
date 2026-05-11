package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

/**
 * Client → server: list authored pools under {@code projectName}'s datapack tree. Triggered by the project content
 * browser on open / refresh. Server reply lands as {@link S2CPoolListPayload}.
 */
public record C2SListPoolsPayload(String projectName) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("list_pools");

    public static final Type<C2SListPoolsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SListPoolsPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SListPoolsPayload::projectName,
        C2SListPoolsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
