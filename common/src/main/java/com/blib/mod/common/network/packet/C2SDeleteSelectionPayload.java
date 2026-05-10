package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: clear the selection's blocks to air without touching the clipboard. Distinct from Cut (which
 * additionally fills the clipboard) — Delete is "destroy this volume" with no copy. Op-gated server-side; reply not
 * sent for v1 (the panel doesn't need a confirmation since the wireframe + world state speak for themselves).
 */
public record C2SDeleteSelectionPayload(
    BlockPos cornerA,
    BlockPos cornerB,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_selection");

    public static final Type<C2SDeleteSelectionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteSelectionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SDeleteSelectionPayload::cornerA,
        BLibCodecs.Stream.BLOCK_POS,
        C2SDeleteSelectionPayload::cornerB,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeleteSelectionPayload::dimensionId,
        C2SDeleteSelectionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
