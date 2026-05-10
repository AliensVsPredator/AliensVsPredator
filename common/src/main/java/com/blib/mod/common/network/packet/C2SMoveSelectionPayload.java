package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: move (or copy) the volume of blocks bounded by two corners by an integer offset. {@code copy=false}
 * is the default — the source is cleared to air and re-placed at the destination (true cut+paste). {@code copy=true}
 * (Alt-drag) leaves the source in place. Server replies via {@link S2CMoveSelectionResultPayload}.
 * <p>
 * Op-gated server-side. Volume cap mirrors the capture engine ({@code 256³} blocks) so an accidental whole-region
 * selection can't freeze the server thread.
 */
public record C2SMoveSelectionPayload(
    BlockPos cornerA,
    BlockPos cornerB,
    int dx,
    int dy,
    int dz,
    boolean copy,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("move_selection");

    public static final Type<C2SMoveSelectionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SMoveSelectionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.BLOCK_POS,
        C2SMoveSelectionPayload::cornerA,
        BLibCodecs.Stream.BLOCK_POS,
        C2SMoveSelectionPayload::cornerB,
        StreamCodecs.INT,
        C2SMoveSelectionPayload::dx,
        StreamCodecs.INT,
        C2SMoveSelectionPayload::dy,
        StreamCodecs.INT,
        C2SMoveSelectionPayload::dz,
        StreamCodecs.BOOLEAN,
        C2SMoveSelectionPayload::copy,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SMoveSelectionPayload::dimensionId,
        C2SMoveSelectionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
