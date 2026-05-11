package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: trigger a "Save &amp; Reload" on the named project for the named pool. Since pool edits already
 * write to disk continuously, this packet is effectively just a reload trigger from the Pool Editor's header button —
 * keeps the one-click "make my changes live" affordance without forcing users to dig into the FILE menu after every
 * tweak.
 * <p>
 * Server runs {@code EngineProjectIO.reloadProject} (ensure-selected + {@code reloadResources}) and replies via
 * {@link S2CProjectOpResultPayload} (op {@code RELOAD}). The {@code poolId} field is preserved purely so the client can
 * echo back which editor invoked the reload — server logic doesn't need it. Op-gated.
 */
public record C2SSavePoolPayload(
    String projectName,
    ResourceLocation poolId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("save_pool");

    public static final Type<C2SSavePoolPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSavePoolPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SSavePoolPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSavePoolPayload::poolId,
        C2SSavePoolPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
