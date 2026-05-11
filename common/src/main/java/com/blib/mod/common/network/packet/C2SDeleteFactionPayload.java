package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: delete a faction. Op-gated; server tears down the membership index, relationship table entries, and
 * shard mapping for that faction. On success the directory push fires automatically.
 */
public record C2SDeleteFactionPayload(ResourceLocation factionId) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_faction");

    public static final Type<C2SDeleteFactionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteFactionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeleteFactionPayload::factionId,
        C2SDeleteFactionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
