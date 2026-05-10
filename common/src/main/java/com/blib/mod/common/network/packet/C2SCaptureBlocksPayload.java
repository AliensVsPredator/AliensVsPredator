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
 * Client → server: capture a volume of blocks bounded by two corners and save it to the active project. Mode is either
 * {@code GENERAL} (single {@code .nbt} in {@code <project>/captures/}) or {@code JIGSAW} (split into ≤48³ sub-pieces
 * with auto-stitched seams + auto-generated pool in the project's datapack).
 * <p>
 * {@code dimensionId} is the level the corners are in (the picker locks corners to the same dimension; this is captured
 * at click time on the client). Op-gated server-side. Server replies via {@link S2CProjectOpResultPayload} with
 * {@code op=CAPTURE} and, for general captures, a fresh {@link S2CCaptureListPayload}; jigsaw captures additionally
 * trigger the standard project reload so the new structures + pool become live in the registry.
 */
public record C2SCaptureBlocksPayload(
    String projectName,
    String captureName,
    BlockPos cornerA,
    BlockPos cornerB,
    int modeOrdinal,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("capture_blocks");

    public static final Type<C2SCaptureBlocksPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SCaptureBlocksPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SCaptureBlocksPayload::projectName,
        StreamCodecs.STRING_UTF8,
        C2SCaptureBlocksPayload::captureName,
        BLibCodecs.Stream.BLOCK_POS,
        C2SCaptureBlocksPayload::cornerA,
        BLibCodecs.Stream.BLOCK_POS,
        C2SCaptureBlocksPayload::cornerB,
        StreamCodecs.INT,
        C2SCaptureBlocksPayload::modeOrdinal,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCaptureBlocksPayload::dimensionId,
        C2SCaptureBlocksPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
