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
 * Client → server: place the structure template identified by {@code templateId} at world position {@code anchor}.
 * Triggered from the engine workspace's piece-palette flow; the server validates op permissions, looks up the template
 * by id, and runs {@code placeInWorld} with default settings. {@code rotationOrdinal} / {@code mirrorOrdinal} are
 * placeholders for the next iteration — the MVP always sends {@code 0} ({@code Rotation.NONE} / {@code Mirror.NONE}).
 */
public record C2SPlaceJigsawPiecePayload(
    ResourceLocation templateId,
    BlockPos anchor,
    int rotationOrdinal,
    int mirrorOrdinal
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("place_jigsaw_piece");

    public static final Type<C2SPlaceJigsawPiecePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SPlaceJigsawPiecePayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SPlaceJigsawPiecePayload::templateId,
        BLibCodecs.Stream.BLOCK_POS,
        C2SPlaceJigsawPiecePayload::anchor,
        StreamCodecs.INT,
        C2SPlaceJigsawPiecePayload::rotationOrdinal,
        StreamCodecs.INT,
        C2SPlaceJigsawPiecePayload::mirrorOrdinal,
        C2SPlaceJigsawPiecePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
